package org.ditto.lib.apigrpc.di;

import android.util.Log;

import org.ditto.lib.apigrpc.BuildConfig;
import org.ditto.lib.apigrpc.SigninService;
import org.ditto.lib.apigrpc.WordService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.grpc.ManagedChannel;
import io.grpc.okhttp.OkHttpChannelBuilder;

/**
 * Created by amirziarati on 10/4/16.
 */
@Singleton
@Module
public class ApigrpcModule {
    private final static String TAG = ApigrpcModule.class.getSimpleName();


    @Singleton
    @Provides
    ManagedChannel provideManagedChannel() {
        final ManagedChannel channel = OkHttpChannelBuilder
                .forAddress(BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT)
                .usePlaintext(true)
//                .keepAliveTime(60, TimeUnit.SECONDS)
                .build();
        Log.i(TAG, String.format("Prepare ManagedChannel GRPC_SERVER_HOST=%s,GRPC_SERVER_PORT=%d", BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT));
        return channel;
    }

    @Singleton
    @Provides
    WordService provideWordService(final ManagedChannel channel) {
        return new WordService(channel);
    }

    @Singleton
    @Provides
    SigninService provideSigninService(final ManagedChannel channel) {
        return new SigninService(channel);
    }

}