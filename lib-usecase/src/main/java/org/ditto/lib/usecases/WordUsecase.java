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

}
