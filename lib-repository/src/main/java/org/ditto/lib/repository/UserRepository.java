package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;

import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.user.Myprofile;
import org.ditto.lib.dbroom.user.User;
import org.ditto.lib.dbroom.user.UserCommand;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository that handles VoUser objects.
 */
@Singleton
public class UserRepository {

    private RoomFascade roomFascade;

    @Inject
    public UserRepository(  RoomFascade roomFascade) {
        this.roomFascade = roomFascade;
    }


    public LiveData<User> findUserByLogin(String login) {
        return roomFascade.daoUser.load(login);
    }

    public Observable<Long> save(UserCommand command) {
        return Observable.fromCallable(
                () -> roomFascade.daoUser.save(command)
        ).subscribeOn(Schedulers.io());
    }

    public  LiveData<Myprofile> loadProfile(String type,int size) {
        return roomFascade.daoUser.loadProfile(type,size);
    }

    public  LiveData<List<Myprofile>> loadAllProfiles() {
        return roomFascade.daoUser.loadAllProfiles();
    }
}