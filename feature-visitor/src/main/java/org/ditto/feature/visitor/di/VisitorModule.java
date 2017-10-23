package org.ditto.feature.visitor.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = VisitorViewModelSubComponent.class)
public   class VisitorModule {

    @Singleton
    @Provides
    VisitorViewModelFactory provideMessageViewModelFactory(
            VisitorViewModelSubComponent.Builder viewModelSubComponent) {
        return new VisitorViewModelFactory(viewModelSubComponent.build());
    }

}