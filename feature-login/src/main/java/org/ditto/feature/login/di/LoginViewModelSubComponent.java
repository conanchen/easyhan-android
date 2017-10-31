package org.ditto.feature.login.di;


import org.ditto.feature.login.LoginViewModel;

import dagger.Subcomponent;

/**
 * A sub component to create ViewModels. It is called by the
 * {@link LoginViewModelFactory}. Using this component allows
 * ViewModels to define {@link javax.inject.Inject} constructors.
 */
@Subcomponent
public interface LoginViewModelSubComponent {



    LoginViewModel createLoginViewModel();


    @Subcomponent.Builder
    interface Builder {
        LoginViewModelSubComponent build();
    }
}