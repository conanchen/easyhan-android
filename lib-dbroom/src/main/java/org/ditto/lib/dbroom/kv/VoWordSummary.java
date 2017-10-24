package org.ditto.lib.dbroom.kv;

public class VoWordSummary {
    public int memory7;
    public int memory6;
    public int memory5;
    public int memory4;
    public int memory3;
    public int memory2;
    public int memory1;
    public int memory0;

    public VoWordSummary() {
    }

    private VoWordSummary(int memory7, int memory6, int memory5, int memory4, int memory3, int memory2, int memory1, int memory0) {
        this.memory7 = memory7;
        this.memory6 = memory6;
        this.memory5 = memory5;
        this.memory4 = memory4;
        this.memory3 = memory3;
        this.memory2 = memory2;
        this.memory1 = memory1;
        this.memory0 = memory0;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int memory7;
        private int memory6;
        private int memory5;
        private int memory4;
        private int memory3;
        private int memory2;
        private int memory1;
        private int memory0;

        Builder() {
        }

        public VoWordSummary build() {
            String missing = "";

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new VoWordSummary(memory7, memory6, memory5, memory4, memory3, memory2, memory1, memory0);
        }

        public Builder setMemory7(int memory7) {
            this.memory7 = memory7;
            return this;
        }

        public Builder setMemory6(int memory6) {
            this.memory6 = memory6;
            return this;
        }

        public Builder setMemory5(int memory5) {
            this.memory5 = memory5;
            return this;
        }

        public Builder setMemory4(int memory4) {
            this.memory4 = memory4;
            return this;
        }

        public Builder setMemory3(int memory3) {
            this.memory3 = memory3;
            return this;
        }

        public Builder setMemory2(int memory2) {
            this.memory2 = memory2;
            return this;
        }

        public Builder setMemory1(int memory1) {
            this.memory1 = memory1;
            return this;
        }

        public Builder setMemory0(int memory0) {
            this.memory0 = memory0;
            return this;
        }
    }


}
