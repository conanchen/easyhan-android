package org.ditto.lib.dbroom;

import org.ditto.lib.dbroom.index.DaoWord;
import org.ditto.lib.dbroom.index.DaoIndexVisitor;
import org.ditto.lib.dbroom.kv.DaoKeyValue;
import org.ditto.lib.dbroom.user.DaoUser;

import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class RoomFascade {

    public final DaoUser daoUser;
    public final DaoWord daoWord;
    public final DaoKeyValue daoKeyValue;
    public final DaoIndexVisitor daoIndexVisitor;
    @Inject
    String strAmir;


    @Inject
    public RoomFascade(DaoUser daoUser,
                       DaoWord daoWord,
                       DaoKeyValue daoKeyValue,
                       DaoIndexVisitor daoIndexVisitor) {
        this.daoUser = daoUser;
        this.daoWord = daoWord;
        this.daoKeyValue = daoKeyValue;
        this.daoIndexVisitor = daoIndexVisitor;
        System.out.println(strAmir);

    }

    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }


}