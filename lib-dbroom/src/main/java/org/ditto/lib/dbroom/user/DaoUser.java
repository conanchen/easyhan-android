package org.ditto.lib.dbroom.user;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import org.ditto.lib.dbroom.index.Word;

import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoUser {

    @Insert(onConflict = REPLACE)
    Long save(MyProfile myProfile);

    @Query("SELECT * FROM MyProfile WHERE accessToken = :accessToken LIMIT 1")
    LiveData<MyProfile> load(String accessToken);

    @Query("SELECT * FROM MyProfile WHERE accessToken = :accessToken LIMIT 1")
    Maybe<MyProfile> findMaybe(String accessToken);


}