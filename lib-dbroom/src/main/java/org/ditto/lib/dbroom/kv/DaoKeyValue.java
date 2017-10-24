package org.ditto.lib.dbroom.kv;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoKeyValue {

    @Insert(onConflict = REPLACE)
    Long save(KeyValue keyValue);

    @Insert(onConflict = REPLACE)
    List<Long>  saveAll(List<KeyValue> keyValues);

    @Query("SELECT * FROM KeyValue WHERE key = :key")
    LiveData<KeyValue> loadLiveOne(String key);

    @Query("SELECT * FROM KeyValue WHERE key = :key")
    Maybe<KeyValue> loadMaybe(String key);

}