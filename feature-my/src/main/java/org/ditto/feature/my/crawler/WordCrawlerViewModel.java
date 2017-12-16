package org.ditto.feature.my.crawler;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.repository.WordRepository;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;
import org.easyhan.common.grpc.HanziLevel;
import org.easyhan.word.HanZi;

import javax.inject.Inject;

public class WordCrawlerViewModel extends ViewModel {
    private final static String TAG = WordCrawlerViewModel.class.getSimpleName();
    private static Gson gson = new Gson();
    @VisibleForTesting
    final MutableLiveData<Integer> mutableRequestWord = new MutableLiveData<Integer>();
    @VisibleForTesting
    final MutableLiveData<String> mutableSaveRequest = new MutableLiveData<String>();
    private final LiveData<Word> liveWord;
    private final LiveData<Status> liveSaveStatus;
    @Inject
    UsecaseFascade usecaseFascade;


    @SuppressWarnings("unchecked")
    @Inject
    public WordCrawlerViewModel() {
        liveWord = Transformations.switchMap(mutableRequestWord, (Integer requestIdx) -> {
            Log.i(TAG, String.format("mutableRequestWord.value=%s", mutableRequestWord.getValue()));
            return new LiveData<Word>() {
                @Override
                protected void onActive() {
                    int mIdx = requestIdx;
                    if(requestIdx<1 || requestIdx>HanZi.LEVEL1.length+HanZi.LEVEL2.length+HanZi.LEVEL3.length){
                        mIdx = 1;
                    }

                    Word word ;
                    if (mIdx > HanZi.LEVEL1.length + HanZi.LEVEL2.length) {
                        word = Word.builder().setWord(HanZi.LEVEL3[mIdx - HanZi.LEVEL1.length - HanZi.LEVEL2.length - 1]).setLevel(HanziLevel.THREE.name()).setLevelIdx(mIdx).build();
                    } else if (mIdx > HanZi.LEVEL1.length) {
                        word = Word.builder().setWord(HanZi.LEVEL2[mIdx - HanZi.LEVEL1.length - 1]).setLevel(HanziLevel.TWO.name()).setLevelIdx(mIdx).build();
                    } else {
                        word = Word.builder().setWord(HanZi.LEVEL1[mIdx - 1]).setLevel(HanziLevel.ONE.name()).setLevelIdx(mIdx).build();
                    }
                    postValue(word);
                }
            };
        });


        liveSaveStatus = Transformations.switchMap(mutableSaveRequest, html -> {
            return new LiveData<Status>() {
                @Override
                protected void onActive() {
                    usecaseFascade.repositoryFascade.wordRepository.updateWordFromBaidu(liveWord.getValue().word, html,
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
                }
            };
        });
    }

    public LiveData<Word> getLiveWord() {
        return liveWord;
    }

    public LiveData<Status> getLiveSaveStatus() {
        return liveSaveStatus;
    }

    public void setWordIdx(int wordIdx) {
        mutableRequestWord.setValue(wordIdx);
    }

    public void saveParsedWord(String results) {
        mutableSaveRequest.postValue(results);
    }

}