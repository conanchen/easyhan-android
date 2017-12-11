package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.JcaUtils;
import org.ditto.lib.apigrpc.WordService;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.index.Pinyin;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.Value;
import org.ditto.lib.dbroom.kv.VoAccessToken;
import org.ditto.lib.dbroom.kv.VoWordSortType;
import org.ditto.lib.dbroom.kv.VoWordSummary;
import org.ditto.lib.repository.model.MyWordLoadRequest;
import org.ditto.lib.repository.model.MyWordRefreshRequest;
import org.ditto.lib.repository.model.MyWordStatsRefreshRequest;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.repository.model.WordLoadRequest;
import org.ditto.lib.repository.model.WordRefreshRequest;
import org.ditto.lib.repository.model.WordSlidesLoadRequest;
import org.easyhan.common.grpc.HanziLevel;
import org.easyhan.common.grpc.StatusResponse;
import org.easyhan.myword.grpc.MyWordResponse;
import org.easyhan.myword.grpc.StatsResponse;
import org.easyhan.myword.grpc.UpsertResponse;
import org.easyhan.word.grpc.ListRequest;
import org.easyhan.word.grpc.WordResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.CallCredentials;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
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

    public Flowable<Word> findMyExamWord() {
        Random random = new Random();
        return roomFascade.daoWord
                .getLiveMyExamWords(true, 20)
                .flatMapIterable(x -> x)
                .skip(random.nextInt(20))
                .take(1);
    }

    public Maybe<Word> findByIdx(Integer wordIdx) {
        return roomFascade.daoWord.findOneByIdx(wordIdx);
    }

    public void updateWordFromBaidu(String word, String html, ProgressCallback callback) {
        apigrpcFascade.getWordService().updateWordFromBaidu(word, html, new WordService.WordCallback() {
            @Override
            public void onWordReceived(WordResponse image) {
            }

            @Override
            public void onWordUpdated(StatusResponse image) {
                callback.onSucess();
            }

            @Override
            public void onApiError() {
                callback.onError();
            }

            @Override
            public void onApiCompleted() {

            }

            @Override
            public void onApiReady() {

            }
        });
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
                        Log.i(TAG, String.format("onWordReceived save to database, word=%s,pinyins=%s,\nresponse[%s]",
                                response.getWord(), gson.toJson(response.getPinyinsList()), gson.toJson(response)));
                        List<Pinyin> pinyins = new ArrayList<>();
                        for (int i = 0; i < response.getPinyinsCount(); i++) {
                            pinyins.add(new Pinyin(response.getPinyins(i).getPinyin(), response.getPinyins(i).getMp3()));
                        }


                        Word word = Word.builder()
                                .setWord(response.getWord())
                                .setLevel(response.getLevel().name())
                                .setLevelIdx(response.getLevelIdx())
                                .setCreated(response.getCreated())
                                .setLastUpdated(response.getLastUpdated())
                                .setVisitCount(response.getVistCount())
                                .setPinyins(pinyins)
                                .setRadical(response.getRadical())
                                .setWuxing(response.getWuxing())
                                .setTraditional(response.getTraditional())
                                .setWubi(response.getWubi())
                                .setStrokes(response.getStrokes15List())
                                .setStrokenames(response.getStrokenamesList())
                                .setStrokes_count(response.getStrokesCount17())
                                .setBasemean(response.getBasemean())
                                .setDetailmean(response.getDetailmean())
                                .setTerms(response.getTermsList())
                                .setRiddles(response.getRiddlesList())
                                .setFanyi(response.getFanyi())
                                .setBishun(response.getBishun())
                                .setDefined(response.getDefined())
                                .build();
                        roomFascade.daoWord.save(word);
                    }

                    @Override
                    public void onWordUpdated(StatusResponse image) {

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
        if (VoWordSortType.WordSortType.MEMORY.equals(wordLoadRequest.sortType)) {
            return roomFascade.daoWord.listLivePagedWordsOrderByMemIdx(wordLoadRequest.level.name())
                    .create(wordLoadRequest.page * wordLoadRequest.pageSize,
                            new PagedList.Config
                                    .Builder()
                                    .setPageSize(wordLoadRequest.pageSize)
                                    .setPrefetchDistance(wordLoadRequest.pageSize)
                                    .setEnablePlaceholders(true)
                                    .build());
        } else {
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

    public Maybe<Word> findMyWordMaxLastUpdated() {
        return roomFascade.daoWord.findMyLatestWord();
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
                                        .setLevel(HanziLevel.ONE.name())
                                        .setLevelIdx(1)
                                        .setLastUpdated(now + 1)
                                        .build());
                    }
                    if (roomFascade.daoWord.findLatestWord(HanziLevel.TWO.name()) == null) {
                        roomFascade.daoWord.save(

                                Word.builder()
                                        .setWord(String.format("乂"))
                                        .setLevel(HanziLevel.TWO.name())
                                        .setLevelIdx(3501)
                                        .setLastUpdated(now + 1)
                                        .build());
                    }
                    if (roomFascade.daoWord.findLatestWord(HanziLevel.THREE.name()) == null) {
                        roomFascade.daoWord.save(
                                Word.builder()
                                        .setWord(String.format("亍"))
                                        .setLevel(HanziLevel.THREE.name())
                                        .setLevelIdx(6501)
                                        .setLastUpdated(now + 1)
                                        .build()
                        );
                    }
                });

    }

    public void upsertMyWord(VoAccessToken voAccessToken, String word, Boolean isFlight, ProgressCallback callback) {
        CallCredentials callCredentials = JcaUtils
                .getCallCredentials(voAccessToken.accessToken,
                        Long.valueOf(voAccessToken.expiresIn));
        apigrpcFascade.getWordService().upsertMyWord(callCredentials, word, isFlight,
                new WordService.MyWordCallback() {
                    @Override
                    public void onMyWordUpserted(UpsertResponse response) {
                        Log.i(TAG, String.format("onMyWordUpserted upsertResponse.getMemIdx()=%d", response.getMemIdx()));
                        roomFascade.daoWord.findMaybe(word)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(word -> {
                                    if (response.getMemIdx() > 7) {
                                        word.memIdxIsOverThreshold = 1;
                                    }
                                    word.memIdx = response.getMemIdx();
                                    roomFascade.daoWord.save(word);
                                    Log.i(TAG, String.format("onMyWordUpserted save word=[%s]", gson.toJson(word)));
                                });

                        callback.onSucess();
                    }

                    @Override
                    public void onMyWordReceived(MyWordResponse value) {
                        //Nothing to do for  upsertMyWord
                    }

                    @Override
                    public void onMyStatsReceived(StatsResponse value) {
                        //Nothing to do for  onMyStatsReceived
                    }

                    @Override
                    public void onApiError() {
                        Log.i(TAG, String.format("onApiError"));
                        callback.onError();
                    }

                    @Override
                    public void onApiCompleted() {
                        Log.i(TAG, String.format("onApiCompleted"));
                    }

                    @Override
                    public void onApiReady() {
                        Log.i(TAG, String.format("onApiReady"));
                    }
                });
    }

    public LiveData<PagedList<Word>> listPagedMyWordsBy(MyWordLoadRequest request) {
        if (VoWordSortType.WordSortType.MEMORY.equals(request.sortType)) {
            return roomFascade.daoWord.listLivePagedMyWordsOrderByMemIdx()
                    .create(request.page * request.pageSize,
                            new PagedList.Config
                                    .Builder()
                                    .setPageSize(request.pageSize)
                                    .setPrefetchDistance(request.pageSize)
                                    .setEnablePlaceholders(true)
                                    .build());
        } else {
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
        CallCredentials callCredentials = JcaUtils
                .getCallCredentials(request.voAccessToken.accessToken,
                        Long.valueOf(request.voAccessToken.expiresIn));
        org.easyhan.myword.grpc.ListRequest listRequest = org.easyhan.myword.grpc.ListRequest.newBuilder()
                .setLastUpdated(request.lastUpdated).build();

        apigrpcFascade.getWordService().listMyWords(callCredentials, listRequest,
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
                    public void onMyStatsReceived(StatsResponse value) {
                        //Nothing to do here
                    }

                    @Override
                    public void onMyWordReceived(MyWordResponse response) {
                        Log.i(TAG, String.format("onMyProfileReceived MyWordResponse=[%s]", gson.toJson(response)));
                        roomFascade.daoWord.findMaybe(response.getWord())
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(word -> {
                                    if (response.getMemIdx() > 7) {
                                        word.memIdxIsOverThreshold = 1;
                                    }
                                    word.memIdx = response.getMemIdx();
                                    word.memLastUpdated = response.getLastUpdated();
                                    roomFascade.daoWord.save(word);
                                    Log.i(TAG, String.format("onMyProfileReceived save word=[%s]", gson.toJson(word)));
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

    public void refresh(MyWordStatsRefreshRequest request) {
        CallCredentials callCredentials = JcaUtils.getCallCredentials(request.voAccessToken.accessToken,
                Long.valueOf(request.voAccessToken.expiresIn));
        apigrpcFascade.getWordService().listMyStats(callCredentials, new WordService.MyWordCallback() {
            @Override
            public void onMyWordUpserted(UpsertResponse image) {
                // nothing to do here
            }

            @Override
            public void onMyWordReceived(MyWordResponse value) {
                // nothing to do here
            }

            @Override
            public void onMyStatsReceived(StatsResponse response) {
                Value value = Value
                        .builder()
                        .setVoWordSummary(VoWordSummary
                                .builder()
                                .setMemory0(response.getNumMemory0())
                                .setMemory1(response.getNumMemory1())
                                .setMemory2(response.getNumMemory2())
                                .setMemory3(response.getNumMemory3())
                                .setMemory4(response.getNumMemory4())
                                .setMemory5(response.getNumMemory5())
                                .setMemory6(response.getNumMemory6())
                                .setMemory7(response.getNumMemory7())
                                .build())
                        .build();
                switch (response.getLevel()) {
                    case ONE:
                        roomFascade.daoKeyValue.save(KeyValue.builder()
                                .setKey(KeyValue.KEY.USER_STATS_WORD_LEVEL1)
                                .setValue(value)
                                .build());
                        break;
                    case TWO:
                        roomFascade.daoKeyValue.save(KeyValue.builder()
                                .setKey(KeyValue.KEY.USER_STATS_WORD_LEVEL2)
                                .setValue(value)
                                .build());
                        break;
                    case THREE:
                        roomFascade.daoKeyValue.save(KeyValue.builder()
                                .setKey(KeyValue.KEY.USER_STATS_WORD_LEVEL3)
                                .setValue(value)
                                .build());
                        break;
                    default:
                        Log.i(TAG, String.format("onMyStatsReceived  unknown HanziLevel = %s", response.getLevel()));
                }
            }

            @Override
            public void onApiError() {
                Log.i(TAG, String.format("onApiError  refresh(MyWordStatsRefreshRequest request)"));
            }

            @Override
            public void onApiCompleted() {

            }

            @Override
            public void onApiReady() {

            }
        });
    }

    public void init(HanziLevel level, String s) {
        roomFascade.daoWord.save(
                Word.builder()
                        .setWord(s)
                        .setLevel(level.name())
                        .setLevelIdx(9999)
                        .build());
    }

    public LiveData<List<Word>> listWordSlidesBy(WordSlidesLoadRequest request) {
        return roomFascade.daoWord.getLiveMySlideWords(true,request.pageSize);
    }

    public interface ProgressCallback {
        void onSucess();

        void onError();
    }

}