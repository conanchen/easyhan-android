package org.ditto.lib.repository.model;


public class MyWordLoadRequest {
    public int page;
    public int pageSize;

    public MyWordLoadRequest() {
    }

    public MyWordLoadRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int page;
        private int pageSize;

        Builder() {
        }

        public MyWordLoadRequest build() {
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

            return new MyWordLoadRequest(page, pageSize);
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