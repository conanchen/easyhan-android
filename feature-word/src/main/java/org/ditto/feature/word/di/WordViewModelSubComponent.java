package org.ditto.feature.word.di;


import org.ditto.feature.word.index.WordsViewModel;
import org.ditto.feature.word.profile.WordViewModel;

import dagger.Subcomponent;

/**
 * A sub component to create ViewModels. It is called by the
 * {@link WordViewModelFactory}. Using this component allows
 * ViewModels to define {@link javax.inject.Inject} constructors.
 */
@Subcomponent
public interface WordViewModelSubComponent {


    WordsViewModel createImageIndicesViewModel();

    WordViewModel createImageIndexViewModel();


    @Subcomponent.Builder
    interface Builder {
        WordViewModelSubComponent build();
    }
}