package org.ditto.lib.repository.model;


import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.easyhan.common.grpc.HanziLevel;

public class WordSlidesLoadRequest {
    public String defaultWord;
    public VoWordSortType.WordSortType sortType;
    public int page;
    public int pageSize;

    public WordSlidesLoadRequest() {
    }

    public WordSlidesLoadRequest(String defaultWord, VoWordSortType.WordSortType sortType, int page, int pageSize) {
        this.defaultWord = defaultWord;
        this.sortType = sortType;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String defaultWord;
        private VoWordSortType.WordSortType sortType;
        private int page;
        private int pageSize;

        Builder() {
        }

        public WordSlidesLoadRequest build() {
            String missing = "";

            if (page < 0) {
                missing += " page";
            }

            if (pageSize < 5) {
                missing += " pageSize";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new WordSlidesLoadRequest( defaultWord,sortType, page, pageSize);
        }

        public Builder setSortType(VoWordSortType.WordSortType sortType) {
            this.sortType = sortType;
            return this;
        }

        public Builder setDefaultWord(String defaultWord) {
            this.defaultWord = defaultWord;
            return this;
        }

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }
    }


}