package org.ditto.feature.word.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.repository.model.WordLoadRequest;
import org.ditto.lib.repository.model.WordRefreshRequest;
import org.ditto.lib.usecases.UsecaseFascade;
import org.easyhan.common.grpc.HanziLevel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class WordsViewModel extends ViewModel {

    private final static String TAG = WordsViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<WordLoadRequest> mutableRequest = new MutableLiveData<>();

    private final LiveData<PagedList<Word>> liveWords;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public WordsViewModel() {
        liveWords = Transformations.switchMap(mutableRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.wordRepository
                        .listPagedWordsBy(mutableRequest.getValue());
            }
        });
    }

    public LiveData<PagedList<Word>> getLiveWords() {
        return this.liveWords;
    }

    public void refresh(HanziLevel level) {
        Preconditions.checkNotNull(level);
        Observable.fromCallable(() -> usecaseFascade.repositoryFascade.wordRepository.findMaxLastUpdated(level))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(maxLastUpdated -> {
                    usecaseFascade.repositoryFascade.wordRepository.refresh(WordRefreshRequest.builder().setLevel(level).setLastUpdated(maxLastUpdated).build());
                });

    }

    public void loadPage(HanziLevel level, int page, int pageSize) {
        WordLoadRequest wordLoadRequest = WordLoadRequest.builder()
                .setLevel(level)
                .setPage(page)
                .setPageSize(pageSize)
                .build();
        this.mutableRequest.setValue(wordLoadRequest);
        Log.i(TAG, String.format("loadMore.wordRefreshRequest=%s", gson.toJson(wordLoadRequest)));
    }

}