package org.ditto.feature.my.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ditto.feature.base.WordUtils;
import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.VoAccessToken;
import org.ditto.lib.repository.WordRepository;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyWordViewModel extends ViewModel {

    private final static String TAG = MyWordViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<Long> mutableExamRequest = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<String> mutableCheckPinyin1Request = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<String> mutableCheckPinyin2Request = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<String> mutableCheckStrokesRequest = new MutableLiveData<>();

    @VisibleForTesting
    final MutableLiveData<Boolean> mutableRequestUpsertMyWord = new MutableLiveData<Boolean>();


    private final LiveData<Word> liveExamWord;
    private final LiveData<Status> liveCheckPinyin1Status;
    private final LiveData<Status> liveCheckPinyin2Status;
    private final LiveData<Status> liveCheckStrokesStatus;

    private final LiveData<MyLiveExamWordHolder> liveExamWordHolder;

    private final LiveData<Status> liveUpsertStatus;
    @Inject
    UsecaseFascade usecaseFascade;


    @SuppressWarnings("unchecked")
    @Inject
    public MyWordViewModel() {

        liveExamWord = Transformations.switchMap(mutableExamRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return new LiveData<Word>() {
                    @Override
                    protected void onActive() {
                        usecaseFascade.repositoryFascade.wordRepository
                                .findMyExamWord().singleElement().subscribe(word -> {
                            postValue(word);
                        });
                    }
                };
            }
        });

        liveCheckPinyin1Status = Transformations.switchMap(mutableCheckPinyin1Request, pinyin -> {
            return new LiveData<Status>() {
                @Override
                protected void onActive() {
                    if (liveExamWord.getValue().pinyins.size() > 0) {
                        if (pinyin != null &&
                                pinyin.compareToIgnoreCase(liveExamWord.getValue().pinyins.get(0).pinyin) == 0) {
                            postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                        } else {
                            postValue(Status.builder().setCode(Status.Code.END_ERROR).build());
                        }
                    } else {
                        postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                    }
                }
            };
        });
        liveCheckPinyin2Status = Transformations.switchMap(mutableCheckPinyin2Request, pinyin -> {
            return new LiveData<Status>() {
                @Override
                protected void onActive() {
                    if (liveExamWord.getValue().pinyins.size() > 1) {
                        if (pinyin != null &&
                                pinyin.compareToIgnoreCase(liveExamWord.getValue().pinyins.get(1).pinyin) == 0) {
                            postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                        } else {
                            postValue(Status.builder().setCode(Status.Code.END_ERROR).build());
                        }
                    } else {
                        postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                    }
                }
            };
        });
        liveCheckStrokesStatus = Transformations.switchMap(mutableCheckStrokesRequest, inputStrokes -> {
            return new LiveData<Status>() {
                @Override
                protected void onActive() {
                    if (compareStrokes(inputStrokes, liveExamWord.getValue().strokes)) {
                        postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                    } else {
                        postValue(Status.builder().setCode(Status.Code.END_ERROR).build());
                    }
                }
            };
        });

        liveExamWordHolder = new MediatorLiveData<MyLiveExamWordHolder>() {
            {
                addSource(liveExamWord, word -> setValue(MyLiveExamWordHolder.create(word, liveCheckPinyin1Status.getValue(), liveCheckPinyin2Status.getValue(), liveCheckStrokesStatus.getValue())));
                addSource(liveCheckPinyin1Status, status -> setValue(MyLiveExamWordHolder.create(liveExamWord.getValue(), status, liveCheckPinyin2Status.getValue(), liveCheckStrokesStatus.getValue())));
                addSource(liveCheckPinyin2Status, status -> setValue(MyLiveExamWordHolder.create(liveExamWord.getValue(), liveCheckPinyin1Status.getValue(), status, liveCheckStrokesStatus.getValue())));
                addSource(liveCheckStrokesStatus, status -> setValue(MyLiveExamWordHolder.create(liveExamWord.getValue(), liveCheckPinyin1Status.getValue(), liveCheckPinyin2Status.getValue(), status)));
            }
        };


        liveUpsertStatus = Transformations.switchMap(mutableRequestUpsertMyWord, (Boolean isFlight) -> {

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
                                    Observable
                                            .just(true)
                                            .observeOn(Schedulers.io())
                                            .subscribe(aBoolean -> {
                                                usecaseFascade
                                                        .repositoryFascade
                                                        .wordRepository
                                                        .upsertMyWord(voAccessToken, liveExamWord.getValue().word, isFlight,
                                                                new WordRepository.ProgressCallback() {
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

    private boolean compareStrokes(String inputStrokes, List<String> strokes) {
        boolean result = true;
        if (inputStrokes != null && strokes != null) {
            char[] inputKeycodes = inputStrokes.toCharArray();

            if (inputKeycodes.length == strokes.size()) {
                for (int i = 0; i < inputKeycodes.length; i++) {
                    String[] strokeVal = WordUtils.STROKES.get((int) inputKeycodes[i]);
                    String[] strokeArr = StringUtils.splitByWholeSeparator(strokes.get(i), "/");
                    boolean found = false;
                    for (int j = 0; j < strokeArr.length; j++) {
                        if (strokeVal[0].compareToIgnoreCase(strokeArr[j]) == 0) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        result = false;
                        break;
                    }
                }
            } else {
                result = false;
            }
        }

        return result;
    }

    public LiveData<Status> getLiveUpsertStatus() {
        return liveUpsertStatus;
    }

    public LiveData<MyLiveExamWordHolder> getLiveExamWordHolder() {
        return liveExamWordHolder;
    }

    public void nextExamWord() {
        mutableExamRequest.setValue(System.currentTimeMillis());
    }


    public void checkPinyin1(String s) {
        mutableCheckPinyin1Request.setValue(s);
    }

    public void checkPinyin2(String s) {
        mutableCheckPinyin2Request.setValue(s);
    }

    public void checkStrokes(String s) {
        mutableCheckStrokesRequest.setValue(s);
    }

    public void updateMyWordProgress(Boolean isFlight) {
        mutableRequestUpsertMyWord.setValue(isFlight);
    }
}