package org.ditto.feature.my.di;



import org.ditto.feature.my.index.FragmentMy;
import org.ditto.feature.my.index.FragmentMyWords;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentMyBuildersModule {

    @ContributesAndroidInjector
    abstract FragmentMy contributeFragmentMy();

    @ContributesAndroidInjector
    abstract FragmentMyWords contributeFragmentMyWords();

}