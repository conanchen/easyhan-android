package org.ditto.lib.apigrpc;


import android.util.Log;

import com.google.gson.Gson;

import org.easyhan.common.grpc.StatusResponse;
import org.easyhan.myword.grpc.MyWordGrpc;
import org.easyhan.myword.grpc.MyWordResponse;
import org.easyhan.myword.grpc.StatsResponse;
import org.easyhan.myword.grpc.UpsertRequest;
import org.easyhan.word.grpc.GetRequest;
import org.easyhan.word.grpc.ListRequest;
import org.easyhan.word.grpc.UpdateRequest;
import org.easyhan.word.grpc.WordGrpc;
import org.easyhan.word.grpc.WordResponse;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.CallCredentials;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
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
    private static final Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(WordService.class.getName());
    final HealthCheckRequest wordGrpcHealthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(WordGrpc.getServiceDescriptor().getName())
            .build();
    final HealthCheckRequest myWordGrpcHealthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(MyWordGrpc.getServiceDescriptor().getName())
            .build();
    private final ManagedChannel channel;
    ClientCallStreamObserver<ListRequest> listRequestStream;

    @Inject
    public WordService(final ManagedChannel channel) {
        this.channel = channel;
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);

    }

    public void updateWordFromBaidu(String word, String html, WordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("updateWord connectivityState = [%s]", gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        WordGrpc.WordStub wordStub = WordGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(wordGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("updateWord healthStub.check onNext word = [%s]", word));
                            wordStub.withWaitForReady().update(UpdateRequest.newBuilder().setWord(word).setHtml(html).build(), new StreamObserver<StatusResponse>() {
                                @Override
                                public void onNext(StatusResponse statusResponse) {
                                    callback.onWordUpdated(statusResponse);

                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    callback.onApiError();
                                }

                                @Override
                                public void onCompleted() {
                                    callback.onApiCompleted();
                                }
                            });
                        } else {
                            Log.i(TAG, String.format("updateWord healthStub.check onNext NOT! ServingStatus.SERVING word = [%s]", word));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("updateWord healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("updateWord healthStub.check onCompleted grpc service check health\n%s", ""));
                        callback.onApiCompleted();
                    }
                });
    }

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

    public void download(String word, WordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("download connectivityState = [%s]", gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        WordGrpc.WordStub wordStub = WordGrpc.newStub(channel);
        GetRequest getRequest = GetRequest.newBuilder().setWord(word).build();
        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(wordGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("download healthStub.check onNext getRequest = [%s]", gson.toJson(getRequest)));
                            wordStub.withWaitForReady().get(getRequest,
                                    new StreamObserver<WordResponse>() {
                                        @Override
                                        public void onNext(WordResponse wordResponse) {
                                            Log.i(TAG, String.format("download  onNext wordResponse = [%s]", gson.toJson(wordResponse)));
                                            callback.onWordReceived(wordResponse);
                                        }

                                        @Override
                                        public void onError(Throwable throwable) {

                                        }

                                        @Override
                                        public void onCompleted() {

                                        }
                                    });
                        } else {
                            Log.i(TAG, String.format("download healthStub.check onNext NOT! ServingStatus.SERVING getRequest = [%s]", gson.toJson(getRequest)));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("download healthStub.check onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("download healthStub.check onCompleted grpc service check health\n%s", ""));
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

    public void upsertMyWord(CallCredentials callCredentials, String requestWord, Boolean isFlight, Boolean updateMemStrokes, String memStrokes, MyWordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("upsertMyWord connectivityState = [%s]", gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        MyWordGrpc.MyWordStub myWordStub = MyWordGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS).check(myWordGrpcHealthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("upsertMyWord healthStub.check onNext requestWord = [%s]", gson.toJson(requestWord)));
                            UpsertRequest upsertRequest = UpsertRequest
                                    .newBuilder()
                                    .setWord(requestWord)
                                    .setProgressStep(isFlight ? 8 : 1)
                                    .setUpdateMemStrokes(updateMemStrokes)
                                    .setMemStrokes(!updateMemStrokes || memStrokes == null ? "" : memStrokes == null ? "" : memStrokes)
                                    .build();

                            myWordStub
                                    .withWaitForReady()
                                    .withCallCredentials(callCredentials)
                                    .upsert(upsertRequest, new StreamObserver<MyWordResponse>() {
                                        @Override
                                        public void onNext(MyWordResponse value) {
                                            Log.i(TAG, String.format("upsertMyWord upsert.onNext UpsertResponse=%s", gson.toJson(value)));
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

    public void listMyWords(CallCredentials callCredentials, org.easyhan.myword.grpc.ListRequest listRequest, MyWordCallback callback) {
        callback.onApiReady();
        ManagedChannel channel = getManagedChannel();

        ConnectivityState connectivityState = channel.getState(true);
        Log.i(TAG, String.format("listMyWords BuildConfig.GRPC_SERVER_HOST=%s, BuildConfig.GRPC_SERVER_PORT=%d,connectivityState = [%s]", BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT, gson.toJson(connectivityState)));

        HealthGrpc.HealthStub healthStub = HealthGrpc.newStub(channel);
        MyWordGrpc.MyWordStub myWordStub = MyWordGrpc.newStub(channel);


        healthStub.withDeadlineAfter(60, TimeUnit.SECONDS)
                .check(myWordGrpcHealthCheckRequest,
                        new StreamObserver<HealthCheckResponse>() {
                            @Override
                            public void onNext(HealthCheckResponse value) {

                                if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                                    Log.i(TAG, String.format("listMyWords healthStub.check onNext requestLastUpdated = [%s]", gson.toJson(listRequest)));

                                    myWordStub
                                            .withWaitForReady()
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
                                    Log.i(TAG, String.format("listMyWords healthStub.check onNext NOT! ServingStatus.SERVING listRequest = [%s]", gson.toJson(listRequest)));
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

    public void listMyStats(CallCredentials callCredentials, MyWordCallback callback) {
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
                            myWordStub
                                    .withWaitForReady()
                                    .withCallCredentials(callCredentials)
                                    .stats(listRequest, new StreamObserver<StatsResponse>() {
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

    public interface WordCallback extends CommonApiCallback {
        void onWordReceived(WordResponse image);

        void onWordUpdated(StatusResponse image);
    }

    public interface MyWordCallback extends CommonApiCallback {

        void onMyWordReceived(MyWordResponse value);

        void onMyStatsReceived(StatsResponse value);
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
