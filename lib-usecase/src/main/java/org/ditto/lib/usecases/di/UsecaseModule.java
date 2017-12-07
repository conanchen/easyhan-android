package org.ditto.lib.usecases.di;

import org.ditto.lib.repository.RepositoryFascade;
import org.ditto.lib.repository.di.RepositoryModule;
import org.ditto.lib.usecases.UsecaseFascade;
import org.ditto.lib.usecases.UserUsecase;
import org.ditto.lib.usecases.WordUsecase;

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
                                                WordUsecase wordUsecase,
                                                RepositoryFascade repositoryFascade) {
        return new UsecaseFascade(  userUsecase, wordUsecase,repositoryFascade);
    }

    @Singleton
    @Provides
    public UserUsecase provideUserUsecase(RepositoryFascade repositoryFascade) {
        return new UserUsecase(repositoryFascade);
    }

    @Singleton
    @Provides
    public WordUsecase provideWordUsecase(RepositoryFascade repositoryFascade) {
        return new WordUsecase(repositoryFascade);
    }

}