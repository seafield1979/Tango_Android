package com.sunsunsoft.shutaro.tangobook.save;

/**
 * Created by shutaro on 2017/06/14.
 */

import java.util.Date;

/**
 * TangoCardHistory保存用
 */
public class CHistory {
    private int cId;             // cardId
    private int cfn;     // correctFlagNum
    private byte[] cf = new byte[10];     // correctFlags
    private Date date;       // studiedDate

    /**
     * Get/Set
     */
    public int getCardId() {
        return cId;
    }

    public int getCorrectFlagNum() {
        return cfn;
    }

    public byte[] getCorrectFlag() {
        return cf;
    }

    public Date getStudiedDate() {
        return date;
    }

    /**
     * Constructor
     */
    public CHistory(){}
    public CHistory(int cardId, int correctFlagNum, byte[] correctFlag, Date studiedDate){
        this.cId = cardId;
        this.cfn = correctFlagNum;
        this.cf = correctFlag;
        this.date = studiedDate;
    }
}