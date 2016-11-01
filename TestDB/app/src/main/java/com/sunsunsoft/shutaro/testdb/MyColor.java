package com.sunsunsoft.shutaro.testdb;

import android.util.Log;

import java.util.Random;

/**
 * 色処理のあれこれ
 */

public class MyColor {

    /**
     * intのColorをランダムで取得する
     * @return
     */
    public static int getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return (r << 16) | (g << 8) | b;
    }

    /**
     * #001122 のような文字列の色をランダムで取得する
     * @return
     */
    public static String getRandomColorStr() {
        int color = getRandomColor();
        return String.format("#%06x", color);
    }

    /**
     * #001122 のような文字列の色指定を int に変換する
     * @param intStr
     * @return
     */
    public static int convStrToInt(String intStr) {
        // 先頭が # なら取り除く
        int val = 0;
        Log.d("mylog", intStr.substring(0,1));
        if (intStr.substring(0,1).equals("#")) {
            intStr = intStr.substring(1);
        }
        if (intStr.length() < 6) return 0;

        int color = (hex2int(intStr.substring(0, 2)) << 16) |
                (hex2int(intStr.substring(2, 4)) << 8) |
                hex2int(intStr.substring(4, 6));
        Log.d("mylog", "" + color);

        return (hex2int(intStr.substring(0, 2)) << 16) |
                (hex2int(intStr.substring(2, 4)) << 8) |
                hex2int(intStr.substring(4, 6));
    }

    /**
     * 0x112233 のようにアルファがない色の値に対してアルファを追加する
     * @param color
     * @return
     */
    public static int addAlpha(int color, int alpha) {
        return color | (alpha << 24);
    }

    private static int hex2int(String s){
        int v=0;
        try {
            v=Integer.parseInt(s,16);
        }catch (Exception e){
            v=0;
        }
        return v;
    }
}
