package org.ditto.pinkhan.di;

import org.ditto.feature.login.LoginActivity;
import org.ditto.feature.login.di.FragmentLoginBuildersModule;
import org.ditto.feature.my.crawler.WordCrawlerActivity;
import org.ditto.feature.my.di.FragmentMyBuildersModule;
import org.ditto.feature.my.index.WordExamActivity;
import org.ditto.feature.word.di.FragmentWordsBuildersModule;
import org.ditto.lib.usecases.AppServiceCommandSenderImpl;
import org.ditto.lib.usecases.AppServiceKeepliveTraceImpl;
import org.ditto.pinkhan.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module()
public abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = {
            FragmentLoginBuildersModule.class,
            FragmentWordsBuildersModule.class,
            FragmentMyBuildersModule.class})

    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract WordExamActivity contributeWordExamActivity();

    @ContributesAndroidInjector
    abstract WordCrawlerActivity contributeWordCrawlerActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract AppServiceKeepliveTraceImpl contributeAppServiceKeepliveTraceImpl();

    @ContributesAndroidInjector
    abstract AppServiceCommandSenderImpl contributeAppServiceCommandSenderImpl();
}