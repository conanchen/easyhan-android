package org.ditto.lib.apigrpc;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.gson.Gson;

import org.easyhan.myword.grpc.MyWordGrpc;
import org.easyhan.myword.grpc.MyWordResponse;
import org.easyhan.myword.grpc.StatsResponse;
import org.easyhan.myword.grpc.UpsertRequest;
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
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;

/**
 * Created by admin on 2017/9/24.
 */

@Singleton
public class WordService {

    private final static String TAG = WordService.class.getSimpleName();

    public interface WordCallback extends CommonApiCallback {
        void onWordReceived(WordResponse image);
    }

    public interface MyWordCallback extends CommonApiCallback {
        void onMyWordUpserted(UpsertResponse image);

        void onMyWordReceived(MyWordResponse value);

        void onMyStatsReceived(StatsResponse value);
    }

    public interface CommonApiCallback {
        void onApiError();

        void onApiCompleted();

        void onApiReady();
    }

    private static final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(WordService.class.getName());
    private final ManagedChannel channel;
    final HealthCheckRequest wordGrpcHealthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(WordGrpc.getServiceDescriptor().getName())
            .build();

    final HealthCheckRequest myWordGrpcHealthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(MyWordGrpc.getServiceDescriptor().getName())
            .build();

    @Inject
    public WordService(final ManagedChannel channel) {
        this.channel = channel;
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);

    }

    ClientCallStreamObserver<ListRequest> listRequestStream;

    public void listWords(ListRequest listRequest, WordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("listWords connectivityState = [%s]", gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        WordGrpc.WordStub wordStub = WordGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(wordGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("listWords healthStub.check onNext listRequest = [%s]", gson.toJson(listRequest)));
                            wordStub.withWaitForReady().list(listRequest,
                                    new ListWordsStreamObserver(callback));
                        } else {
                            Log.i(TAG, String.format("listWords healthStub.check onNext NOT! ServingStatus.SERVING listRequest = [%s]", gson.toJson(listRequest)));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("listWords healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("listWords healthStub.check onCompleted grpc service check health\n%s", ""));
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

    public void upsertMyWord(String requestWord, MyWordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("listWords connectivityState = [%s]", gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        MyWordGrpc.MyWordStub myWordStub = MyWordGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(myWordGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("upsertMyWord healthStub.check onNext requestWord = [%s]", gson.toJson(requestWord)));
                            UpsertRequest upsertRequest = UpsertRequest.newBuilder().setWord(requestWord).build();
                            myWordStub.withWaitForReady().upsert(upsertRequest, new StreamObserver<UpsertResponse>() {
                                @Override
                                public void onNext(UpsertResponse value) {
                                    Log.i(TAG, String.format("upsertMyWord upsert.onNext UpsertResponse=%s", gson.toJson(value)));
                                    callback.onMyWordUpserted(value);
                                }

                                @Override
                                public void onError(Throwable t) {

                                }

                                @Override
                                public void onCompleted() {

                                }
                            });
                        } else {
                            Log.i(TAG, String.format("upsertMyWord healthStub.check onNext NOT! ServingStatus.SERVING requestWord = [%s]", gson.toJson(requestWord)));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("upsertMyWord healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("upsertMyWord healthStub.check onCompleted grpc service check health\n%s", ""));
                        callback.onApiCompleted();
                    }
                });
    }

    public void listMyWords(long requestLastUpdated, MyWordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("listMyWords BuildConfig.GRPC_SERVER_HOST=%s, BuildConfig.GRPC_SERVER_PORT=%d,connectivityState = [%s]", BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT, gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        MyWordGrpc.MyWordStub myWordStub = MyWordGrpc.newStub(channel);

        CallCredentials callCredentials = getCallCredentials("allyourbase",77650);

        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(myWordGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("listMyWords healthStub.check onNext requestLastUpdated = [%d]", requestLastUpdated));
                            org.easyhan.myword.grpc.ListRequest listRequest = org.easyhan.myword.grpc.ListRequest.newBuilder()
                                    .setLastUpdated(requestLastUpdated).build();
                            myWordStub.withWaitForReady()
                                    .withCallCredentials(callCredentials)
                                    .list(listRequest, new StreamObserver<MyWordResponse>() {
                                @Override
                                public void onNext(MyWordResponse value) {
                                    Log.i(TAG, String.format("listMyWords list.onNext MyWordResponse=%s", gson.toJson(value)));
                                    callback.onMyWordReceived(value);
                                }

                                @Override
                                public void onError(Throwable t) {

                                }

                                @Override
                                public void onCompleted() {

                                }
                            });
                        } else {
                            Log.i(TAG, String.format("listMyWords healthStub.check onNext NOT! ServingStatus.SERVING requestLastUpdated = [%l]", requestLastUpdated));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("listMyWords healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("listMyWords healthStub.check onCompleted grpc service check health\n%s", ""));
                        callback.onApiCompleted();
                    }
                });

    }

    @NonNull
    private CallCredentials getCallCredentials(String accessToken, long expires) {
        final AccessToken token = new AccessToken(accessToken, new Date(expires));
        final OAuth2Credentials oAuth2Credentials = new OAuth2Credentials() {
            @Override
            public AccessToken refreshAccessToken() throws IOException {
                return token;
            }
        };

        return MoreCallCredentials.from(oAuth2Credentials);
    }

    public void listMyStats(MyWordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("listMyStats BuildConfig.GRPC_SERVER_HOST=%s, BuildConfig.GRPC_SERVER_PORT=%d,connectivityState = [%s]", BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT, gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        MyWordGrpc.MyWordStub myWordStub = MyWordGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(myWordGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            org.easyhan.myword.grpc.StatsRequest listRequest = org.easyhan.myword.grpc.StatsRequest.newBuilder()
                                    .build();
                            myWordStub.withWaitForReady().stats(listRequest, new StreamObserver<StatsResponse>() {
                                @Override
                                public void onNext(StatsResponse value) {
                                    Log.i(TAG, String.format("listMyStats stats.onNext StatsResponse=%s", gson.toJson(value)));
                                    callback.onMyStatsReceived(value);
                                }

                                @Override
                                public void onError(Throwable t) {

                                }

                                @Override
                                public void onCompleted() {

                                }
                            });
                        } else {
                            Log.i(TAG, String.format("listMyStats healthStub.check onNext NOT! ServingStatus.SERVING "));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("listMyStats healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("listMyStats healthStub.check onCompleted grpc service check health\n%s", ""));
                        callback.onApiCompleted();
                    }
                });

    }

    private class ListWordsStreamObserver implements ClientResponseObserver<
            ListRequest, WordResponse> {


        WordCallback wordCallback;

        public ListWordsStreamObserver(WordCallback wordCallback) {
            this.wordCallback = wordCallback;
        }

        @Override
        public void beforeStart(ClientCallStreamObserver requestStream) {
            listRequestStream = requestStream;
            listRequestStream.disableAutoInboundFlowControl();

            listRequestStream.setOnReadyHandler(() -> {
                logger.info(String.format("ListWordsStreamObserver %s", "setOnReadyHandler"));
                wordCallback.onApiReady();
            });
        }

        @Override
        public void onNext(WordResponse response) {
            wordCallback.onWordReceived(response);
            listRequestStream.request(1);
            logger.info(String.format("ListWordsStreamObserver %s WordResponse=[%s]", "onNext listRequestStream.request(1)", gson.toJson(response)));
        }


        @Override
        public void onError(Throwable t) {
            logger.info(String.format("ListWordsStreamObserver %s%s", "onError", t.getMessage()));
            t.printStackTrace();
            wordCallback.onApiError();
        }

        @Override
        public void onCompleted() {
            logger.info(String.format("ListWordsStreamObserver %s", "onCompleted"));
            wordCallback.onApiCompleted();
        }
    }

}
