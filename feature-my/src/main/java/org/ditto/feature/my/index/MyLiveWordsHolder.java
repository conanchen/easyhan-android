package org.ditto.feature.my.index;

import android.arch.paging.PagedList;

import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.repository.model.MyWordLoadRequest;

public class MyLiveWordsHolder {
    public final PagedList<Word> words;
    public final MyWordLoadRequest loadRequest;

    public MyLiveWordsHolder(PagedList<Word> words, MyWordLoadRequest loadRequest) {
        this.words = words;
        this.loadRequest = loadRequest;
    }

    public static MyLiveWordsHolder create(PagedList<Word> words, MyWordLoadRequest loadRequest) {
        return new MyLiveWordsHolder(words, loadRequest);
    }
}
