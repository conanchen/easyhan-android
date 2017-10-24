package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;

import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.kv.KeyValue;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository that handles VoUser objects.
 */
@Singleton
public class KeyValueRepository {

    private RoomFascade roomFascade;

    @Inject
    public KeyValueRepository(RoomFascade roomFascade) {
        this.roomFascade = roomFascade;
    }


    public LiveData<KeyValue> findOne(String key) {
        return roomFascade.daoKeyValue.loadLiveOne(key);
    }

    public Maybe<KeyValue> findMaybe(String key) {
        return roomFascade.daoKeyValue.loadMaybe(key);
    }

    public Observable<Long> save(KeyValue keyValue) {
        return Observable.fromCallable(
                () -> roomFascade.daoKeyValue.save(keyValue)
        ).subscribeOn(Schedulers.io());
    }

}