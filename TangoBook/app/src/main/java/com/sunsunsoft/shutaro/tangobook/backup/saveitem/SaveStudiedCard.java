package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

import com.sunsunsoft.shutaro.tangobook.database.TangoStudiedCard;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by shutaro on 2017/06/27.
 *
 * バックアップファイルにTangoStudiedCardを保存、復元する処理を行うクラス
 */

public class SaveStudiedCard extends SaveItem {

    /**
     * Member Variables
     */

    /**
     * Constructor
     * @param buffer
     */
    public SaveStudiedCard(ByteBuffer buffer) {
        super(buffer);
    }

    /**
     * バックアップファイルからTangoCardデータを読み込む
     * @param inputBuf  データを読み込む元のバイナリデータ
     * @return
     */
    public StudiedC readData(ByteBuffer inputBuf) throws IOException{
        // カードデータのサイズを取得
        int size = inputBuf.getShort();

        mBuf.clear();
        // サイズ分読み込む
        inputBuf.get(mBuf.array(), 0, size);

        int historyId = mBuf.getInt();
        int cardId = mBuf.getInt();
        boolean okFlag = mBuf.get() == 0 ? false : true;

        StudiedC card = new StudiedC(historyId, cardId, okFlag);

        return card;
    }

    /**
     * カードの学習履歴を１件書き込む
     * @param output
     * @param card
     * @throws IOException
     */
    public void writeData(BufferedOutputStream output, TangoStudiedCard card) throws IOException
    {
        mBuf.clear();

        // int bookHistoryId
        mBuf.putInt(card.getBookHistoryId());

        // int cardId
        mBuf.putInt(card.getCardId());

        // boolean okFlag
        mBuf.put((byte)(card.isOkFlag() ? 1 : 0));

        // ファイルに書き込み(サイズ + 本体)
        writeShort(output, (short)mBuf.position());
        output.write(mBuf.array(), 0, mBuf.position());
    }

}
