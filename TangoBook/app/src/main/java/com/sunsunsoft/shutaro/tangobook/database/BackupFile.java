package com.sunsunsoft.shutaro.tangobook.database;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by shutaro on 2017/06/16.
 *
 * バックアップファイル情報
 * バックアップは複数作れるのでテーブルで管理する
 */

public class BackupFile extends RealmObject{
    @Index
    private int id;               // バックアップ番号 1,2,3...
    private boolean enabled;    // 使用中かどうか(アプリ初期化時にレコードを作成するため、レコードがあっても未使用状態もある）
    private String filePath;    // バックアップファイルパス
    private int cardNum;        // 総カード数
    private int bookNum;        // 総ブック数
    private Date dateTime;      // 保存日時

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    public int getBookNum() {
        return bookNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}