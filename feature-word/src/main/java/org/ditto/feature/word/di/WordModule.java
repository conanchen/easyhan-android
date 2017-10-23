package org.ditto.feature.word.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = WordViewModelSubComponent.class)
public class WordModule {

    @Singleton
    @Provides
    WordViewModelFactory provideWordViewModelFactory(
            WordViewModelSubComponent.Builder viewModelSubComponent) {
        return new WordViewModelFactory(viewModelSubComponent.build());
    }

}