package org.ditto.feature.my.crawler;

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

import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WordCrawlerViewModel extends ViewModel {
    private static Gson gson = new Gson();
    private final static String TAG = WordCrawlerViewModel.class.getSimpleName();
    @VisibleForTesting
    final MutableLiveData<Integer> mutableRequestWord = new MutableLiveData<Integer>();
    private final LiveData<Word> liveWord;

    public LiveData<Word> getLiveWord() {
        return liveWord;
    }

    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public WordCrawlerViewModel() {
        liveWord = Transformations.switchMap(mutableRequestWord, (Integer requestWord) -> {
            Log.i(TAG, String.format("mutableRequestWord.value=%s", mutableRequestWord.getValue()));
            return new LiveData<Word>() {
                @Override
                protected void onActive() {
                    usecaseFascade.repositoryFascade.wordRepository
                            .findByIdx(requestWord)
                            .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<Word>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Word word) {
                            postValue(word);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
            } ;
        });


    }

    public void setWordIdx(int wordIdx) {
        mutableRequestWord.setValue(wordIdx);
    }
}