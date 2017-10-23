package org.ditto.feature.my.di;



import org.ditto.feature.my.index.MyViewModel;

import dagger.Subcomponent;

/**
 * A sub component to create ViewModels. It is called by the
 * {@link MyViewModelFactory}. Using this component allows
 * ViewModels to define {@link javax.inject.Inject} constructors.
 */
@Subcomponent
public interface MyViewModelSubComponent {


    MyViewModel createMyViewModel();



    @Subcomponent.Builder
    interface Builder {
        MyViewModelSubComponent build();
    }
}