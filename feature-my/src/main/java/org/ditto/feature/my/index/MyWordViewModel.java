package org.ditto.feature.my.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

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

    private final LiveData<Word> liveExamWord;
    private final LiveData<Status> liveCheckPinyin1Status;
    private final LiveData<Status> liveCheckPinyin2Status;
    private final LiveData<Status> liveCheckStrokesStatus;

    private final LiveData<MyLiveExamWordHolder> liveExamWordHolder;

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
                    if (pinyin != null && pinyin.compareToIgnoreCase(liveExamWord.getValue().pinyin1) == 0) {
                        postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                    } else {
                        postValue(Status.builder().setCode(Status.Code.END_ERROR).build());
                    }
                }
            };
        });
        liveCheckPinyin2Status = Transformations.switchMap(mutableCheckPinyin2Request, pinyin -> {
            return new LiveData<Status>() {
                @Override
                protected void onActive() {
                    if (pinyin != null && pinyin.compareToIgnoreCase(liveExamWord.getValue().pinyin2) == 0) {
                        postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                    } else {
                        postValue(Status.builder().setCode(Status.Code.END_ERROR).build());
                    }
                }
            };
        });
        liveCheckStrokesStatus = Transformations.switchMap(mutableCheckStrokesRequest, strokes -> {
            return new LiveData<Status>() {
                @Override
                protected void onActive() {
                    if (strokes != null && strokes.compareToIgnoreCase(liveExamWord.getValue().strokes) == 0) {
                        postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                    } else {
                        postValue(Status.builder().setCode(Status.Code.END_ERROR).build());
                    }
                }
            };
        });

        liveExamWordHolder = new MediatorLiveData<MyLiveExamWordHolder>() {
            {
                addSource(liveExamWord, word -> setValue(MyLiveExamWordHolder.create(word, liveCheckPinyin1Status.getValue(),liveCheckPinyin2Status.getValue(),liveCheckStrokesStatus.getValue())));
                addSource(liveCheckPinyin1Status, status -> setValue(MyLiveExamWordHolder.create(liveExamWord.getValue(), status,liveCheckPinyin2Status.getValue(),liveCheckStrokesStatus.getValue())));
                addSource(liveCheckPinyin2Status, status -> setValue(MyLiveExamWordHolder.create(liveExamWord.getValue(), liveCheckPinyin1Status.getValue(),status,liveCheckStrokesStatus.getValue())));
                addSource(liveCheckStrokesStatus , status -> setValue(MyLiveExamWordHolder.create(liveExamWord.getValue(), liveCheckPinyin1Status.getValue(),liveCheckPinyin2Status.getValue(),status)));
            }
        };

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
}