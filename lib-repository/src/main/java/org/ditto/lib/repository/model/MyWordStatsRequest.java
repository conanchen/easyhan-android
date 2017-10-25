package org.ditto.lib.repository.model;


public class MyWordStatsRequest {

    public MyWordStatsRequest() {
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        Builder() {
        }

        public MyWordStatsRequest build() {
            String missing = "";

//            if (lastUpdated < 0) {
//                missing += " lastUpdated";
//            }
//
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new MyWordStatsRequest();
        }
    }
}