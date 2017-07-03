package com.sunsunsoft.shutaro.tangobook.preset;

/**
 * Created by shutaro on 2017/06/14.
 *
 * プリセット単語帳を保持するクラス
 */

import android.content.Context;

import com.sunsunsoft.shutaro.tangobook.csv.CsvParser;
import com.sunsunsoft.shutaro.tangobook.util.ULog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class PresetBook {
    public String mName;
    public String mComment;
    public int mColor;
    private Context mContext;
    private int mCsvId = -1;
    private File mFile;

    private LinkedList<PresetCard> mCards;

    /**
     * Get/Set
     */
    public List<PresetCard> getCards() {
        if (mCards == null) {
            if (mCsvId != -1) {
                mCards = CsvParser.getPresetCards(PresetBookManager.getInstance().getContext(),
                        mCsvId);
            } else if (mFile != null){
                mCards = CsvParser.getPresetCards(mFile);
            }
        }
        return mCards;
    }
    public String getFileName() {
        if (mFile != null) {
            return "(" + mFile.getName() + ")";
        }
        return "";
    }

    /**
     * Constructor
     */
    // アプリ内のCSVから追加する
    public PresetBook(Context context, int csvId, String name, String comment, int color) {
        mContext = context;
        mCsvId = csvId;
        mName = name;
        mComment = comment;
        mColor = color;
    }
    // ストレージにあるCSVから追加する
    public PresetBook(Context context, File file, String name, String comment, int color) {
        mContext = context;
        mName = name;
        mFile = file;
        mComment = comment;
        mColor = color;
    }

    public void addCard(PresetCard card) {
        mCards.add(card);
    }

    public void log() {
        ULog.print(PresetBookManager.TAG, "bookName:" + mName + " comment:" + mComment);
    }

}