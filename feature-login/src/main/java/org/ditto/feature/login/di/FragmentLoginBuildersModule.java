package org.ditto.feature.login.di;


import org.ditto.feature.login.register.RegisterUsernameFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentLoginBuildersModule {

    @ContributesAndroidInjector
    abstract RegisterUsernameFragment contributeRegisterUsernameFragment();

}