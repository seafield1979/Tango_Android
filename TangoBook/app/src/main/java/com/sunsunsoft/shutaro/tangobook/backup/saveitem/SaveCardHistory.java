package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

import com.sunsunsoft.shutaro.tangobook.database.TangoCardHistory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by shutaro on 2017/06/27.
 *
 * TangoCardHistoryをファイルに保存、復元するためのクラス
 */

public class SaveCardHistory extends SaveItem {

    /**
     * Member Variables
     */

    /**
     * Constructor
     * @param buffer
     */
    public SaveCardHistory(ByteBuffer buffer) {
        super(buffer);
    }

    /**
     * バックアップファイルからTangoCardデータを読み込む
     * @param inputBuf  データを読み込む元のバイナリデータ
     * @return
     */
    public CHistory readData(ByteBuffer inputBuf) throws IOException {
        // カードデータのサイズを取得
        int size = inputBuf.getShort();

        mBuf.clear();
        // サイズ分読み込む
        inputBuf.get(mBuf.array(), 0, size);

        int cardId = mBuf.getInt();
        byte correctFlagNum = mBuf.get();
        byte[] correctFlags = new byte[10];
        mBuf.get(correctFlags, 0, correctFlagNum);
        Date studiedDate = readDate();

        CHistory history = new CHistory(cardId, correctFlagNum, correctFlags, studiedDate);

        return history;
    }


    /**
     * カードの学習情報を１件書き込む
     * @param output       書き込む先のファイル
     * @param history
     * @throws IOException
     */
    public void writeData(BufferedOutputStream output, TangoCardHistory history) throws IOException
    {
        mBuf.clear();

        // int cardId
        mBuf.putInt(history.getCardId());

        // int correctFlagNum
        mBuf.put((byte)history.getCorrectFlagNum());

        // byte[CORRECT_HISTORY_MAX] correctFlags
        mBuf.put(history.getCorrectFlags(), 0, history.getCorrectFlagNum());

        // Date studiedDate
        writeDate(history.getStudiedDate());

        // ファイルに書き込み(サイズ + 本体)
        writeShort(output, (short)mBuf.position());
        output.write(mBuf.array(), 0, mBuf.position());
    }

}
