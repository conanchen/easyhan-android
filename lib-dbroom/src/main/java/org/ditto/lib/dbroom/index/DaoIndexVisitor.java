package org.ditto.lib.dbroom.index;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoIndexVisitor {

    @Insert(onConflict = REPLACE)
    Long save(IndexVisitor user);


    @Insert(onConflict = REPLACE)
    Long[] saveAll(IndexVisitor... partyIndices);


    @Insert(onConflict = REPLACE)
    List<Long> saveAll(List<IndexVisitor> partyIndices);


    @Query("SELECT * FROM IndexVisitor ORDER by lastUpdated DESC LIMIT :size")
    LiveData<List<IndexVisitor>> listPartyIndicesBy(int size);

}