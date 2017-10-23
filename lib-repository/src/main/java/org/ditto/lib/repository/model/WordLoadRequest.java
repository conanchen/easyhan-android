package org.ditto.lib.repository.model;


import org.easyhan.common.grpc.HanziLevel;

public class WordLoadRequest {
    public HanziLevel level;
    public int page;
    public int pageSize;

    public WordLoadRequest() {
    }


    public WordLoadRequest(HanziLevel level, int page, int pageSize) {
        this.level = level;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private HanziLevel level;
        private int page;
        private int pageSize;

        Builder() {
        }

        public WordLoadRequest build() {
            String missing = "";

            if (level == null) {
                missing += " level";
            }


            if (page < 0) {
                missing += " page";
            }

            if (pageSize < 5) {
                missing += " pageSize";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new WordLoadRequest(level,page, pageSize);
        }

        public Builder setLevel(HanziLevel level) {
            this.level = level;
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