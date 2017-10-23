package org.ditto.lib.usecases;

import org.ditto.lib.repository.RepositoryFascade;

import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class UsecaseFascade {


    @Inject
    String strAmir;

    public UserUsecase userUsecase;
    public RepositoryFascade repositoryFascade;


    @Inject
    public UsecaseFascade(  UserUsecase userUsecase, RepositoryFascade repositoryFascade) {
        this.userUsecase = userUsecase;
        this.repositoryFascade = repositoryFascade;
        System.out.println(strAmir);

    }

    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }


}