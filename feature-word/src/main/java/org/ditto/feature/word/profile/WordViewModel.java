package org.ditto.feature.word.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

public class WordViewModel extends ViewModel {
    private final static String TAG = WordViewModel.class.getSimpleName();
    private static Gson gson = new Gson();
    @VisibleForTesting
    final MutableLiveData<String> mutableRequestWord = new MutableLiveData<String>();
    private final LiveData<Word> liveWord;
    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public WordViewModel() {
        liveWord = Transformations.switchMap(mutableRequestWord, (String requestWord) -> {
            return new LiveData<Word>() {
                @Override
                protected void onActive() {
                    Log.i(TAG, String.format("mutableRequestWord.value=%s", mutableRequestWord.getValue()));
                    usecaseFascade.repositoryFascade.wordRepository
                            .findMaybe(requestWord)
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                            .subscribe(word -> {
                                postValue(word);
                            })
                    ;
                }
            };
        });


    }

    public LiveData<Word> getLiveWord() {
        return liveWord;
    }

    public void setWord(String word) {
        mutableRequestWord.setValue(word);
    }

    public void download(String mWord) {
usecaseFascade.repositoryFascade.wordRepository.download(mWord);
    }
}