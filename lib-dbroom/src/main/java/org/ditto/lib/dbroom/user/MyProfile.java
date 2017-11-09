package org.ditto.lib.dbroom.user;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;

@Entity
public class MyProfile {
    @PrimaryKey
    @NonNull
    public String accessToken;
    public String userNo;
    public String avatarUrl;
    public String name;
    public String url;
    public String company;
    public String reposUrl;
    public String blog;
    public boolean visible;
    public String latestMessage;
    public String latestLocation;
    public long created;
    public long lastUpdated;


    public MyProfile() {
    }


    private MyProfile(@NonNull String accessToken, String userNo, String avatarUrl, String name, String url, String company, String reposUrl, String blog, boolean visible, String latestMessage, String latestLocation, long created, long lastUpdated) {
        this.accessToken = accessToken;
        this.userNo = userNo;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.url = url;
        this.company = company;
        this.reposUrl = reposUrl;
        this.blog = blog;
        this.visible = visible;
        this.latestMessage = latestMessage;
        this.latestLocation = latestLocation;
        this.created = created;
        this.lastUpdated = lastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String accessToken;
        private String userNo;
        private String avatarUrl;
        private String name;
        private String url;
        private String company;
        private String reposUrl;
        private String blog;
        private boolean visible;
        private String latestMessage;
        private String latestLocation;
        private long created;
        private long lastUpdated;


        Builder() {
        }

        public MyProfile build() {
            String missing = "";

            if (Strings.isNullOrEmpty(accessToken)) {
                missing += " accessToken";
            }

            if (Strings.isNullOrEmpty(name)) {
                missing += " name";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new MyProfile( accessToken,   userNo,   avatarUrl,
                      name,   url,   company,   reposUrl,   blog,
                    visible,   latestMessage,   latestLocation,   created,   lastUpdated);
        }

        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder setUserNo(String userNo) {
            this.userNo = userNo;
            return this;
        }

        public Builder setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setCompany(String company) {
            this.company = company;
            return this;
        }

        public Builder setReposUrl(String reposUrl) {
            this.reposUrl = reposUrl;
            return this;
        }

        public Builder setBlog(String blog) {
            this.blog = blog;
            return this;
        }

        public Builder setVisible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder setLatestMessage(String latestMessage) {
            this.latestMessage = latestMessage;
            return this;
        }

        public Builder setLatestLocation(String latestLocation) {
            this.latestLocation = latestLocation;
            return this;
        }

        public Builder setCreated(long created) {
            this.created = created;
            return this;
        }

        public Builder setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
    }
}