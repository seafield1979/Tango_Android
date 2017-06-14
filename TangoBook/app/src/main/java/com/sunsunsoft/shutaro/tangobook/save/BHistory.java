package com.sunsunsoft.shutaro.tangobook.save;

/**
 * Created by shutaro on 2017/06/14.
 */


import java.util.Date;

/**
 * TangoBookHistory保存用
 */
public class BHistory {
    private int id;
    private int bId;        // bookId
    private boolean learn;  // learned
    private int ok;         // okNum
    private int ng;         // ngNum
    private Date st;        // StudiedDateTime

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public int getBookId() {
        return bId;
    }

    public int getOkNum() {
        return ok;
    }

    public int getNgNum() {
        return ng;
    }

    public Date getStudiedDateTime() {
        return st;
    }

    /**
     * Constructor
     */
    public BHistory(){}
    public BHistory(int id, int bookId, int okNum, int ngNum, Date studiedTime) {
        this.id = id;
        this.bId = bookId;
        this.ok = okNum;
        this.ng = ngNum;
        this.st = studiedTime;
    }
}