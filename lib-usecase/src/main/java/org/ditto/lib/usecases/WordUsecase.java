package org.ditto.lib.usecases;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ditto.lib.repository.RepositoryFascade;
import org.ditto.lib.repository.model.Status;
import org.easyhan.common.grpc.HanziLevel;
import org.easyhan.word.HanZi;

import java.util.Arrays;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by admin on 2017/6/25.
 */

public class WordUsecase {
    private   final static String TAG=WordUsecase.class.getSimpleName();
    private RepositoryFascade repositoryFascade;

    @Inject
    public WordUsecase(RepositoryFascade repositoryFascade) {
        this.repositoryFascade = repositoryFascade;
    }

    public LiveData<Status> init(HanziLevel level, int startIdx) {
        return new LiveData<Status>() {
            @Override
            protected void onActive() {
                Observable
                        .just(level)
                        .map(level -> {
                            String[] words = null;
                            switch (level) {
                                case ONE:
                                    words = StringUtils.splitByWholeSeparator(HanZi.LEVEL1, ",");
                                    break;
                                case TWO:
                                    words = StringUtils.splitByWholeSeparator(HanZi.LEVEL2, ",");
                                    break;
                                case THREE:
                                    words = StringUtils.splitByWholeSeparator(HanZi.LEVEL3, ",");
                                    break;
                            }
                            Log.i(TAG,String.format("map level=%s num=%d",level.name(),words.length));
                            return words;
                        })
                        .flatMapIterable(new Function<String[], Iterable<String>>() {
                            @Override
                            public Iterable<String> apply(String[] strings) throws Exception {
                                return Arrays.asList(strings);
                            }
                        })
                        .skip(startIdx)
                        .doOnComplete(() -> {
                            postValue(Status.builder().setCode(Status.Code.END_SUCCESS).setMessage("init word ok").build());
                        })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.io()).subscribe(s -> {
                            repositoryFascade.wordRepository.init(level, s);
                        }
                );
            }
        };
    }
}
