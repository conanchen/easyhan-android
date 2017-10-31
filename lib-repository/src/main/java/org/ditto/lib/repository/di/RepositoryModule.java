package org.ditto.lib.repository.di;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.di.ApigrpcModule;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.di.RoomModule;
import org.ditto.lib.repository.IndexVisitorRepository;
import org.ditto.lib.repository.KeyValueRepository;
import org.ditto.lib.repository.RepositoryFascade;
import org.ditto.lib.repository.UserRepository;
import org.ditto.lib.repository.WordRepository;
import org.ditto.lib.system.SystemService;
import org.ditto.lib.system.di.SystemModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by amirziarati on 10/4/16.
 */
@Singleton
@Module(includes = {
        RoomModule.class,
        SystemModule.class,
        ApigrpcModule.class
})
public class RepositoryModule {

    @Singleton
    @Provides
    public RepositoryFascade provideRepositoryFascade(
            UserRepository userRepository,
            WordRepository wordRepository,
            KeyValueRepository keyValueRepository,
            IndexVisitorRepository indexVisitorRepository,
            SystemService systemService
    ) {
        return new RepositoryFascade(
                userRepository,
                wordRepository,
                keyValueRepository,
                indexVisitorRepository,
                systemService);
    }


    @Singleton
    @Provides
    public UserRepository provideUserRepository(
            ApigrpcFascade apigrpcFascade,
            RoomFascade roomFascade) {
        return new UserRepository(apigrpcFascade, roomFascade);
    }


    @Singleton
    @Provides
    public WordRepository provideWordRepository(
            ApigrpcFascade apigrpcFascade,
            RoomFascade roomFascade) {
        return new WordRepository(apigrpcFascade, roomFascade);
    }


    @Singleton
    @Provides
    public KeyValueRepository provideKeyValueRepository(
            RoomFascade roomFascade) {
        return new KeyValueRepository(roomFascade);
    }

    @Singleton
    @Provides
    public IndexVisitorRepository provideIndexVisitorRepository(
            RoomFascade roomFascade) {
        return new IndexVisitorRepository(roomFascade);
    }
}