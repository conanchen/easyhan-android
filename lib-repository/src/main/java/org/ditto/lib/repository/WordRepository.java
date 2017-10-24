package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.WordService;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.repository.model.MyWordLoadRequest;
import org.ditto.lib.repository.model.MyWordRefreshRequest;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.repository.model.WordLoadRequest;
import org.ditto.lib.repository.model.WordRefreshRequest;
import org.easyhan.common.grpc.HanziLevel;
import org.easyhan.myword.grpc.MyWordResponse;
import org.easyhan.myword.grpc.UpsertResponse;
import org.easyhan.word.grpc.ListRequest;
import org.easyhan.word.grpc.WordResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository that handles Word objects.
 */
@Singleton
public class WordRepository {
    private final static String TAG = WordRepository.class.getSimpleName();

    private final static Gson gson = new Gson();
    private ApigrpcFascade apigrpcFascade;
    private RoomFascade roomFascade;

    @Inject
    public WordRepository(ApigrpcFascade apigrpcFascade,
                          RoomFascade roomFascade) {
        this.roomFascade = roomFascade;
        this.apigrpcFascade = apigrpcFascade;
    }

    public void refresh(WordRefreshRequest wordRefreshRequest) {
        ListRequest listRequest = ListRequest.newBuilder()
                .setLevel(wordRefreshRequest.level).setLastUpdated(wordRefreshRequest.lastUpdated).build();

        apigrpcFascade.getWordService().listWords(listRequest,
                new WordService.WordCallback() {
                    @Override
                    public void onApiReady() {
                        Status status = Status.builder().setCode(Status.Code.LOADING)
                                .build();
//                        postValue(status);
                        Log.i(TAG, String.format("onApiReady  postValue(status) status = [%s],listRequest=%s",
                                gson.toJson(status), gson.toJson(listRequest)));
                    }

                    @Override
                    public void onWordReceived(WordResponse response) {
//                        postValue(Status.builder().setCode(Status.Code.LOADING)
//                                .build());
                        Log.i(TAG, String.format("onWordReceived save to database, word=[%s]", gson.toJson(response)));
                        Word word = Word.builder()
                                .setWord(response.getWord())
                                .setIdx(response.getIdx())
                                .setPinyin(response.getPinyin())
                                .setLevel(response.getLevel().name())
                                .setCreated(response.getCreated())
                                .setLastUpdated(response.getLastUpdated())
                                .setVisitCount(response.getVistCount())
                                .build();
                        roomFascade.daoWord.save(word);
                    }

                    @Override
                    public void onApiCompleted() {
                        Status status = Status.builder().setCode(Status.Code.END_SUCCESS)
                                .build();
//                        postValue(status);
                        Log.i(TAG, String.format("onApiCompleted  postValue(status)  postValue(status) status = [%s],listRequest=%s",
                                gson.toJson(status), gson.toJson(listRequest)));
                    }

                    @Override
                    public void onApiError() {
                        Status status = Status.builder().setCode(Status.Code.END_DISCONNECTED)
                                .setMessage("aaaaaa").build();
//                        postValue(status);
                        Log.i(TAG, String.format("onApiError  postValue(status)  postValue(status) status = [%s],listRequest=%s",
                                gson.toJson(status), gson.toJson(listRequest)));
                    }
                });
    }

    public LiveData<PagedList<Word>> listPagedWordsBy(WordLoadRequest wordLoadRequest) {
        if(VoWordSortType.WordSortType.MEMORY.equals(wordLoadRequest.sortType)) {
            return roomFascade.daoWord.listLivePagedWordsOrderByMemIdx(wordLoadRequest.level.name())
                    .create(wordLoadRequest.page * wordLoadRequest.pageSize,
                            new PagedList.Config
                                    .Builder()
                                    .setPageSize(wordLoadRequest.pageSize)
                                    .setPrefetchDistance(wordLoadRequest.pageSize)
                                    .setEnablePlaceholders(true)
                                    .build());
        }else{
            return roomFascade.daoWord.listLivePagedWordsOrderByIdx(wordLoadRequest.level.name())
                    .create(wordLoadRequest.page * wordLoadRequest.pageSize,
                            new PagedList.Config
                                    .Builder()
                                    .setPageSize(wordLoadRequest.pageSize)
                                    .setPrefetchDistance(wordLoadRequest.pageSize)
                                    .setEnablePlaceholders(true)
                                    .build());
        }
    }

    public long findMaxLastUpdated(HanziLevel imageType) {
        long result = 0l;
        Word word = roomFascade.daoWord.findLatestWord(imageType.name());
        if (word != null) {
            result = word.lastUpdated;
        }
        return result;
    }

    public long findMyWordMaxLastUpdated() {
        long result = 0l;
        Word word = roomFascade.daoWord.findMyLatestWord();
        if (word != null) {
            result = word.memLastUpdated;
        }
        return result;
    }


    public LiveData<Word> find(String imageUrl) {
        return roomFascade.daoWord.findLive(imageUrl);
    }


