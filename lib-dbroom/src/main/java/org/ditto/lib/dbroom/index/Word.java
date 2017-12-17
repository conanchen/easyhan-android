package org.ditto.lib.dbroom.index;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;

import java.util.List;

/**
 * 中文字Word列表使用的索引存储
 */
@Entity(indices = {
        @Index(value = {"level", "levelIdx"}),
        @Index(value = {"level", "memIdx", "levelIdx"}),
        @Index(value = {"memIdx", "levelIdx"}),
        @Index(value = {"level", "lastUpdated"})
})
public class Word {

    @PrimaryKey
    @NonNull
    public String word;
    public String level;
    public int levelIdx;
    public long created;
    public long lastUpdated;
    public int visitCount;

    public List<Pinyin> pinyins;
    public String radical;
    public String wuxing;
    public String traditional;
    public String wubi;
    public List<String> strokes;
    public List<String> strokenames;
    public Integer strokes_count;
    public String basemean;
    public String detailmean;
    public List<String> terms;
    public List<String> riddles;
    public String fanyi;
    public String bishun;
    public Boolean defined;

    public int memIdx;
    public long memLastUpdated;
    public String memBrokenStrokes;

    public Word() {
    }

    private Word(@NonNull String word, String level, int levelIdx, long created, long lastUpdated,
                 int visitCount, List<Pinyin> pinyins, String radical, String wuxing, String traditional,
                 String wubi, List<String> strokes, List<String> strokenames, Integer strokes_count,
                 String basemean, String detailmean, List<String> terms, List<String> riddles,
                 String fanyi, String bishun, Boolean defined, int memIdx,
                 long memLastUpdated,String memBrokenStrokes) {
        this.word = word;
        this.level = level;
        this.levelIdx = levelIdx;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.visitCount = visitCount;
        this.pinyins = pinyins;
        this.radical = radical;
        this.wuxing = wuxing;
        this.traditional = traditional;
        this.wubi = wubi;
        this.strokes = strokes;
        this.strokenames = strokenames;
        this.strokes_count = strokes_count;
        this.basemean = basemean;
        this.detailmean = detailmean;
        this.terms = terms;
        this.riddles = riddles;
        this.fanyi = fanyi;
        this.bishun = bishun;
        this.defined = defined;
        this.memIdx = memIdx;
        this.memLastUpdated = memLastUpdated;
        this.memBrokenStrokes = memBrokenStrokes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String word;
        private String level;
        private int levelIdx;
        private long created;
        private long lastUpdated;
        private int visitCount;

        private List<Pinyin> pinyins;
        private String radical;
        private String wuxing;
        private String traditional;
        private String wubi;
        private List<String> strokes;
        private List<String> strokenames;
        private Integer strokes_count;
        private String basemean;
        private String detailmean;
        private List<String> terms;
        private List<String> riddles;
        private String fanyi;
        private String bishun;
        private Boolean defined;

        private int memIdx;
        private long memLastUpdated;
        private String memBrokenStrokes;

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

            Word wordObj = new Word(word, level, levelIdx, created, lastUpdated, visitCount,
                    pinyins, radical, wuxing, traditional, wubi, strokes, strokenames, strokes_count,
                    basemean, detailmean, terms,
                    riddles, fanyi, bishun, defined, memIdx, memLastUpdated,memBrokenStrokes);
            return wordObj;
        }

        public Builder setWord(String word) {
            this.word = word;
            return this;
        }

        public Builder setLevel(String level) {
            this.level = level;
            return this;
        }

        public Builder setLevelIdx(int levelIdx) {
            this.levelIdx = levelIdx;
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

        public Builder setPinyins(List<Pinyin> pinyins) {
            this.pinyins = pinyins;
            return this;
        }

        public Builder setRadical(String radical) {
            this.radical = radical;
            return this;
        }

        public Builder setWuxing(String wuxing) {
            this.wuxing = wuxing;
            return this;
        }

        public Builder setTraditional(String traditional) {
            this.traditional = traditional;
            return this;
        }

        public Builder setWubi(String wubi) {
            this.wubi = wubi;
            return this;
        }

        public Builder setStrokes(List<String> strokes) {
            this.strokes = strokes;
            return this;
        }

        public Builder setStrokes_count(Integer strokes_count) {
            this.strokes_count = strokes_count;
            return this;
        }

        public Builder setBasemean(String basemean) {
            this.basemean = basemean;
            return this;
        }

        public Builder setDetailmean(String detailmean) {
            this.detailmean = detailmean;
            return this;
        }

        public Builder setTerms(List<String> terms) {
            this.terms = terms;
            return this;
        }

        public Builder setRiddles(List<String> riddles) {
            this.riddles = riddles;
            return this;
        }

        public Builder setFanyi(String fanyi) {
            this.fanyi = fanyi;
            return this;
        }

        public Builder setBishun(String bishun) {
            this.bishun = bishun;
            return this;
        }

        public Builder setMemIdx(int memIdx) {
            this.memIdx = memIdx;
            return this;
        }

        public Builder setStrokenames(List<String> strokenames) {
            this.strokenames = strokenames;
            return this;
        }

        public Builder setDefined(Boolean defined) {
            this.defined = defined;
            return this;
        }

        public Builder setMemLastUpdated(long memLastUpdated) {
            this.memLastUpdated = memLastUpdated;
            return this;
        }

        public Builder setMemBrokenStrokes(String memBrokenStrokes) {
            this.memBrokenStrokes = memBrokenStrokes;
            return this;
        }
    }
}
