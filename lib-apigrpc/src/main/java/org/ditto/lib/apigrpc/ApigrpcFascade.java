package org.ditto.lib.apigrpc;


import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class ApigrpcFascade {

    @Inject
    String strAmir;

    WordService wordService;

    @Inject
    public ApigrpcFascade(WordService wordService) {
        this.wordService = wordService;
        System.out.println(strAmir);

    }


    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }

    public WordService getWordService() {
        return wordService;
    }
}