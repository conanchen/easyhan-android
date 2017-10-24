package org.ditto.lib.repository.model;


import org.ditto.lib.dbroom.kv.VoWordSortType;

public class MyWordLoadRequest {
    public VoWordSortType.WordSortType sortType;
    public int page;
    public int pageSize;

    public MyWordLoadRequest() {
    }

    public MyWordLoadRequest(VoWordSortType.WordSortType sortType,int page, int pageSize) {
        this.sortType = sortType;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private VoWordSortType.WordSortType sortType;
        private int page;
        private int pageSize;

        Builder() {
        }

        public MyWordLoadRequest build() {
            String missing = "";


            if (sortType == null) {
                missing += " sortType";
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

            return new MyWordLoadRequest(sortType,page, pageSize);
        }

        public Builder setSortType(VoWordSortType.WordSortType sortType) {
            this.sortType = sortType;
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