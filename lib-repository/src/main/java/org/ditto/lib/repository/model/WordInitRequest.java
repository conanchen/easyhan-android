package org.ditto.lib.repository.model;


import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.easyhan.common.grpc.HanziLevel;

public class WordInitRequest {
    public HanziLevel level;
    public int startIdx;

    public WordInitRequest() {
    }

    private WordInitRequest(HanziLevel level, int startIdx) {
        this.level = level;
        this.startIdx = startIdx;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private HanziLevel level;
        private int startIdx;

        Builder() {
        }

        public WordInitRequest build() {
            String missing = "";

            if (level == null) {
                missing += " level";
            }




            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new WordInitRequest(level,startIdx);
        }

        public Builder setLevel(HanziLevel level) {
            this.level = level;
            return this;
        }

        public Builder setStartIdx(int startIdx) {
            this.startIdx = startIdx;
            return this;
        }
    }


}