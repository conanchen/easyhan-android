package org.ditto.feature.word.slide;

import org.ditto.lib.dbroom.index.Word;

import java.util.List;

public class WordSlideHolder {
    public final Word defaultWord;
    public final List<Word> words;

    public WordSlideHolder(Word defaultWord, List<Word> words) {
        this.defaultWord = defaultWord;
        this.words = words;
    }

    public static WordSlideHolder create(Word defaultWord, List<Word> words) {
        return new WordSlideHolder(defaultWord, words);
    }
}
