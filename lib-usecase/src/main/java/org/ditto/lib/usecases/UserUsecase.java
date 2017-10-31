package org.ditto.lib.usecases;

import org.ditto.lib.repository.RepositoryFascade;

import javax.inject.Inject;

/**
 * Created by admin on 2017/6/25.
 */

public class UserUsecase {
    private RepositoryFascade repositoryFascade;

    @Inject
    public UserUsecase(RepositoryFascade repositoryFascade) {
        this.repositoryFascade = repositoryFascade;
    }
}