    public void saveSampleImageIndices() {
        Observable.just(true).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                    long now = 0l;
                    if (roomFascade.daoWord.findLatestWord(HanziLevel.ONE.name()) == null) {
                        roomFascade.daoWord.save(
                                Word.builder()
                                        .setWord(String.format("一"))
                                        .setIdx(1)
                                        .setPinyin("yì")
                                        .setLevel(HanziLevel.ONE.name())
                                        .setLastUpdated(now + 1)
                                        .build());
                    }
                    if (roomFascade.daoWord.findLatestWord(HanziLevel.TWO.name()) == null) {
                        roomFascade.daoWord.save(

                                Word.builder()
                                        .setWord(String.format("乂"))
                                        .setIdx(3501)
                                        .setPinyin("yì")
                                        .setLevel(HanziLevel.TWO.name())
                                        .setLastUpdated(now + 1)
                                        .build());
                    }
                    if (roomFascade.daoWord.findLatestWord(HanziLevel.THREE.name()) == null) {
                        roomFascade.daoWord.save(
                                Word.builder()
                                        .setWord(String.format("亍"))
                                        .setIdx(6501)
                                        .setPinyin("chù")
                                        .setLevel(HanziLevel.THREE.name())
                                        .setLastUpdated(now + 1)
                                        .build()
                        );
                    }
                });

    }

    public LiveData<Status> upsertMyWord(String word) {
        return new LiveData<Status>() {
            @Override
            protected void onActive() {
                apigrpcFascade.getWordService().upsertMyWord(word,
                        new WordService.MyWordCallback() {
                            @Override
                            public void onMyWordUpserted(UpsertResponse image) {
                                postValue(Status.builder()
                                        .setCode(Status.Code.LOADING)
                                        .setMessage(gson.toJson(image))
                                        .build());
                            }

                            @Override
                            public void onMyWordReceived(MyWordResponse value) {
                                //Nothing to do for  upsertMyWord
                            }

                            @Override
                            public void onApiError() {
                                postValue(Status.builder().setCode(Status.Code.END_ERROR).build());
                            }

                            @Override
                            public void onApiCompleted() {
                                postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                            }

                            @Override
                            public void onApiReady() {
                                postValue(Status.builder().setCode(Status.Code.START).build());
                            }
                        });
            }
        };
    }

    public LiveData<PagedList<Word>> listPagedMyWordsBy(MyWordLoadRequest request) {
        if(VoWordSortType.WordSortType.MEMORY.equals(request.sortType)) {
            return roomFascade.daoWord.listLivePagedMyWordsOrderByMemIdx()
                    .create(request.page * request.pageSize,
                            new PagedList.Config
                                    .Builder()
                                    .setPageSize(request.pageSize)
                                    .setPrefetchDistance(request.pageSize)
                                    .setEnablePlaceholders(true)
                                    .build());
        }else{
            return roomFascade.daoWord.listLivePagedMyWordsOrderByIdx()
                    .create(request.page * request.pageSize,
                            new PagedList.Config
                                    .Builder()
                                    .setPageSize(request.pageSize)
                                    .setPrefetchDistance(request.pageSize)
                                    .setEnablePlaceholders(true)
                                    .build());
        }
    }

    public void refresh(MyWordRefreshRequest request) {
        ListRequest listRequest = ListRequest.newBuilder()
                .setLastUpdated(request.lastUpdated).build();

        apigrpcFascade.getWordService().listMyWords(request.lastUpdated,
                new WordService.MyWordCallback() {
                    @Override
                    public void onApiReady() {
                        Status status = Status.builder().setCode(Status.Code.LOADING)
                                .build();
//                        postValue(status);
                        Log.i(TAG, String.format("onApiReady  postValue(status) status = [%s],listRequest=%s",
                                gson.toJson(status), gson.toJson(listRequest)));
                    }

                    @Override
                    public void onMyWordUpserted(UpsertResponse image) {
                        //Nothing to do here
                    }

                    @Override
                    public void onMyWordReceived(MyWordResponse response) {
                        Log.i(TAG, String.format("onMyWordReceived MyWordResponse=[%s]", gson.toJson(response)));
                        roomFascade.daoWord.findSingle(response.getWord())
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(word -> {
                                    word.memIdx = response.getMemIdx();
                                    word.memLastUpdated = response.getLastUpdated();
                                    roomFascade.daoWord.save(word);
                                    Log.i(TAG, String.format("onMyWordReceived save word=[%s]", gson.toJson(word)));
                                });
                    }

                    @Override
                    public void onApiCompleted() {
                        Status status = Status.builder().setCode(Status.Code.END_SUCCESS)
                                .build();
//                        postValue(status);
                        Log.i(TAG, String.format("onApiCompleted  postValue(status)  postValue(status) status = [%s],listRequest=%s",
                                gson.toJson(status), gson.toJson(listRequest)));
                    }

                    @Override
                    public void onApiError() {
                        Status status = Status.builder().setCode(Status.Code.END_DISCONNECTED)
                                .setMessage("aaaaaa").build();
//                        postValue(status);
                        Log.i(TAG, String.format("onApiError  postValue(status)  postValue(status) status = [%s],listRequest=%s",
                                gson.toJson(status), gson.toJson(listRequest)));
                    }
                });
    }
}