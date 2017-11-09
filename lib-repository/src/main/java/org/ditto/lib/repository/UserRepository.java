package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.JcaUtils;
import org.ditto.lib.apigrpc.MyProfileService;
import org.ditto.lib.apigrpc.SigninService;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.Value;
import org.ditto.lib.dbroom.kv.VoAccessToken;
import org.ditto.lib.dbroom.user.MyProfile;
import org.ditto.lib.repository.model.Status;
import org.ditto.sigin.grpc.SigninResponse;
import org.easyhan.myprofile.grpc.MyProfileResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.CallCredentials;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository that handles MyProfile objects.
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

//
//    public Observable<Long> save(UserCommand command) {
//        return Observable.fromCallable(
//                () -> roomFascade.daoUser.save(command)
//        ).subscribeOn(Schedulers.io());
//    }

    public LiveData<MyProfile> loadProfile(String accessToken) {
        return roomFascade.daoUser.load(accessToken);
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

    public Maybe<MyProfile> findMyProfile(String accessToken) {
        return roomFascade.daoUser.findMaybe(accessToken);
    }

    public Maybe<VoAccessToken> findMyAccessToken() {
        return roomFascade.daoKeyValue
                .loadMaybe(KeyValue.KEY.USER_CURRENT_ACCESSTOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(keyValue -> keyValue.value.voAccessToken);
    }

    public void refreshMyProfile() {
        roomFascade.daoKeyValue
                .loadMaybe(KeyValue.KEY.USER_CURRENT_ACCESSTOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(keyValue -> keyValue.value.voAccessToken)
                .subscribe(new MaybeObserver<VoAccessToken>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onSuccess(VoAccessToken voAccessToken) {
                        Log.i(TAG, String.format("onSuccess voAccessToken=%s", gson.toJson(voAccessToken)));
                        CallCredentials callCredentials = JcaUtils.getCallCredentials(voAccessToken.accessToken,
                                Long.valueOf(voAccessToken.expiresIn));

                        apigrpcFascade.getMyProfileService().getMyProfile(callCredentials, "", new MyProfileService.MyProfileCallback() {
                            @Override
                            public void onMyProfileReceived(MyProfileResponse response) {
                                MyProfile myProfile = MyProfile
                                        .builder()
                                        .setAccessToken(voAccessToken.accessToken)
                                        .setAvatarUrl(response.getAvartarUrl())
                                        .setName(response.getNickName())
                                        .setLastUpdated(response.getLastUpdated())
                                        .setUserNo(response.getUserNo())
                                        .build();
                                roomFascade.daoUser.save(myProfile);
                                Log.i(TAG, String.format("onMyProfileReceived save myProfile=[%s]", gson.toJson(myProfile)));

                            }

                            @Override
                            public void onApiError() {

                            }

                            @Override
                            public void onApiCompleted() {

                            }

                            @Override
                            public void onApiReady() {

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        //access database error
                        Log.i(TAG, String.format("onError Throwable=%s", e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        //not found accesstoken
                        Log.i(TAG, String.format("onComplete Not Logined! No accesstoken,please login."));
                    }
                });
    }
}