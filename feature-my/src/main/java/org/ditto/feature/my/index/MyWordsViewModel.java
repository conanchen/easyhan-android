package org.ditto.feature.my.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.VoAccessToken;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.repository.model.MyWordLoadRequest;
import org.ditto.lib.repository.model.MyWordRefreshRequest;
import org.ditto.lib.repository.model.MyWordStatsRefreshRequest;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyWordsViewModel extends ViewModel {

    private final static String TAG = MyWordsViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<MyWordLoadRequest> mutableLoadRequest = new MutableLiveData<>();


    private final LiveData<PagedList<Word>> liveMyWords;
    private final LiveData<MyLiveWordsHolder> liveMyWordsHolder;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public MyWordsViewModel() {
        liveMyWords = Transformations.switchMap(mutableLoadRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.wordRepository
                        .listPagedMyWordsBy(mutableLoadRequest.getValue());
            }
        });
        liveMyWordsHolder = new MediatorLiveData<MyLiveWordsHolder>() {
            {
                addSource(liveMyWords, words -> setValue(MyLiveWordsHolder.create(words, mutableLoadRequest.getValue())));
                addSource(mutableLoadRequest, request -> setValue(MyLiveWordsHolder.create(liveMyWords.getValue(), request)));
            }
        };

    }


    public LiveData<MyLiveWordsHolder> getLiveMyWordsHolder() {
        return liveMyWordsHolder;
    }

    public void refresh() {
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

                        usecaseFascade.repositoryFascade.wordRepository.findMyWordMaxLastUpdated()
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(word -> {
                                    MyWordRefreshRequest myWordRefreshRequest = MyWordRefreshRequest
                                            .builder()
                                            .setVoAccessToken(voAccessToken)
                                            .setLastUpdated(word.lastUpdated)
                                            .build();
                                    usecaseFascade.repositoryFascade.wordRepository
                                            .refresh(myWordRefreshRequest);
                                    MyWordStatsRefreshRequest myWordStatsRefreshRequest = MyWordStatsRefreshRequest
                                            .builder()
                                            .setVoAccessToken(voAccessToken)
                                            .build();
                                    usecaseFascade.repositoryFascade.wordRepository.refresh(myWordStatsRefreshRequest);
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
                        Log.i(TAG, String.format("onComplete hasValue=%b,Not Logined! No accesstoken,please login.", hasValue));
                    }
                });
    }

    private int getPageSize() {
        return 100;
    }


    public void loadPage(int page) {

        getMyWordSortType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wordSortType -> {
                    MyWordLoadRequest wordLoadRequest = MyWordLoadRequest.builder()
                            .setSortType(wordSortType)
                            .setPage(page)
                            .setPageSize(getPageSize())
                            .build();
                    this.mutableLoadRequest.setValue(wordLoadRequest);
                    Log.i(TAG, String.format("loadPage.MyWordLoadRequest=%s", gson.toJson(wordLoadRequest)));
                }, throwable -> {
                    Log.i(TAG, throwable.getMessage());
                });

    }

    private Maybe<VoWordSortType.WordSortType> getMyWordSortType() {
        Maybe<VoWordSortType.WordSortType> dbValue = usecaseFascade.repositoryFascade.keyValueRepository
                .findMaybe(KeyValue.KEY.USER_SETTING_WORDSORTTYPE)
                .map(keyValue -> keyValue.value.voWordSortType.sortType);
        Maybe<VoWordSortType.WordSortType> reqValue = Maybe.just(mutableLoadRequest)
                .filter(mutableLoadRequest ->
                        mutableLoadRequest.getValue() != null && mutableLoadRequest.getValue().sortType != null)
                .map(mutableLoadRequest -> mutableLoadRequest.getValue().sortType);

        Maybe<VoWordSortType.WordSortType> defValue = Maybe.just(VoWordSortType.WordSortType.MEMORY);

        return Maybe.concat(dbValue, reqValue, defValue)
                .firstElement();
    }

}