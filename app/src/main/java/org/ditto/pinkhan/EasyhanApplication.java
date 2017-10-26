package org.ditto.pinkhan;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.github.anrwatchdog.ANRWatchDog;
import com.google.android.gms.security.ProviderInstaller;
import com.xdandroid.hellodaemon.DaemonEnv;

import org.ditto.lib.apigrpc.JcaUtils;
import org.ditto.lib.repository.RepositoryFascade;
import org.ditto.lib.usecases.AppServiceCommandSenderImpl;
import org.ditto.lib.usecases.AppServiceKeepliveTraceImpl;
import org.ditto.pinkhan.di.AppInjector;

import java.security.Provider;
import java.util.List;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

//import com.robotpajamas.stetho.couchbase.CouchbaseInspectorModulesProvider;

/**
 * Created by admin on 2017/5/3.
 */

public class EasyhanApplication extends MultiDexApplication implements HasActivityInjector, HasServiceInjector {
    public static final String TAG = EasyhanApplication.class.getSimpleName();

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Inject
    RepositoryFascade repositoryFascade;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.USE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
            new ANRWatchDog().start();
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            ARouter.openDebug();
            initStethoDebugBridge();
        }

        AppInjector.init(this);

        ARouter.init(this);


        DaemonEnv.initialize(this, AppServiceKeepliveTraceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        try {
            startService(new Intent(this, AppServiceKeepliveTraceImpl.class));
            startService(new Intent(this, AppServiceCommandSenderImpl.class));
        } catch (Exception ignored) {
        }

        ProviderInstaller.installIfNeededAsync(this, providerInstallListener);
        List<Provider> providers = JcaUtils.getSecurityProviders();
        for (Provider provider : providers) {
            List<Provider.Service> services = JcaUtils.getSortedProviderServices(provider);
            for (Provider.Service service : services) {
                Log.i(TAG, String.format("Provider=%s,Algorithm=%s", provider.getName(), service.getAlgorithm()));
            }
        }
    }

    private ProviderInstaller.ProviderInstallListener providerInstallListener =
            new ProviderInstaller.ProviderInstallListener() {
                @Override
                public void onProviderInstalled() {
                    // Provider installed
                    Log.i(TAG, String.format("%s", "Provider installed"));
                }

                @Override
                public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
                    Log.i(TAG, String.format("%s", "Provider installation failed errorCode=%d", errorCode));
                    // Provider installation failed
                }
            };

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    private void initStethoDebugBridge() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (BuildConfig.DEBUG) {
//            Stetho.initialize(
//                    Stetho.newInitializerBuilder(this)
//                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                            //.enableWebKitInspector(new CouchbaseInspectorModulesProvider.Builder(this).build())
//                            .build());
            Stetho.initializeWithDefaults(this);
        }
    }

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }
}
