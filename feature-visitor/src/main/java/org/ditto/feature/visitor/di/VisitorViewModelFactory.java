package org.ditto.feature.visitor.di;

import android.arch.lifecycle.ViewModel;

import org.ditto.feature.base.di.BaseViewModelFactory;
import org.ditto.feature.visitor.VisitorIndicesViewModel;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class VisitorViewModelFactory extends BaseViewModelFactory {

    @Inject
    public VisitorViewModelFactory(final VisitorViewModelSubComponent viewModelSubComponent) {
        super();
        // we cannot inject view models directly because they won't be bound to the owner's
        // view model scope.



        super.creators.put(VisitorIndicesViewModel.class, new Callable<ViewModel>() {
            @Override
            public ViewModel call() throws Exception {
                return viewModelSubComponent.createPartyIndicesViewModel();
            }
        });



    }

}