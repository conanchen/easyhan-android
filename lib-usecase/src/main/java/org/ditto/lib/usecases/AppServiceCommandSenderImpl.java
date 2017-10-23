package org.ditto.lib.usecases;


import android.arch.lifecycle.LifecycleService;
import android.content.Intent;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppServiceCommandSenderImpl extends LifecycleService {

    @Inject
    UsecaseFascade usecaseFascade;

    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Observable.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        usecaseFascade.repositoryFascade.wordRepository.saveSampleImageIndices();
                        usecaseFascade.repositoryFascade.indexVisitorRepository.saveSampleVisitorIndices();

                    }
                });
    }

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
    }


}