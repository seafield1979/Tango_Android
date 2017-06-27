package com.sunsunsoft.shutaro.tangobook.save.saveitem;

import com.sunsunsoft.shutaro.tangobook.database.TangoCard;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by shutaro on 2017/06/27.
 *
 * TangoCardをバックアップファイルに書き込むためのクラス
 */

public class SaveCard extends SaveItem {

    /**
     * Member Variables
     */

    /**
     * Constructor
     * @param buffer
     */
    public SaveCard(ByteBuffer buffer) {
        super(buffer);
    }


    /**
     * カード情報(TangoCard)を１件分書き込む
     * @param output  書き込み先のファイル
     * @param card    書き込むカードデータ
     */
    public void writeData(BufferedOutputStream output, TangoCard card) throws IOException {
        mBuf.clear();

        // id
        mBuf.putInt(card.getId());

        // wordA
        // 長さと文字列
        writeString(card.getWordA());

        // wordB
        writeString(card.getWordB());

        // comment
        writeString(card.getComment());

        // createTime
        writeDate(card.getCreateTime());

        // updateTime
        writeDate(card.getUpdateTime());

        // lastStudiedTime
        writeDate(card.getLastStudiedTime());

        // color
        mBuf.putInt(card.getColor());

        // star
        mBuf.put((byte)(card.getStar() ? 1 : 0));

        // newFlag
        mBuf.put((byte)(card.isNewFlag() ? 1 : 0));

        // ファイルに書き込み(サイズ + 本体)
        writeShort(output, (short)mBuf.position());
        output.write(mBuf.array(), 0, mBuf.position());
    }


    /**
     * バックアップファイルからTangoCardデータを読み込む
     * @param inputBuf  データを読み込む元のバイナリデータ
     * @return
     */
    public Card readData(ByteBuffer inputBuf) throws IOException{
        // カードデータのサイズを取得
        int cardSize = inputBuf.getShort();

        mBuf.clear();
        // サイズ分読み込む
        inputBuf.get(mBuf.array(), 0, cardSize);

        // 読み込んだバッファからデータを取得
        int id = mBuf.getInt();
        String wordA = readString();
        String wordB = readString();
        String comment = readString();
        Date createTime = readDate();
        Date updateTime = readDate();
        Date studiedTime = readDate();
        int color = mBuf.getInt();
        boolean star = mBuf.get() == 0 ? false : true;
        boolean newFlag = mBuf.get() == 0 ? false : true;

        Card card = new Card(id, wordA, wordB, comment, createTime, updateTime,
                studiedTime, color, star, newFlag);

        return card;
    }


}
