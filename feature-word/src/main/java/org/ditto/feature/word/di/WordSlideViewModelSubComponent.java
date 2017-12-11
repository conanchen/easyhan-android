package org.ditto.feature.word.di;


import org.ditto.feature.word.slide.WordSlideViewModel;
import org.ditto.feature.word.profile.WordViewModel;

import dagger.Subcomponent;

/**
 * A sub component to create ViewModels. It is called by the
 * {@link WordViewModelFactory}. Using this component allows
 * ViewModels to define {@link javax.inject.Inject} constructors.
 */
@Subcomponent
public interface WordSlideViewModelSubComponent {


    WordSlideViewModel createWordSlidesViewModel();
    WordViewModel createWordViewModel();


    @Subcomponent.Builder
    interface Builder {
        WordSlideViewModelSubComponent build();
    }
}