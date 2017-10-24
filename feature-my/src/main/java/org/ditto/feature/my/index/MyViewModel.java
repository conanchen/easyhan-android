package org.ditto.feature.my.index;

import android.arch.lifecycle.LiveData;
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
import org.ditto.lib.dbroom.kv.Value;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.repository.model.MyWordLoadRequest;
import org.ditto.lib.repository.model.MyWordRefreshRequest;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyViewModel extends ViewModel {

    private final static String TAG = MyViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<MyWordLoadRequest> mutableLoadRequest = new MutableLiveData<>();

    private final LiveData<PagedList<Word>> liveMyWords;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public MyViewModel() {
        liveMyWords = Transformations.switchMap(mutableLoadRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.wordRepository
                        .listPagedMyWordsBy(mutableLoadRequest.getValue());
            }
        });
    }

    public LiveData<PagedList<Word>> getLiveMyWords() {
        return this.liveMyWords;
    }

    public void refresh() {
        Log.i(TAG, String.format("refresh()"));
        Observable.fromCallable(() -> usecaseFascade.repositoryFascade.wordRepository.findMyWordMaxLastUpdated())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(maxLastUpdated -> {
                    usecaseFascade.repositoryFascade.wordRepository.refresh(MyWordRefreshRequest.builder().setLastUpdated(maxLastUpdated).build());
                });
    }

    private int getPageSize() {
        return 493;//8105/18
    }


    public void loadPage(int page) {

        Maybe<VoWordSortType.WordSortType> dbValue = usecaseFascade.repositoryFascade.keyValueRepository
                .findMaybe(KeyValue.KEY.USER_SETTING_WORDSORTTYPE)
                .filter(keyValue -> keyValue.value != null && keyValue.value.voWordSortType != null)
                .map(keyValue -> keyValue.value.voWordSortType.sortType);

        Maybe<VoWordSortType.WordSortType> reqValue = Maybe.just(mutableLoadRequest)
                .filter(mutableLoadRequest ->
                        mutableLoadRequest.getValue() != null && mutableLoadRequest.getValue().sortType != null)
                .map(mutableLoadRequest -> mutableLoadRequest.getValue().sortType);

        Maybe<VoWordSortType.WordSortType> defValue = Maybe.just(VoWordSortType.WordSortType.SEQUENCE);

        Maybe.concat(dbValue, reqValue, defValue)
                .firstElement()
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

    public void changeWordSortType(VoWordSortType.WordSortType sortType) {
        KeyValue keyValue = KeyValue.builder()
                .setKey(KeyValue.KEY.USER_SETTING_WORDSORTTYPE)
                .setValue(Value
                        .builder()
                        .setVoWordSortType(VoWordSortType
                                .builder()
                                .setSortType(sortType)
                                .build()
                        )
                        .build())
                .build();

        usecaseFascade.repositoryFascade.keyValueRepository.save(keyValue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    int page = 0;
                    if (mutableLoadRequest.getValue() != null) {
                        page = mutableLoadRequest.getValue().page;
                    }
                    MyWordLoadRequest loadRequest = MyWordLoadRequest.builder()
                            .setSortType(sortType)
                            .setPage(page)
                            .setPageSize(getPageSize())
                            .build();
                    this.mutableLoadRequest.setValue(loadRequest);
                    Log.i(TAG, String.format("loadPage.loadRequest=%s", gson.toJson(loadRequest)));

                });
    }
}