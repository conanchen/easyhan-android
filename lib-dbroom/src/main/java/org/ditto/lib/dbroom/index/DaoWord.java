package org.ditto.lib.dbroom.index;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoWord {

    @Insert(onConflict = REPLACE)
    Long save(Word word);

    @Insert(onConflict = REPLACE)
    Long[] saveAll(Word... words);

    @Delete
    void delete(Word word);

    @Query("SELECT * FROM Word WHERE level = :level ORDER by levelIdx ASC ")
    public abstract LivePagedListProvider<Integer, Word> listLivePagedWordsOrderByIdx(String level);

    @Query("SELECT * FROM Word WHERE level = :level ORDER by memIdxIsOverThreshold ASC, memIdx DESC ")
    public abstract LivePagedListProvider<Integer, Word> listLivePagedWordsOrderByMemIdx(String level);

    @Query("SELECT * FROM Word WHERE word = :word LIMIT 1")
    LiveData<Word> findLive(String word);

    @Query("SELECT * FROM Word WHERE word = :word LIMIT 1")
    Maybe<Word> findMaybe(String word);

    @Query("SELECT * FROM Word WHERE level = :level ORDER BY lastUpdated DESC LIMIT 1")
    Word findLatestWord(String level);

    @Query("SELECT * FROM Word ORDER by levelIdx ASC ")
    public abstract LivePagedListProvider<Integer, Word> listLivePagedMyWordsOrderByIdx();

    @Query("SELECT * FROM Word ORDER by memIdxIsOverThreshold ASC, memIdx DESC, levelIdx ASC ")
    public abstract LivePagedListProvider<Integer, Word> listLivePagedMyWordsOrderByMemIdx();

    @Query("SELECT * FROM Word WHERE memIdx <> 0 ORDER BY memLastUpdated DESC LIMIT 1")
    Maybe<Word> findMyLatestWord();

    @Query("SELECT * FROM Word WHERE word = :word LIMIT 1")
    Word findOne(String word);

    @Query("SELECT * FROM Word ORDER by memIdxIsOverThreshold ASC, memIdx DESC, levelIdx ASC LIMIT :size")
    Flowable<List<Word>> getLiveMyExamWords(Integer size);

    @Query("SELECT * FROM Word WHERE levelIdx = :wordIdx LIMIT 1")
    Maybe<Word> findOneByIdx(Integer wordIdx);
}