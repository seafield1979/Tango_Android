package com.sunsunsoft.shutaro.tangobook;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import java.util.Date;

/**
 * Created by shutaro on 2016/12/04.
 *
 * Bookの学習履歴
 * １つのBookに複数の履歴を持つことも可能
 */

public class TangoBookHistory extends RealmObject {
    @Index
    private int bookId;

    // 覚え済みフラグ(ユーザー自身がON/OFFする)
    private boolean learned;

    // 過去のOK数
    private int okNum;

    // 過去のNG数
    private int ngNum;

    // 正解率
    private float correctRatio;

    // 学習日
    private Date studiedDateTime;


    /**
     * Get/Set
     */
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public boolean isLearned() {
        return learned;
    }

    public void setLearned(boolean learned) {
        this.learned = learned;
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

    public float getCorrectRatio() {
        return correctRatio;
    }

    public void setCorrectRatio(float correctRatio) {
        this.correctRatio = correctRatio;
    }
}