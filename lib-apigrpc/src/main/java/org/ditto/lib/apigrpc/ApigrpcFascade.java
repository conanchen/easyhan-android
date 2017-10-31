package org.ditto.lib.apigrpc;


import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class ApigrpcFascade {

    @Inject
    String strAmir;

    WordService wordService;
    SigninService signinService;

    @Inject
    public ApigrpcFascade(WordService wordService,SigninService signinService) {
        this.wordService = wordService;
        this.signinService = signinService;
        System.out.println(strAmir);

    }


    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }

    public WordService getWordService() {
        return wordService;
    }

    public SigninService getSigninService() {
        return signinService;
    }
}