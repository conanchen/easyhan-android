package org.ditto.lib.system;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SystemService {
    private final static String TAG = SystemService.class.getSimpleName();

    private final static Gson gson = new Gson();
    private final Context mContext;

    @Inject
    public SystemService(Context context) {
        this.mContext = context;
    }

    public LiveData<Connectivity> getConnectivity() {
        return new LiveData<Connectivity>() {
            @Override
            protected void onActive() {
                super.onActive();
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                mContext.registerReceiver(networkReceiver, filter);
            }

            @Override
            protected void onInactive() {
                super.onInactive();
                mContext.unregisterReceiver(networkReceiver);
            }

            private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
                @SuppressWarnings("deprecation")
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getExtras() != null) {
                        NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras()
                                .get(ConnectivityManager.EXTRA_NETWORK_INFO);
                        boolean isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();
                        if (isConnected) {
                            switch (activeNetwork.getType()) {
                                case ConnectivityManager.TYPE_WIFI:
                                    postValue(new Connectivity(Connectivity.Type.WIFI, true));
                                    break;
                                case ConnectivityManager.TYPE_MOBILE:
                                    postValue(new Connectivity(Connectivity.Type.MOBILE, true));
                                    break;
                            }
                        } else {
                            postValue(new Connectivity(Connectivity.Type.MOBILE, false));
                        }
                    }
                }
            };
        };
    }
}