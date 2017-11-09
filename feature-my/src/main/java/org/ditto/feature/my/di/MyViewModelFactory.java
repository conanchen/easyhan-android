package org.ditto.feature.my.di;

import org.ditto.feature.base.di.BaseViewModelFactory;
import org.ditto.feature.my.index.MyViewModel;
import org.ditto.feature.my.index.MyWordViewModel;
import org.ditto.feature.my.index.MyWordsViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyViewModelFactory extends BaseViewModelFactory {

    @Inject
    public MyViewModelFactory(final MyViewModelSubComponent viewModelSubComponent) {
        super();
        // we cannot inject view models directly because they won't be bound to the owner's
        // view model scope.

        super.creators.put(MyViewModel.class,
                () -> viewModelSubComponent.createMyViewModel());

        super.creators.put(MyWordsViewModel.class,
                () -> viewModelSubComponent.createMyWordsViewModel());

        super.creators.put(MyWordViewModel.class,
                () -> viewModelSubComponent.createMyWordViewModel());
    }

}