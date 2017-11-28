package org.ditto.feature.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/11/9.
 */

public class WordUtils {
    public static Map<Integer, String[]> STROKES = new HashMap<Integer, String[]>() {
        {
            //keycode as key
            put(12756,new String[]{"点", "丶", "\u31D4"});
            put(12752,new String[]{"横", "一", "\u31D0"});
            put(12753,new String[]{"竖", "丨", "\u31D1"});
            put(12754,new String[]{"撇", "丿", "\u31D2"});
            put(12751,new String[]{"捺", "乀", "\u31CF"});
            put(12736,new String[]{"提", "㇀", "\u31C0"});
            put(12757,new String[]{"横折", "𠃍", "\u31D5"});
            put(12759,new String[]{"竖折", "𠃊", "\u31D7"});
            put(12761,new String[]{"竖提", "𠄌", "\u31D9"});
            put(12762,new String[]{"竖钩", "亅", "\u31DA"});
            put(12758,new String[]{"横钩", "乛", "\u31D6"});
            put(12737,new String[]{"弯钩", "㇁", "\u31C1"});
            put(12767,new String[]{"竖弯钩", "乚", "\u31DF"});
            put(12738,new String[]{"斜钩", "㇂", "\u31C2"});
            put(12739,new String[]{"卧钩", "㇃", "\u31C3"});
            put(12742,new String[]{"横折钩", "", "\u31C6"});
            put(12744,new String[]{"横折弯钩", "㇈", "\u31C8"});
            put(12768,new String[]{"横斜弯钩", "乙", "\u31E0"});
            put(11908,new String[]{"横斜钩", "⺄", "\u2E84"});
            put(12745,new String[]{"竖折折钩", "㇉", "\u31C9"});
            put(12769,new String[]{"横折折折钩", "𠄎", "\u31E1"});
            put(12748,new String[]{"横撇弯钩", "㇌", "\u31CC"});
            put(12740,new String[]{"竖弯", "㇄", "\u31C4"});
            put(12749,new String[]{"横折弯", "㇍", "\u31CD"});
            put(12741,new String[]{"横折折", "㇅", "\u31C5"});
            put(12746,new String[]{"横折提", "㇊", "\u31CA"});
            put(12743,new String[]{"横撇", "㇇", "\u31C7"});
            put(12747,new String[]{"横折折撇", "㇋", "\u31CB"});
            put(12579,new String[]{"竖折撇", "ㄣ", "\u3123"});
            put(12766,new String[]{"竖折折", "𠃑", "\u31DE"});
            put(12763,new String[]{"撇点", "𡿨", "\u31DB"});
            put(12764,new String[]{"撇折", "𠃋", "\u31DC"});
            put(12750,new String[]{"横折折折", "㇎", "\u31CE"});
            put(12755,new String[]{"竖撇", "\u31D3", "\u31D3"});
            put(12760,new String[]{"竖弯折", "\u31D8", "\u31D8"});
            put(12765,new String[]{"提捺", "\u31DD", "\u31DD"});
            put(12770,new String[]{"撇钩", "\u31E2", "\u31E2"});

        }
    };

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
        if (memIdx < 7) {
            return ms[memIdx][0];
        } else {
            return ms[7][0];
        }
    }

    public static String getDescByMemIdx(int memIdx) {
        if (memIdx < 7) {
            return ms[memIdx][1];
        } else {
            return ms[7][1];
        }
    }

    public static String[] toUNICODE(String[] lines) {
        String[] result = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            result[i] = toUNICODE(lines[i]);
        }
        return result;
    }

    public static String toUNICODE(String line) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\n') {
                sb.append("\n");
            } else {
                sb.append(" U+" + Integer.toHexString(line.charAt(i)));
            }
        }
        return sb.toString();
    }


}
