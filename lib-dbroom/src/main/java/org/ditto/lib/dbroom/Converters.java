package org.ditto.lib.dbroom;

import android.arch.persistence.room.TypeConverter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Converters {
    private static final Gson gson = new GsonBuilder().create();

    //---------------
    @TypeConverter
    public static String CommandStatusToName(CommandStatus commandStatus) {

        if (commandStatus == null)
            return null;


        return commandStatus.name();
    }

    @TypeConverter
    public static CommandStatus NameToCommandStatus(String name) {

        if (Strings.isNullOrEmpty(name))
            return null;


        return CommandStatus.valueOf(name);
    }


}