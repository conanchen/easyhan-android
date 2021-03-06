package org.ditto.lib.repository.model;


import org.ditto.lib.dbroom.kv.VoAccessToken;

public class MyWordRefreshRequest {
    public VoAccessToken voAccessToken;
    public long lastUpdated;

    public MyWordRefreshRequest(VoAccessToken voAccessToken, long lastUpdated) {
        this.voAccessToken = voAccessToken;
        this.lastUpdated = lastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private VoAccessToken voAccessToken;
        private long lastUpdated;

        Builder() {
        }

        public MyWordRefreshRequest build() {
            String missing = "";


            if (voAccessToken == null) {
                missing += " voAccessToken";
            }

            if (lastUpdated < 0) {
                missing += " lastUpdated";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new MyWordRefreshRequest(voAccessToken, lastUpdated);
        }

        public Builder setVoAccessToken(VoAccessToken voAccessToken) {
            this.voAccessToken = voAccessToken;
            return this;
        }

        public Builder setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
    }


}