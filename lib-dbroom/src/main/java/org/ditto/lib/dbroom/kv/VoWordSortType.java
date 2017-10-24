package org.ditto.lib.dbroom.kv;

public class VoWordSortType {
    public enum WordSortType {
        SEQUENCE, MEMORY, USAGE
    }

    public WordSortType sortType;


    public VoWordSortType() {
    }

    private VoWordSortType(WordSortType sortType) {
        this.sortType = sortType;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private WordSortType sortType;

        Builder() {
        }

        public VoWordSortType build() {
            String missing = "";
            if (sortType == null) {
                missing += " sortType";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new VoWordSortType(sortType);
        }

        public Builder setSortType(WordSortType sortType) {
            this.sortType = sortType;
            return this;
        }
    }


}
