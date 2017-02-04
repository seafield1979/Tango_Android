package com.sunsunsoft.shutaro.tangobook;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/04.
 *
 * Bookの学習履歴
 * １つのBookに複数の履歴を持つことも可能
 */

public class TangoBookHistory extends RealmObject {
    /**
     * Constants
     */
    public static final int CARD_IDS_MAX = 100;

    @PrimaryKey
    private int id;

    @Index
    private int bookId;

    // OK数
    private int okNum;

    // NG数
    private int ngNum;

    // 学習日
    private Date studiedDateTime;

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Date getStudiedDateTime() {
        return studiedDateTime;
    }

    public void setStudiedDateTime(Date studyDateTime) {
        this.studiedDateTime = studyDateTime;
    }

    public int getOkNum() {
        return okNum;
    }

    public void setOkNum(int okNum) {
        this.okNum = okNum;
    }

    public int getNgNum() {
        return ngNum;
    }

    public void setNgNum(int ngNum) {
        this.ngNum = ngNum;
    }
}
