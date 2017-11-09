package org.ditto.feature.my.index;

/**
 * Created by admin on 2017/11/9.
 */

public class WordUtils {
    private static String[] ms = new String[]{"〇", "①", "②", "③", "④", "⑤", "⑥", "⑦"};

    public static String getTitleByMemIdx(int memIdx) {
        if (memIdx < 7) {
            return ms[memIdx];
        } else {
            return ms[7];
        }
    }


}
