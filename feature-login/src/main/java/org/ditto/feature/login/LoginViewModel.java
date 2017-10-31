package org.ditto.feature.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {
    private final static String TAG = LoginViewModel.class.getSimpleName();
    @VisibleForTesting
    final MutableLiveData<String> mutableRequestQQAccessToken = new MutableLiveData<String>();

    private final LiveData<Status> liveLoginedUser;
    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public LoginViewModel() {
        liveLoginedUser = Transformations.switchMap(mutableRequestQQAccessToken, (String accessToken) -> {
            if (accessToken == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.userRepository.loginWithQQAccessToken(accessToken);
            }
        });
    }

    public LiveData<Status> getLiveLoginedUser() {
        return liveLoginedUser;
    }

    public void loginWithQQAccessToken(String accessToken) {
        mutableRequestQQAccessToken.setValue(accessToken);
    }
}