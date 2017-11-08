package org.ditto.feature.my.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.Value;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyViewModel extends ViewModel {

    private final static String TAG = MyViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<Long> mutableRefreshRequest = new MutableLiveData<>();


    private final LiveData<VoWordSortType.WordSortType> liveMyWordSortType;
    private final LiveData<KeyValue> liveMyWordLevel1Stats;
    private final LiveData<KeyValue> liveMyWordLevel2Stats;
    private final LiveData<KeyValue> liveMyWordLevel3Stats;
    private final LiveData<MyLiveDataHolder> liveMyData;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public MyViewModel() {
        liveMyWordSortType = new LiveData<VoWordSortType.WordSortType>() {
            @Override
            protected void onActive() {
                getMyWordSortType()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(wordSortType -> {
                            postValue(wordSortType);
                        });
            }
        };
        liveMyWordLevel1Stats = Transformations.switchMap(mutableRefreshRequest, login ->
                usecaseFascade.repositoryFascade.keyValueRepository.findOne(KeyValue.KEY.USER_STATS_WORD_LEVEL1));

        liveMyWordLevel2Stats = Transformations.switchMap(mutableRefreshRequest, login ->
                usecaseFascade.repositoryFascade.keyValueRepository.findOne(KeyValue.KEY.USER_STATS_WORD_LEVEL2));

        liveMyWordLevel3Stats = Transformations.switchMap(mutableRefreshRequest, login ->
                usecaseFascade.repositoryFascade.keyValueRepository.findOne(KeyValue.KEY.USER_STATS_WORD_LEVEL3));

        liveMyData = new MediatorLiveData<MyLiveDataHolder>() {
            {
                addSource(liveMyWordSortType, sortType -> setValue(MyLiveDataHolder.create(sortType
                        , liveMyWordLevel1Stats.getValue(), liveMyWordLevel2Stats.getValue(), liveMyWordLevel3Stats.getValue())));

                addSource(liveMyWordLevel1Stats, stats -> setValue(MyLiveDataHolder.create(liveMyWordSortType.getValue()
                        , stats, liveMyWordLevel2Stats.getValue(), liveMyWordLevel3Stats.getValue())));

                addSource(liveMyWordLevel2Stats, stats -> setValue(MyLiveDataHolder.create(liveMyWordSortType.getValue()
                        , liveMyWordLevel1Stats.getValue(), stats, liveMyWordLevel3Stats.getValue())));

                addSource(liveMyWordLevel3Stats, stats -> setValue(MyLiveDataHolder.create(liveMyWordSortType.getValue()
                        , liveMyWordLevel1Stats.getValue(), liveMyWordLevel2Stats.getValue(), stats)));
            }
        };
    }


    public LiveData<MyLiveDataHolder> getLiveMyData() {
        return liveMyData;
    }


    public void refresh() {
        mutableRefreshRequest.setValue(System.currentTimeMillis());
    }

    private Maybe<VoWordSortType.WordSortType> getMyWordSortType() {
        Maybe<VoWordSortType.WordSortType> dbValue = usecaseFascade.repositoryFascade.keyValueRepository
                .findMaybe(KeyValue.KEY.USER_SETTING_WORDSORTTYPE)
                .map(keyValue -> keyValue.value.voWordSortType.sortType);


        Maybe<VoWordSortType.WordSortType> defValue = Maybe.just(VoWordSortType.WordSortType.MEMORY);

        return Maybe.concat(dbValue, defValue)
                .firstElement();
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
                .observeOn(Schedulers.io())
                .subscribe();
    }
}