package org.ditto.lib.apigrpc;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.gson.Gson;

import org.ditto.sigin.grpc.QQSigninRequest;
import org.ditto.sigin.grpc.SigninGrpc;
import org.ditto.sigin.grpc.SigninResponse;
import org.easyhan.myword.grpc.MyWordResponse;
import org.easyhan.myword.grpc.StatsResponse;
import org.easyhan.myword.grpc.UpsertResponse;
import org.easyhan.word.grpc.ListRequest;
import org.easyhan.word.grpc.WordGrpc;
import org.easyhan.word.grpc.WordResponse;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.CallCredentials;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.StreamObserver;

/**
 * Created by admin on 2017/9/24.
 */

@Singleton
public class SigninService {

    private final static String TAG = SigninService.class.getSimpleName();

    public interface SigninCallback extends CommonApiCallback {
        void onSignined(SigninResponse image);
    }

    private static final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(SigninService.class.getName());
    private final ManagedChannel channel;
    final HealthCheckRequest wordGrpcHealthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(WordGrpc.getServiceDescriptor().getName())
            .build();

    final HealthCheckRequest singinGrpcHealthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(SigninGrpc.getServiceDescriptor().getName())
            .build();

    @Inject
    public SigninService(final ManagedChannel channel) {
        this.channel = channel;
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);

    }

    ClientCallStreamObserver<ListRequest> listRequestStream;

    private ManagedChannel getManagedChannel() {
        return OkHttpChannelBuilder
                .forAddress(BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT)
                .usePlaintext(true)
                //                .keepAliveTime(60, TimeUnit.SECONDS)
                .build();
    }

    public void loginWithQQAccessToken(String accessToken, SigninCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("loginWithQQAccessToken connectivityState = [%s]", gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        SigninGrpc.SigninStub signinStub = SigninGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(singinGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("loginWithQQAccessToken healthStub.check onNext accessToken = [%s]", accessToken));
                            QQSigninRequest upsertRequest = QQSigninRequest.newBuilder().setAccessToken(accessToken).build();
                            signinStub.withWaitForReady().qQSignin(upsertRequest, new StreamObserver<SigninResponse>() {
                                @Override
                                public void onNext(SigninResponse value) {
                                    Log.i(TAG, String.format("loginWithQQAccessToken qQSignin.onNext SigninResponse=%s", gson.toJson(value)));
                                    callback.onSignined(value);
                                }

                                @Override
                                public void onError(Throwable t) {

                                }

                                @Override
                                public void onCompleted() {

                                }
                            });
                        } else {
                            Log.i(TAG, String.format("loginWithQQAccessToken healthStub.check onNext NOT! ServingStatus.SERVING requestWord = [%s]", accessToken));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("loginWithQQAccessToken healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("loginWithQQAccessToken healthStub.check onCompleted grpc service check health\n%s", ""));
                        callback.onApiCompleted();
                    }
                });
    }


}
