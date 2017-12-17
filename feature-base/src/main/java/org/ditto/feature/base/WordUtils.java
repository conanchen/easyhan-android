package org.ditto.feature.base;

/**
 * Created by admin on 2017/11/9.
 */

public class WordUtils {
    private static String[][] ms = new String[][]{
            {"〇", "此时与君未相识"},
            {"①", "与君始相识"},
            {"②", "回头忘相识"},
            {"③", "忆初相识到今朝"},
            {"④", "相识虽新有故情"},
            {"⑤", "十五即相识"},
            {"⑥", "村中相识久"},
            {"⑦", "恨与卢君相识迟"}
    };

    public static String getTitleByMemIdx(int memIdx) {
        if (memIdx > -1 && memIdx < 7) {
            return ms[memIdx][0];
        } else {
            return ms[7][0];
        }
    }

    public static String getDescByMemIdx(int memIdx) {
        if (memIdx > -1 && memIdx < 7) {
            return ms[memIdx][1];
        } else {
            return ms[7][1];
        }
    }
}
