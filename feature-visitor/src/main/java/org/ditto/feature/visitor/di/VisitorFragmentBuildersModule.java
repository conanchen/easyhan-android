package org.ditto.feature.visitor.di;


import org.ditto.feature.visitor.FragmentVisitorIndices;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class VisitorFragmentBuildersModule {



    @ContributesAndroidInjector
    abstract FragmentVisitorIndices contributeFragmentVisitorIndices();


}