package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

/**
 * Created by shutaro on 2017/06/14.
 */

import java.util.Date;

/**
 * TangoCardHistory保存用
 */
public class CHistory {
    private int cardId;
    private int correctFlagNum;
    private byte[] correctFlags = new byte[10];
    private Date studiedDate;

    /**
     * Get/Set
     */
    public int getCardId() {
        return cardId;
    }

    public int getCorrectFlagNum() {
        return correctFlagNum;
    }

    public byte[] getCorrectFlag() {
        return correctFlags;
    }

    public Date getStudiedDate() {
        return studiedDate;
    }

    /**
     * Constructor
     */
    public CHistory(){}
    public CHistory(int cardId, int correctFlagNum, byte[] correctFlag, Date studiedDate){
        this.cardId = cardId;
        this.correctFlagNum = correctFlagNum;
        this.correctFlags = correctFlag;
        this.studiedDate = studiedDate;
    }
}