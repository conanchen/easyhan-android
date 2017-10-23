package org.ditto.feature.my.index;

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
import org.ditto.lib.repository.model.MyWordLoadRequest;
import org.ditto.lib.repository.model.MyWordRefreshRequest;
import org.ditto.lib.repository.model.WordLoadRequest;
import org.ditto.lib.repository.model.WordRefreshRequest;
import org.ditto.lib.usecases.UsecaseFascade;
import org.easyhan.common.grpc.HanziLevel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.level;

public class MyViewModel extends ViewModel {

    private final static String TAG = MyViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<MyWordLoadRequest> mutableRequest = new MutableLiveData<>();

    private final LiveData<PagedList<Word>> liveWords;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public MyViewModel() {
        liveWords = Transformations.switchMap(mutableRequest, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.wordRepository
                        .listPagedMyWordsBy(mutableRequest.getValue());
            }
        });
    }

    public LiveData<PagedList<Word>> getLiveWords() {
        return this.liveWords;
    }

    public void refresh() {
        Observable.fromCallable(() -> usecaseFascade.repositoryFascade.wordRepository.findMyWordMaxLastUpdated())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(maxLastUpdated -> {
                    usecaseFascade.repositoryFascade.wordRepository.refresh(MyWordRefreshRequest.builder().setLastUpdated(maxLastUpdated).build());
                });

    }

    public void loadPage(int page, int pageSize) {
        MyWordLoadRequest wordLoadRequest = MyWordLoadRequest.builder()
                .setPage(page)
                .setPageSize(pageSize)
                .build();
        this.mutableRequest.setValue(wordLoadRequest);
        Log.i(TAG, String.format("loadMore.myWordRefreshRequest=%s", gson.toJson(wordLoadRequest)));
    }

}