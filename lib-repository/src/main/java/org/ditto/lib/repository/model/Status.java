package org.ditto.lib.repository.model;

public class Status {
    public Code code;
    public String message;

    public enum Code {
        START,
        LOADING,
        END_SUCCESS,
        END_ERROR,
        END_DISCONNECTED,
        END_UNKNOWN
    }

    public Status() {
    }

    private Status(Code code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Code code;
        private String message;

        Builder() {
        }

        public Status build() {
            String missing = "";

            if (code == null) {
                missing += " code";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new Status(code, message);
        }

        public Builder setCode(Code code) {
            this.code = code;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }
    }

}
