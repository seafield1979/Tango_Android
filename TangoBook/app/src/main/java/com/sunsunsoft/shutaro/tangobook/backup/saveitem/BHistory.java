package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

/**
 * Created by shutaro on 2017/06/14.
 */


import java.util.Date;

/**
 * TangoBookHistory保存用
 */
public class BHistory {
    private int id;
    private int bookId;        // bookId
    private int okNum;         // okNum
    private int ngNum;         // ngNum
    private Date studiedDate;  // StudiedDateTime

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getOkNum() {
        return okNum;
    }

    public int getNgNum() {
        return ngNum;
    }

    public Date getStudiedDateTime() {
        return studiedDate;
    }

    /**
     * Constructor
     */
    public BHistory(){}
    public BHistory(int id, int bookId, int okNum, int ngNum, Date studiedDate) {
        this.id = id;
        this.bookId = bookId;
        this.okNum = okNum;
        this.ngNum = ngNum;
        this.studiedDate = studiedDate;
    }
}