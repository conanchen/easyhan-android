package org.ditto.feature.visitor.di;


import org.ditto.feature.visitor.VisitorIndicesViewModel;

import dagger.Subcomponent;

/**
 * A sub component to create ViewModels. It is called by the
 * {@link VisitorViewModelFactory}. Using this component allows
 * ViewModels to define {@link javax.inject.Inject} constructors.
 */
@Subcomponent
public interface VisitorViewModelSubComponent {



    VisitorIndicesViewModel createPartyIndicesViewModel();


    @Subcomponent.Builder
    interface Builder {
        VisitorViewModelSubComponent build();
    }
}