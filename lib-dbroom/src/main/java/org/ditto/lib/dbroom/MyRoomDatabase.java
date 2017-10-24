package org.ditto.lib.dbroom;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.TypeConverters;

import org.ditto.lib.dbroom.index.DaoIndexVisitor;
import org.ditto.lib.dbroom.index.DaoWord;
import org.ditto.lib.dbroom.index.IndexVisitor;
import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.dbroom.kv.DaoKeyValue;
import org.ditto.lib.dbroom.kv.KeyValue;
import org.ditto.lib.dbroom.kv.ValueConverters;
import org.ditto.lib.dbroom.user.ConverterUser;
import org.ditto.lib.dbroom.user.DaoUser;
import org.ditto.lib.dbroom.user.Myprofile;
import org.ditto.lib.dbroom.user.User;
import org.ditto.lib.dbroom.user.UserCommand;


@Database(entities =
        {
                Myprofile.class,
                User.class,
                UserCommand.class,
                Word.class,
                KeyValue.class,
                IndexVisitor.class
        }, version = 1)
@TypeConverters({ValueConverters.class, Converters.class, ConverterUser.class})
public abstract class MyRoomDatabase extends android.arch.persistence.room.RoomDatabase {
    public abstract DaoUser daoUser();

    public abstract DaoWord daoWord();

    public abstract DaoKeyValue daoKeyValue();

    public abstract DaoIndexVisitor daoPartyIndex();

}