package org.ditto.lib.dbroom.index;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;

/**
 * 中文字Word列表使用的索引存储
 */
@Entity(indices = {
        @Index(value = {"level", "idx"}),
        @Index(value = {"level", "memIdxIsOverThreshold","memIdx","idx"}),
        @Index(value = {"level", "lastUpdated"})
})
public class Word {

    @PrimaryKey
    @NonNull
    public String word;
    public int idx;
    public String pinyin;
    public String level;
    public long created;
    public long lastUpdated;
    public int visitCount;
    public int memIdx;
    public int memIdxIsOverThreshold;
    public long memLastUpdated;

    public Word() {
    }

    private Word(@NonNull String word, int idx, String pinyin, String level, long created, long lastUpdated, int visitCount, int memIdx, int memIdxIsOverThreshold, long memLastUpdated) {
        this.word = word;
        this.idx = idx;
        this.pinyin = pinyin;
        this.level = level;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.visitCount = visitCount;
        this.memIdx = memIdx;
        this.memIdxIsOverThreshold = memIdxIsOverThreshold;
        this.memLastUpdated = memLastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String word;
        private int idx;
        private String pinyin;
        private String level;
        private long created;
        private long lastUpdated;
        private int visitCount;
        private int memIdx;
        private int memIdxIsOverThreshold;
        private long memLastUpdated;

        Builder() {
        }

        public Word build() {
            String missing = "";
            if (Strings.isNullOrEmpty(word)) {
                missing += " word";
            }

            if (Strings.isNullOrEmpty(level)) {
                missing += " level";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            Word wordObj = new Word(word, idx, pinyin, level, created, lastUpdated, visitCount, memIdx, memIdxIsOverThreshold, memLastUpdated);
            return wordObj;
        }

        public Builder setWord(String word) {
            this.word = word;
            return this;
        }

        public Builder setIdx(int idx) {
            this.idx = idx;
            return this;
        }

        public Builder setPinyin(String pinyin) {
            this.pinyin = pinyin;
            return this;
        }


        public Builder setLevel(String level) {
            this.level = level;
            return this;
        }

        public Builder setCreated(long created) {
            this.created = created;
            return this;
        }

        public Builder setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder setVisitCount(int visitCount) {
            this.visitCount = visitCount;
            return this;
        }

        public Builder setMemIdx(int memIdx) {
            this.memIdx = memIdx;
            return this;
        }

        public Builder setMemIdxIsOverThreshold(int memIdxIsOverThreshold) {
            this.memIdxIsOverThreshold = memIdxIsOverThreshold;
            return this;
        }

        public Builder setMemLastUpdated(long memLastUpdated) {
            this.memLastUpdated = memLastUpdated;
            return this;
        }
    }
}
