package org.ditto.lib.dbroom.index;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoWord {

    @Insert(onConflict = REPLACE)
    Long save(Word word);

    @Insert(onConflict = REPLACE)
    Long[] saveAll(Word... words);

    @Delete
    void delete(Word word);

    @Query("SELECT * FROM Word WHERE level = :level ORDER by idx ASC LIMIT :pageSize")
    LiveData<List<Word>> listLiveWordsBy(String level, int pageSize);

    @Query("SELECT * FROM Word WHERE level = :level ORDER by idx ASC ")
    public abstract LivePagedListProvider<Integer, Word> listLivePagedWordsBy(String level);

    @Query("SELECT * FROM Word WHERE word = :word LIMIT 1")
    LiveData<Word> findLive(String word);

    @Query("SELECT * FROM Word WHERE word = :word LIMIT 1")
    Flowable<Word> findFlowable(String word);

    @Query("SELECT * FROM Word WHERE level = :level ORDER BY lastUpdated DESC LIMIT 1")
    Word findLatestWord(String level);

    @Query("SELECT * FROM Word WHERE memIdx <> 0 ORDER by memIdx DESC ")
    public abstract LivePagedListProvider<Integer, Word> listLivePagedMyWordsBy();

    @Query("SELECT * FROM Word WHERE memIdx <> 0 ORDER BY memLastUpdated DESC LIMIT 1")
    Word findMyLatestWord();

    @Query("SELECT * FROM Word WHERE word = :word LIMIT 1")
    Word findOne(String word);
}