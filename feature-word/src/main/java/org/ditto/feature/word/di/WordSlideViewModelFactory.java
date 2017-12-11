package org.ditto.feature.word.di;

import org.ditto.feature.base.di.BaseViewModelFactory;
import org.ditto.feature.word.slide.WordSlideViewModel;
import org.ditto.feature.word.profile.WordViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WordSlideViewModelFactory extends BaseViewModelFactory {

    @Inject
    public WordSlideViewModelFactory(final WordSlideViewModelSubComponent viewModelSubComponent) {
        super();
        // we cannot inject view models directly because they won't be bound to the owner's
        // view model scope.


        super.creators.put(WordSlideViewModel.class,
                () -> viewModelSubComponent.createWordSlidesViewModel());


        super.creators.put(WordViewModel.class,
                () -> viewModelSubComponent.createWordViewModel());

    }

}