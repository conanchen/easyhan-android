package org.ditto.feature.login.di;

import org.ditto.feature.base.di.BaseViewModelFactory;
import org.ditto.feature.login.LoginViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginViewModelFactory extends BaseViewModelFactory {

    @Inject
    public LoginViewModelFactory(final LoginViewModelSubComponent viewModelSubComponent) {
        super();
        // we cannot inject view models directly because they won't be bound to the owner's
        // view model scope.

        super.creators.put(LoginViewModel.class,
                () -> viewModelSubComponent.createLoginViewModel());

    }

}