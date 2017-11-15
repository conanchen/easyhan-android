package org.ditto.feature.base;

/**
 * Created by admin on 2017/11/9.
 */

public class WordUtils {
    private static String[][] STROKES = new String[][]{
            {"点","丶","\u31D4"},
            {"横","一","\u31D0"},
            {"竖","丨","\u31D1"},
            {"撇","丿","\u31D2"},
            {"捺","乀","\u31CF"},
            {"提","㇀","\u31C0"},
            {"横折","𠃍","\u31D5"},
            {"竖折","𠃊","\u31D7"},
            {"竖提","𠄌","\u31D9"},
            {"竖钩","亅","\u31DA"},
            {"横钩","乛","\u31D6"},
            {"弯钩","㇁","\u31C1"},
            {"竖弯钩","乚","\u31DF"},
            {"斜钩","㇂","\u31C2"},
            {"卧钩","㇃","\u31C3"},
            {"横折钩","","\u31C6"},
            {"横折弯钩","㇈","\u31C8"},
            {"横斜弯钩","乙","\u31E0"},
            {"横斜钩","⺄","\u2E84"},
            {"竖折折钩","㇉","\u31C9"},
            {"横折折折钩","𠄎","\u31E1"},
            {"横撇弯钩","㇌","\u31CC"},
            {"竖弯","㇄","\u31C4"},
            {"横折弯","㇍","\u31CD"},
            {"横折折","㇅","\u31C5"},
            {"横折提","㇊","\u31CA"},
            {"横撇","㇇","\u31C7"},
            {"横折折撇","㇋","\u31CB"},
            {"竖折撇","ㄣ","\u3123"},
            {"竖折折","𠃑","\u31DE"},
            {"撇点","𡿨","\u31DB"},
            {"撇折","𠃋","\u31DC"},
            {"横折折折","㇎","\u31CE"},
            {"竖撇","\u31D3","\u31D3"},
            {"竖弯折","\u31D8","\u31D8"},
            {"提捺","\u31DD","\u31DD"},
            {"撇钩","\u31E2","\u31E2"}
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
