package com.sunsunsoft.shutaro.tangobook.preset;

/**
 * Created by shutaro on 2017/06/14.
 */


import com.sunsunsoft.shutaro.tangobook.util.ULog;

/**
 * プリセット単語帳の中のカードクラス
 */
public class PresetCard {
    public String mWordA;
    public String mWordB;
    public String mComment;

    /**
     * Constructor
     */
    public PresetCard(String wordA, String wordB, String comment) {
        mWordA = wordA;
        mWordB = wordB;
        mComment = comment;
    }

    public void log() {
        ULog.print(PresetBookManager.TAG, "wordA:" + mWordA + " wordB:" + mWordB);
    }
}