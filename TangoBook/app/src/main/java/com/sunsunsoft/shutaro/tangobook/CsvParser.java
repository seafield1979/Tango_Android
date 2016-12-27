package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

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
    static PresetBook getPresetBook(Context context, int csvId, boolean onlyBook) {
        InputStream is = context.getResources().openRawResource(csvId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PresetBook book = null;

        try {
            boolean isFirst = true;
            String line;
            String[] words;

            while ((line = br.readLine()) != null) {
                words = line.split(",");

                if (isFirst) {
                    // 最初の行は単語帳データ
                    isFirst = false;

                    if (words.length >= 2) {
                        book = new PresetBook(context, csvId, words[0], words[1]);
                    }
                    if (onlyBook) {
                        break;
                    }
                }
                else {
                    if (words.length >= 3) {
                        PresetCard card = new PresetCard(words[0], words[1], words[2]);
                        book.addCard(card);
                    }
                }
            }

        } catch (IOException e) {

        }
        return book;
    }

    /**
     * カードのリストを取得する
     * @param context
     * @param csvId
     * @return
     */
    static LinkedList<PresetCard> getPresetCards(Context context, int csvId) {
        InputStream is = context.getResources().openRawResource(csvId);
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
                    words = line.split(",");
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
