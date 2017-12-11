package org.ditto.feature.word.di;


import org.ditto.feature.word.slide.WordSlideFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentWordSlidesBuildersModule {


    @ContributesAndroidInjector
    abstract WordSlideFragment contributeWordSlideFragment();

}