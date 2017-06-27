package com.sunsunsoft.shutaro.tangobook.save.saveitem;

import com.sunsunsoft.shutaro.tangobook.database.TangoBookHistory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by shutaro on 2017/06/27.
 *
 * TangoBookHistory をバックアップファイルに保存、復元するためのクラス
 */

public class SaveBookHistory extends SaveItem {

    /**
     * Member Variables
     */

    /**
     * Constructor
     * @param buffer
     */
    public SaveBookHistory(ByteBuffer buffer) {
        super(buffer);
    }

    /**
     * バックアップファイルからTangoCardデータを読み込む
     * @param inputBuf データを読み込む元のバイナリデータ
     * @return
     */
    public BHistory readData(ByteBuffer inputBuf) throws IOException{
        // カードデータのサイズを取得
        int size = inputBuf.getShort();

        mBuf.clear();
        // サイズ分読み込む
        inputBuf.get(mBuf.array(), 0, size);

        int id = mBuf.getInt();
        int bookId = mBuf.getInt();
        short okNum = mBuf.getShort();
        short ngNum = mBuf.getShort();
        Date studiedTime = readDate();

        BHistory history = new BHistory(id, bookId, okNum, ngNum, studiedTime);

        return history;
    }

    /**
     * 単語帳の学習情報を１件書き込む
     * @param output       書き込み先のファイル
     * @param history
     * @throws IOException
     */
    public void writeData(BufferedOutputStream output, TangoBookHistory history) throws IOException
    {
        mBuf.clear();

        // int id
        mBuf.putInt(history.getId());

        // int bookId
        mBuf.putInt(history.getBookId());

        // int okNum
        mBuf.putShort((short)history.getOkNum());

        // int ngNum
        mBuf.putShort((short)history.getNgNum());

        // Date studiedDateTime
        writeDate(history.getStudiedDateTime());

        // ファイルに書き込み(サイズ + 本体)
        writeShort(output, (short)mBuf.position());
        output.write(mBuf.array(), 0, mBuf.position());
    }
}
