package org.ditto.lib.usecases.di;

import org.ditto.lib.repository.RepositoryFascade;
import org.ditto.lib.repository.di.RepositoryModule;
import org.ditto.lib.usecases.UsecaseFascade;
import org.ditto.lib.usecases.UserUsecase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by amirziarati on 10/4/16.
 */
@Singleton
@Module(includes = {
        RepositoryModule.class
})
public class UsecaseModule {

    @Singleton
    @Provides
    public UsecaseFascade provideServiceFascade(
                                                UserUsecase userUsecase,
                                                RepositoryFascade repositoryFascade) {
        return new UsecaseFascade(  userUsecase, repositoryFascade);
    }

    @Singleton
    @Provides
    public UserUsecase provideUserUsecase(RepositoryFascade repositoryFascade) {
        return new UserUsecase(repositoryFascade);
    }

}