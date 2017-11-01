package org.ditto.feature.word.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.VoAccessToken;
import org.ditto.lib.repository.WordRepository;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WordViewModel extends ViewModel {
    private static Gson gson = new Gson();
    private final static String TAG = WordViewModel.class.getSimpleName();
    @VisibleForTesting
    final MutableLiveData<String> mutableRequestWord = new MutableLiveData<String>();
    @VisibleForTesting
    final MutableLiveData<Long> mutableRequestUpsertMyWord = new MutableLiveData<Long>();

    private final LiveData<Word> liveWord;
    private final LiveData<Status> liveUpsertStatus;

    public LiveData<Word> getLiveWord() {
        return liveWord;
    }

    public LiveData<Status> getLiveUpsertStatus() {
        return liveUpsertStatus;
    }

    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public WordViewModel() {
        liveWord = Transformations.switchMap(mutableRequestWord, (String requestWord) -> {
            return  usecaseFascade.repositoryFascade.wordRepository.find(requestWord);
        });

        liveUpsertStatus = Transformations.switchMap(mutableRequestUpsertMyWord, (Long time) -> {

            return new LiveData<Status>() {
                @Override
                protected void onActive() {

                    usecaseFascade.repositoryFascade.keyValueRepository
                            .findMaybe(KeyValue.KEY.USER_CURRENT_ACCESSTOKEN)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .map(keyValue -> keyValue.value.voAccessToken)
                            .subscribe(new MaybeObserver<VoAccessToken>() {
                                boolean hasValue = false;

                                @Override
                                public void onSubscribe(Disposable d) {
                                    Log.i(TAG, "onSubscribe");
                                }

                                @Override
                                public void onSuccess(VoAccessToken voAccessToken) {
                                    //found accesstoken
                                    Log.i(TAG, String.format("onSuccess voAccessToken=%s", gson.toJson(voAccessToken)));
                                    hasValue = true;
                                    Observable.just(true).observeOn(Schedulers.io()).subscribe(aBoolean -> {
                                        usecaseFascade.repositoryFascade.wordRepository.upsertMyWord(voAccessToken,mutableRequestWord.getValue(), new WordRepository.ProgressCallback(){
                                            @Override
                                            public void onSucess() {

                                                postValue(Status.builder().setCode(Status.Code.END_SUCCESS).setMessage("api sucess").build());
                                            }

                                            @Override
                                            public void onError() {
                                                postValue(Status.builder().setCode(Status.Code.END_ERROR).setMessage("api error").build());
                                            }
                                        });
                                    });
                                }

                                @Override
                                public void onError(Throwable e) {
                                    //access database error
                                    Log.i(TAG, String.format("onError Throwable=%s", e.getMessage()));
                                }

                                @Override
                                public void onComplete() {
                                    //not found accesstoken
                                    Log.i(TAG, String.format("onComplete hasValue=%b", hasValue));
                                    postValue(Status.builder().setCode(Status.Code.END_NOT_LOGIN).setMessage("Not Logined! No accesstoken,please login.").build());
                                }
                            });
                }
            };
        });

    }

    public void setWord(String word){
        mutableRequestWord.setValue(word);
    }
    public void updateMyWordProgress() {
        mutableRequestUpsertMyWord.setValue(System.currentTimeMillis());
    }
}