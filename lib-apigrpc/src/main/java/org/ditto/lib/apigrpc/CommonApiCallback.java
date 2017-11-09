package org.ditto.lib.apigrpc;

public interface CommonApiCallback {
        void onApiError();

        void onApiCompleted();

        void onApiReady();
    }

