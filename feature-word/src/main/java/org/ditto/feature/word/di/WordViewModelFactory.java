package org.ditto.feature.word.di;

import org.ditto.feature.base.di.BaseViewModelFactory;
import org.ditto.feature.word.index.WordsViewModel;
import org.ditto.feature.word.profile.WordViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WordViewModelFactory extends BaseViewModelFactory {

    @Inject
    public WordViewModelFactory(final WordViewModelSubComponent viewModelSubComponent) {
        super();
        // we cannot inject view models directly because they won't be bound to the owner's
        // view model scope.


        super.creators.put(WordsViewModel.class,
                () -> viewModelSubComponent.createImageIndicesViewModel());


        super.creators.put(WordViewModel.class,
                () -> viewModelSubComponent.createImageIndexViewModel());

    }

}