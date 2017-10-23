package org.ditto.lib.repository;

import org.ditto.lib.system.SystemService;

import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class RepositoryFascade {


    @Inject
    String strAmir;

    public WordRepository wordRepository;
    public IndexVisitorRepository indexVisitorRepository;
    public SystemService systemService;
    public UserRepository userRepository;


    @Inject
    public RepositoryFascade(UserRepository userRepository,
                             WordRepository wordRepository,
                             IndexVisitorRepository indexVisitorRepository,
                             SystemService systemService) {
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.indexVisitorRepository = indexVisitorRepository;
        this.systemService = systemService;
        System.out.println(strAmir);

    }

    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }

}