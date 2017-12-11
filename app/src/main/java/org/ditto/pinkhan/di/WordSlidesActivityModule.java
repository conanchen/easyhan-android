package org.ditto.pinkhan.di;

import org.ditto.feature.word.di.FragmentWordSlidesBuildersModule;
import org.ditto.feature.word.slide.WordSlideActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module()
public abstract class WordSlidesActivityModule {

    @ContributesAndroidInjector(modules = {
            FragmentWordSlidesBuildersModule.class})


    abstract WordSlideActivity contributeWordActivity();

}