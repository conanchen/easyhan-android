package org.ditto.lib.dbroom.kv;

/**
 * Created by admin on 2017/7/20.
 */

public class Value {

    public VoWordSummary voWordSummary;
    public VoWordSortType voWordSortType;

    public Value() {
    }

    private Value(VoWordSummary voWordSummary, VoWordSortType voWordSortType) {
        this.voWordSummary = voWordSummary;
        this.voWordSortType = voWordSortType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private VoWordSummary voWordSummary;
        private VoWordSortType voWordSortType;

        Builder() {
        }

        public Value build() {
            String missing = "";

            if (voWordSummary == null && voWordSortType == null) {
                missing += " voWordSummary|voWordSortType one must be set";
            }
            return new Value(voWordSummary, voWordSortType);
        }

        public Builder setVoWordSummary(VoWordSummary voWordSummary) {
            this.voWordSummary = voWordSummary;
            return this;
        }

        public Builder setVoWordSortType(VoWordSortType voWordSortType) {
            this.voWordSortType = voWordSortType;
            return this;
        }
    }
}
