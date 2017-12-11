package org.ditto.feature.word.di;


import org.ditto.feature.word.index.FragmentWords;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentWordsBuildersModule {

    @ContributesAndroidInjector
    abstract FragmentWords contributeFragmentWords();


}