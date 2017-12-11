package org.ditto.feature.word.slide;

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
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.repository.model.WordSlidesLoadRequest;
import org.ditto.lib.usecases.UsecaseFascade;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WordSlideViewModel extends ViewModel {

    private final static String TAG = WordSlideViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<WordSlidesLoadRequest> mutableLoadRequest = new MutableLiveData<>();
    private final LiveData<List<Word>> liveWords;
    private final LiveData<Word> liveDefaultWord;
    private final LiveData<WordSlideHolder> liveWordSlides;



    @Inject
    UsecaseFascade usecaseFascade;
    private String defaultWord;

    @SuppressWarnings("unchecked")
    @Inject
    public WordSlideViewModel() {

        liveDefaultWord = Transformations.switchMap(mutableLoadRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.wordRepository.find(mutableLoadRequest.getValue().defaultWord);
            }
        });

        liveWords = Transformations.switchMap(mutableLoadRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.wordRepository
                        .listWordSlidesBy(mutableLoadRequest.getValue());
            }
        });

        liveWordSlides = new MediatorLiveData<WordSlideHolder>() {
            {
                addSource(liveDefaultWord, word -> setValue(WordSlideHolder.create(word, liveWords.getValue())));
                addSource(liveWords, words -> setValue(WordSlideHolder.create(liveDefaultWord.getValue(), words)));
            }
        };
    }


    public LiveData<WordSlideHolder> getLiveWordSlides() {
        return liveWordSlides;
    }

    public void setDefaultWord(String defaultWord) {
        getMyWordSortType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wordSortType -> {
                    WordSlidesLoadRequest wordLoadRequest = WordSlidesLoadRequest
                            .builder()
                            .setDefaultWord(defaultWord)
                            .setSortType(wordSortType)
                            .setPage(0)
                            .setPageSize(20)
                            .build();
                    mutableLoadRequest.postValue(wordLoadRequest);
                    Log.i(TAG, String.format("setDefaultWord=%s", gson.toJson(wordLoadRequest)));
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