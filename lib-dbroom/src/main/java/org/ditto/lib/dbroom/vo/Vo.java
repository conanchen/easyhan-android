package org.ditto.lib.dbroom.vo;

import android.support.v7.app.AlertDialog;

/**
 * Created by admin on 2017/7/20.
 */

public class Vo {

    public VoLocation voLocation;
    public VoNamecard voNamecard;
    public VoPhone voPhone;
    public VoSellread voSellread;
    public VoShop voShop;
    public VoText voText;
    public VoUgroup voUgroup;
    public VoUrl voUrl;
    public VoUser voUser;
    public VoVideo voVideo;

    public Vo() {
    }

    public Vo(VoLocation voLocation, VoNamecard voNamecard, VoPhone voPhone, VoSellread voSellread, VoShop voShop, VoText voText, VoUgroup voUgroup, VoUrl voUrl, VoUser voUser, VoVideo voVideo) {
        this.voLocation = voLocation;
        this.voNamecard = voNamecard;
        this.voPhone = voPhone;
        this.voSellread = voSellread;
        this.voShop = voShop;
        this.voText = voText;
        this.voUgroup = voUgroup;
        this.voUrl = voUrl;
        this.voUser = voUser;
        this.voVideo = voVideo;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private VoLocation voLocation;
        private VoNamecard voNamecard;
        private VoPhone voPhone;
        private VoSellread voSellread;
        private VoShop voShop;
        private VoText voText;
        private VoUgroup voUgroup;
        private VoUrl voUrl;
        private VoUser voUser;
        private VoVideo voVideo;

        Builder() {
        }

        public Vo build() {
            return new Vo(voLocation, voNamecard, voPhone,
                    voSellread, voShop, voText, voUgroup, voUrl, voUser, voVideo);
        }



        public Builder setVoLocation(VoLocation voLocation) {
            this.voLocation = voLocation;
            return this;
        }

        public Builder setVoNamecard(VoNamecard voNamecard) {
            this.voNamecard = voNamecard;
            return this;
        }

        public Builder setVoPhone(VoPhone voPhone) {
            this.voPhone = voPhone;
            return this;
        }

        public Builder setVoSellread(VoSellread voSellread) {
            this.voSellread = voSellread;
            return this;
        }

        public Builder setVoShop(VoShop voShop) {
            this.voShop = voShop;
            return this;
        }

        public Builder setVoText(VoText voText) {
            this.voText = voText;
            return this;
        }

        public Builder setVoUgroup(VoUgroup voUgroup) {
            this.voUgroup = voUgroup;
            return this;
        }

        public Builder setVoUrl(VoUrl voUrl) {
            this.voUrl = voUrl;
            return this;
        }

        public Builder setVoUser(VoUser voUser) {
            this.voUser = voUser;
            return this;
        }

        public Builder setVoVideo(VoVideo voVideo) {
            this.voVideo = voVideo;
            return this;
        }
    }
}
