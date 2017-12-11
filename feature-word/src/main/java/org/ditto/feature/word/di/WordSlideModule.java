package org.ditto.feature.word.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = WordSlideViewModelSubComponent.class)
public class WordSlideModule {

    @Singleton
    @Provides
    WordSlideViewModelFactory provideWordSlideViewModelFactory(
            WordSlideViewModelSubComponent.Builder viewModelSubComponent) {
        return new WordSlideViewModelFactory(viewModelSubComponent.build());
    }

}