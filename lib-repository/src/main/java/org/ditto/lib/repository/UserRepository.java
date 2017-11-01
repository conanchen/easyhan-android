package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;

import com.google.gson.Gson;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.SigninService;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.Value;
import org.ditto.lib.dbroom.kv.VoAccessToken;
import org.ditto.lib.dbroom.user.Myprofile;
import org.ditto.lib.dbroom.user.User;
import org.ditto.lib.dbroom.user.UserCommand;
import org.ditto.lib.repository.model.Status;
import org.ditto.sigin.grpc.SigninResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository that handles User objects.
 */
@Singleton
public class UserRepository {

    private final static String TAG = UserRepository.class.getSimpleName();

    private final static Gson gson = new Gson();
    private ApigrpcFascade apigrpcFascade;
    private RoomFascade roomFascade;

    @Inject
    public UserRepository(ApigrpcFascade apigrpcFascade, RoomFascade roomFascade) {
        this.roomFascade = roomFascade;
        this.apigrpcFascade = apigrpcFascade;
    }


    public LiveData<User> findUserByLogin(String login) {
        return roomFascade.daoUser.load(login);
    }

    public Observable<Long> save(UserCommand command) {
        return Observable.fromCallable(
                () -> roomFascade.daoUser.save(command)
        ).subscribeOn(Schedulers.io());
    }

    public LiveData<Myprofile> loadProfile(String type, int size) {
        return roomFascade.daoUser.loadProfile(type, size);
    }

    public LiveData<List<Myprofile>> loadAllProfiles() {
        return roomFascade.daoUser.loadAllProfiles();
    }

    public LiveData<Status> loginWithQQAccessToken(String accessToken) {
        return new LiveData<Status>() {
            @Override
            protected void onActive() {
                apigrpcFascade.getSigninService().loginWithQQAccessToken(accessToken,
                        new SigninService.SigninCallback() {

                            @Override
                            public void onApiError() {

                            }

                            @Override
                            public void onApiCompleted() {

                            }

                            @Override
                            public void onApiReady() {

                            }

                            @Override
                            public void onSignined(SigninResponse response) {
                                KeyValue keyValue = KeyValue.builder()
                                        .setKey(KeyValue.KEY.USER_CURRENT_ACCESSTOKEN)
                                        .setValue(Value
                                                .builder()
                                                .setVoAccessToken(
                                                        VoAccessToken
                                                                .builder()
                                                                .setAccessToken(response.getAccessToken())
                                                                .setExpiresIn(response.getExpiresIn())
                                                                .build())
                                                .build())
                                        .build();
                                if (roomFascade.daoKeyValue.save(keyValue) > 0) {
                                    postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                                }
                            }
                        });
            }
        };
    }
}