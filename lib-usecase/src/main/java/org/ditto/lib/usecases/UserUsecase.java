package org.ditto.lib.usecases;

import org.ditto.lib.dbroom.CommandStatus;
import org.ditto.lib.dbroom.user.User;
import org.ditto.lib.dbroom.user.UserCommand;
import org.ditto.lib.dbroom.vo.VoLocation;
import org.ditto.lib.repository.RepositoryFascade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by admin on 2017/6/25.
 */

public class UserUsecase {
    private RepositoryFascade repositoryFascade;

    @Inject
    public UserUsecase(RepositoryFascade repositoryFascade) {
        this.repositoryFascade = repositoryFascade;
    }


    public List<User> load(int start, int number) {
        return new ArrayList<User>();
    }

    public Observable<Long> updateLatestLocation(VoLocation mylocation) {
        UserCommand command = UserCommand
                .builder()
                .setUuid(UUID.randomUUID().toString())
                .setContent(UserCommand.Content
                        .builder()
                        .setUpdateLatestLocation(
                                UserCommand.UpdateLatestLocation
                                        .builder()
                                        .setLat(mylocation.getLat())
                                        .setLon(mylocation.getLon())
                                        .build())
                        .build())
                .setCommandStatus(CommandStatus.NEW)
                .setCreated(System.currentTimeMillis())
                .build();
        return repositoryFascade.userRepository.save(command).subscribeOn(Schedulers.io());
    }
}
