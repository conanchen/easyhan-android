package org.ditto.lib.repository.model;


import org.easyhan.common.grpc.HanziLevel;

public class WordRefreshRequest {
    public HanziLevel level;
    public long lastUpdated;

    public WordRefreshRequest() {
    }


    public WordRefreshRequest(HanziLevel level, long lastUpdated) {
        this.level = level;
        this.lastUpdated = lastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private HanziLevel level;
        private long lastUpdated;

        Builder() {
        }

        public WordRefreshRequest build() {
            String missing = "";

            if (level == null) {
                missing += " level";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new WordRefreshRequest(level, lastUpdated);
        }

        public Builder setLevel(HanziLevel level) {
            this.level = level;
            return this;
        }

        public Builder setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
    }


}