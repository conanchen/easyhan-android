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
    private final LiveData<Word> liveWord;

    public LiveData<Word> getLiveWord() {
        return liveWord;
    }

    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public WordViewModel() {
        liveWord = Transformations.switchMap(mutableRequestWord, (String requestWord) -> {
            Log.i(TAG,String.format("mutableRequestWord.value=%s",mutableRequestWord.getValue()));
            return usecaseFascade.repositoryFascade.wordRepository.find(requestWord);
        });


    }

    public void setWord(String word) {
        mutableRequestWord.setValue(word);
    }

}