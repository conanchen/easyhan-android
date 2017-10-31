package org.ditto.feature.login.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = LoginViewModelSubComponent.class)
public class LoginModule {

    @Singleton
    @Provides
    LoginViewModelFactory provideLoginViewModelFactory(
            LoginViewModelSubComponent.Builder viewModelSubComponent) {
        return new LoginViewModelFactory(viewModelSubComponent.build());
    }

}