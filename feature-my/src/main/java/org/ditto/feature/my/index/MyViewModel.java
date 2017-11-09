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
import org.ditto.lib.dbroom.kv.VoAccessToken;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.dbroom.user.MyProfile;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyViewModel extends ViewModel {

    private final static String TAG = MyViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<Long> mutableRefreshRequest = new MutableLiveData<>();


    private final LiveData<MyProfile> liveMyProfile;
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
        liveMyProfile = new LiveData<MyProfile>() {
            @Override
            protected void onActive() {
                usecaseFascade.repositoryFascade.userRepository.findMyAccessToken()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new MaybeObserver<VoAccessToken>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(VoAccessToken voAccessToken) {
                                usecaseFascade.repositoryFascade.userRepository
                                        .findMyProfile(voAccessToken.accessToken)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(myProfile -> {
                                            postValue(myProfile);
                                        });

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        };
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
                addSource(liveMyProfile, myProfile -> setValue(MyLiveDataHolder.create(myProfile,liveMyWordSortType.getValue()
                        , liveMyWordLevel1Stats.getValue(), liveMyWordLevel2Stats.getValue(), liveMyWordLevel3Stats.getValue())));

                addSource(liveMyWordSortType, sortType -> setValue(MyLiveDataHolder.create(liveMyProfile.getValue(),sortType
                        , liveMyWordLevel1Stats.getValue(), liveMyWordLevel2Stats.getValue(), liveMyWordLevel3Stats.getValue())));

                addSource(liveMyWordLevel1Stats, stats -> setValue(MyLiveDataHolder.create(liveMyProfile.getValue(),liveMyWordSortType.getValue()
                        , stats, liveMyWordLevel2Stats.getValue(), liveMyWordLevel3Stats.getValue())));

                addSource(liveMyWordLevel2Stats, stats -> setValue(MyLiveDataHolder.create(liveMyProfile.getValue(),liveMyWordSortType.getValue()
                        , liveMyWordLevel1Stats.getValue(), stats, liveMyWordLevel3Stats.getValue())));

                addSource(liveMyWordLevel3Stats, stats -> setValue(MyLiveDataHolder.create(liveMyProfile.getValue(),liveMyWordSortType.getValue()
                        , liveMyWordLevel1Stats.getValue(), liveMyWordLevel2Stats.getValue(), stats)));
            }
        };
    }


    public LiveData<MyLiveDataHolder> getLiveMyData() {
        return liveMyData;
    }


    public void refresh() {
        mutableRefreshRequest.setValue(System.currentTimeMillis());
        usecaseFascade.repositoryFascade.userRepository.refreshMyProfile();
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