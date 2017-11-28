package org.ditto.lib.dbroom;

import android.arch.persistence.room.TypeConverter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.ditto.lib.dbroom.index.Pinyin;

import java.lang.reflect.Type;
import java.util.List;

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


    //---------------
    @TypeConverter
    public static String PinyinToString(Pinyin pinyin) {

        if (pinyin == null)
            return null;


        return gson.toJson(pinyin);
    }

    @TypeConverter
    public static Pinyin StringToPinyin(String s) {

        if (Strings.isNullOrEmpty(s))
            return null;


        return gson.fromJson(s,Pinyin.class);
    }


    @TypeConverter
    public static List<Pinyin> fromStringToPinyinList(String value) {
        Type listType = new TypeToken<List<Pinyin>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromPinyinListToString(List<Pinyin> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<String> fromStringToList(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStringListToString(List<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}