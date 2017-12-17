package org.ditto.lib.apigrpc;


import android.util.Log;

import com.google.gson.Gson;

import org.easyhan.myprofile.grpc.GetRequest;
import org.easyhan.myprofile.grpc.MyProfileGrpc;
import org.easyhan.myprofile.grpc.MyProfileResponse;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Singleton;

import io.grpc.CallCredentials;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Created by admin on 2017/9/24.
 */

@Singleton
public class MyProfileService {

    private final static String TAG = MyProfileService.class.getSimpleName();


    public interface MyProfileCallback extends CommonApiCallback {

        void onMyProfileReceived(MyProfileResponse value);

    }

    private static final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(MyProfileService.class.getName());
    final HealthCheckRequest myProfileGrpcHealthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(MyProfileGrpc.getServiceDescriptor().getName())
            .build();



    public void getMyProfile(CallCredentials callCredentials, String requestWord, MyProfileCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("getMyProfile connectivityState = [%s]", gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        MyProfileGrpc.MyProfileStub myProfileStub = MyProfileGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(myProfileGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("getMyProfile healthStub.check onNext requestWord = [%s]", gson.toJson(requestWord)));
                            GetRequest getRequest = GetRequest.getDefaultInstance().newBuilder().setLastUpdated(System.currentTimeMillis()).build();
                            myProfileStub.withWaitForReady().withCallCredentials(callCredentials).get(getRequest, new StreamObserver<MyProfileResponse>() {
                                @Override
                                public void onNext(MyProfileResponse value) {
                                    Log.i(TAG, String.format("getMyProfile get.onNext MyProfileResponse=%s", gson.toJson(value)));
                                    callback.onMyProfileReceived(value);
                                }

                                @Override
                                public void onError(Throwable t) {

                                }

                                @Override
                                public void onCompleted() {

                                }
                            });
                        } else {
                            Log.i(TAG, String.format("getMyProfile healthStub.check onNext NOT! ServingStatus.SERVING requestWord = [%s]", gson.toJson(requestWord)));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("getMyProfile healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("getMyProfile healthStub.check onCompleted grpc service check health\n%s", ""));
                        callback.onApiCompleted();
                    }
                });
    }

    private ManagedChannel getManagedChannel() {
        return OkHttpChannelBuilder
                .forAddress(BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT)
                .usePlaintext(true)
                //                .keepAliveTime(60, TimeUnit.SECONDS)
                .build();
    }
}
