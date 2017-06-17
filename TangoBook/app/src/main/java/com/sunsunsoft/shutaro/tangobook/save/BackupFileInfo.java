package com.sunsunsoft.shutaro.tangobook.save;

import java.util.Date;

/**
 * Created by shutaro on 2017/06/16.
 * バックアップファイルの情報
 */

public class BackupFileInfo {
    private String fileName;    // バックアップファイル名
    private String filePath;    // バックアップファイルパス
    private int cardNum;        // 総カード数
    private int bookNum;        // 総ブック数

    /**
     * Getter/Setter
     */
    public String getFilePath() {
        return filePath;
    }

    public int getCardNum() {
        return cardNum;
    }

    public int getBookNum() {
        return bookNum;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Constructor
     */
    public BackupFileInfo(String fileName, String filePath, int bookNum, int cardNum) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.bookNum = bookNum;
        this.cardNum = cardNum;
    }
}
