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
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.repository.model.WordLoadRequest;
import org.ditto.lib.repository.model.WordRefreshRequest;
import org.ditto.lib.usecases.UsecaseFascade;
import org.easyhan.common.grpc.HanziLevel;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WordsViewModel extends ViewModel {

    private final static String TAG = WordsViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<WordLoadRequest> mutableLoadRequest = new MutableLiveData<>();

    private final LiveData<PagedList<Word>> liveWords;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public WordsViewModel() {
        liveWords = Transformations.switchMap(mutableLoadRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.wordRepository
                        .listPagedWordsBy(mutableLoadRequest.getValue());
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
    private int getPageSize(HanziLevel level) {
        switch (level) {
            case ONE:
                return 195;//3500/18;
            case TWO:
                return 168;//3000/18;
            case THREE:
            default:
                return 87;//1605/18;
        }
    }


    public void loadPage(HanziLevel level, int page) {

        Maybe<VoWordSortType.WordSortType> dbValue = usecaseFascade.repositoryFascade.keyValueRepository
                .findMaybe(KeyValue.KEY.USER_SETTING_WORDSORTTYPE)
                .map(keyValue -> keyValue.value.voWordSortType.sortType);

        Maybe<VoWordSortType.WordSortType> reqValue = Maybe.just(mutableLoadRequest)
                .filter(mutableLoadRequest ->
                        mutableLoadRequest.getValue() != null && mutableLoadRequest.getValue().sortType != null)
                .map(mutableLoadRequest -> mutableLoadRequest.getValue().sortType);

        Maybe<VoWordSortType.WordSortType> defValue = Maybe.just(VoWordSortType.WordSortType.MEMORY);

        Maybe.concat(dbValue, reqValue, defValue)
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wordSortType -> {
                    WordLoadRequest wordLoadRequest = WordLoadRequest.builder()
                            .setLevel(level)
                            .setSortType(wordSortType)
                            .setPage(page)
                            .setPageSize(getPageSize(level))
                            .build();
                    this.mutableLoadRequest.setValue(wordLoadRequest);
                    Log.i(TAG, String.format("loadPage.WordLoadRequest=%s", gson.toJson(wordLoadRequest)));
                }, throwable -> {
                    Log.i(TAG, throwable.getMessage());
                });
    }

}