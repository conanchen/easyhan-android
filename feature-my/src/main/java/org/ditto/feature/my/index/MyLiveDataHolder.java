package org.ditto.feature.my.index;

import android.arch.paging.PagedList;

import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.VoWordSortType;

public class MyLiveDataHolder {
    public final VoWordSortType.WordSortType wordSortType;
    public final KeyValue myWordLevel1Stats;
    public final KeyValue myWordLevel2Stats;
    public final KeyValue myWordLevel3Stats;


    public MyLiveDataHolder( VoWordSortType.WordSortType wordSortType,
                            KeyValue myWordLevel1Stats, KeyValue myWordLevel2Stats, KeyValue myWordLevel3Stats) {
        this.wordSortType = wordSortType;
        this.myWordLevel1Stats = myWordLevel1Stats;
        this.myWordLevel2Stats = myWordLevel2Stats;
        this.myWordLevel3Stats = myWordLevel3Stats;
    }

    public static MyLiveDataHolder create(VoWordSortType.WordSortType wordSortType,
                                          KeyValue myWordLevel1Stats, KeyValue myWordLevel2Stats, KeyValue myWordLevel3Stats) {
        return new MyLiveDataHolder(wordSortType, myWordLevel1Stats, myWordLevel2Stats, myWordLevel3Stats);
    }
}
