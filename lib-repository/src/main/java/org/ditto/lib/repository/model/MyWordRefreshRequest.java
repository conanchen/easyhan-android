package org.ditto.lib.repository.model;


import org.easyhan.common.grpc.HanziLevel;

public class MyWordRefreshRequest {
    public long lastUpdated;

    public MyWordRefreshRequest() {
    }


    public MyWordRefreshRequest(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private long lastUpdated;

        Builder() {
        }

        public MyWordRefreshRequest build() {
            String missing = "";


            if (lastUpdated < 0) {
                missing += " lastUpdated";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new MyWordRefreshRequest(lastUpdated);
        }

        public Builder setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
    }


}