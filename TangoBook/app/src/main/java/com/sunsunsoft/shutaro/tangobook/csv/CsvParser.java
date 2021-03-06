package com.sunsunsoft.shutaro.tangobook.csv;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.preset.PresetBook;
import com.sunsunsoft.shutaro.tangobook.preset.PresetCard;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/27.
 *
 * Csvを解析して単語帳やカードの情報を抜き出す
 *
 */

public class CsvParser {
    /**
     * Constants
     */
    public static final String TAG = "CsvParser";


    /**
     * PresetBookを取得
     * csvファイルの１行目にBook名、コメントの順で格納されているのを取得する
     * @param context
     * @param csvId
     * @param onlyBook  Book情報のみ取得、Card情報は取得しない
     * @return
     */
    public static PresetBook getPresetBook(Context context, int csvId, boolean onlyBook) {
        InputStream is = context.getResources().openRawResource(csvId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PresetBook book = null;

        try {
            boolean isFirst = true;
            String line;
            String[] words;

            while ((line = br.readLine()) != null) {
                words = splitCsvLine(line);

                if (isFirst) {
                    // 最初の行は単語帳データ
                    isFirst = false;

                    if (words.length >= 3) {
                        int color = Color.parseColor(words[2]);
                        book = new PresetBook(context, csvId, words[0], words[1], color);
                    }
                    else if (words.length >= 2) {
                        book = new PresetBook(context, csvId, words[0], words[1], 0);
                    } else if (words.length >= 1) {
                        book = new PresetBook(context, csvId, words[0], null, 0);
                    }
                    if (onlyBook) {
                        break;
                    }
                }
                else {
                    PresetCard card;

                    if (words.length >= 2) {
                        if (words.length >= 3) {
                            card = new PresetCard(words[0], words[1], words[2]);
                        } else {
                            card = new PresetCard(words[0], words[1], null);
                        }
                        book.addCard(card);
                    }
                }
            }
        } catch (IOException e) {

        }
        return book;
    }

    /**
     * CSVファイルの１行をカンマで分割する
     * "~"で囲まれる文字の中のカンマは区切り文字として使用しない
     * @param str
     * @return
     */
    private static String[] splitCsvLine(String str) {
        LinkedList<String> list = new LinkedList<>();
        StringBuffer buf = new StringBuffer();
        boolean seekDQ = false;

        for (String ch : str.split("")) {       // １文字づつ処理する
            if (seekDQ) {
                if (ch.equals("\"")) {
                    seekDQ = false;
                    list.add(decodeCsv(buf.toString()));
                    buf.setLength(0);
                }
                else {
                    buf.append(ch);
                }
            }
            else {
                // " を見つけたら次の"を見つけるまでカンマスキップモード
                if (ch.equals("\"")) {
                    seekDQ = true;
                } else if (ch.equals(",")) {
                    if (buf.length() > 0) {
                        list.add(decodeCsv(buf.toString()));
                        buf.setLength(0);
                    }
                } else {
                    buf.append(ch);
                }
            }
        }
        if (buf.length() > 0) {
            // "\n" を改行に変換してからリストに追加する
            list.add( decodeCsv(buf.toString()));
            buf.setLength(0);
        }

        return list.toArray(new String[0]);
    }

    /**
     * CSV中のワードをデコードする
     * @param word
     * @return
     */
    private static String decodeCsv(String word) {
        // \nを改行に変換
        return word.replace("\\n", "\n");
    }

    /**
     * CSVファイルを読み込む
     * @param context
     * @param file
     * @param onlyBook  Book情報のみ取得、Card情報は取得しない
     * @return csvファイルから読み込んで作成したPresetBook
     *          null: 読み込み失敗
     */
    public static PresetBook getFileBook(Context context, File file, boolean onlyBook) {
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            PresetBook book = null;

            boolean isFirst = true;
            String line;
            String[] words;

            while ((line = br.readLine()) != null) {
                words = splitCsvLine(line);

                if (isFirst) {
                    // 最初の行は単語帳データ
                    isFirst = false;

                    if (words.length >= 1) {
                        int color = 0;
                        if (words.length >= 3) {
                            color = UColor.parseColor(words[2]);
                        }
                        if (words.length >= 2) {
                            book = new PresetBook(context, file, words[0], words[1], color);
                        } else {
                            book = new PresetBook(context, file, words[0], null, 0);
                        }
                    } else {
                        return null;
                    }
                    if (onlyBook) {
                        break;
                    }
                }
                else {
                    // ２行目以降はカードデータ
                    if (words.length >= 2) {
                        PresetCard card;
                        if (words.length >= 3) {
                            card = new PresetCard(words[0], words[1], words[2]);
                        } else {
                            card = new PresetCard(words[0], words[1], null);
                        }
                        book.addCard(card);
                    }
                }
            }
            return book;
        } catch (IOException e) {

        }
        return null;
    }

    /**
     * カードのリストを取得する
     * @param context
     * @param csvId
     * @return
     */
    static public LinkedList<PresetCard> getPresetCards(Context context, int csvId) {
        InputStream is = context.getResources().openRawResource(csvId);
        return getPresetCards(is);
    }

    static public LinkedList<PresetCard> getPresetCards(File file) {
        try {
            InputStream is = new FileInputStream(file);
            return getPresetCards( is);
        } catch (IOException e) {

        }
        return null;
    }


    static LinkedList<PresetCard> getPresetCards(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        LinkedList<PresetCard> cards = new LinkedList<>();

        try {
            boolean isFirst = true;
            String line;
            String[] words;

            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    // 最初の行は単語帳データ
                    isFirst = false;
                }
                else {
                    words = splitCsvLine(line);
                    if (words.length >= 2) {
                        String wordA = (words.length >= 1) ? words[0] : "";
                        String wordB = (words.length >= 2) ? words[1] : "";
                        String comment = (words.length >= 3) ? words[2] : "";

                        PresetCard card = new PresetCard(wordA, wordB, comment);
                        cards.add(card);
                    }
                }
            }

        } catch (IOException e) {

        }
        return cards;
    }


    static void test1(Context context, int csvId) {
        // res/book.xmlを生ファイルとして開く
        InputStream is = context.getResources().openRawResource(csvId);

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, "line:" + line);
            }
        } catch (IOException e) {

        }

    }
}
