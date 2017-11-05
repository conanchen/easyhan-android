package org.ditto.lib.repository.model;


import org.ditto.lib.dbroom.kv.VoAccessToken;

public class MyWordStatsRefreshRequest {
    public VoAccessToken voAccessToken;

    public MyWordStatsRefreshRequest(VoAccessToken voAccessToken) {
        this.voAccessToken = voAccessToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private VoAccessToken voAccessToken;

        Builder() {
        }

        public MyWordStatsRefreshRequest build() {
            String missing = "";


            if (voAccessToken == null) {
                missing += " voAccessToken";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new MyWordStatsRefreshRequest(voAccessToken);
        }

        public Builder setVoAccessToken(VoAccessToken voAccessToken) {
            this.voAccessToken = voAccessToken;
            return this;
        }
    }
}