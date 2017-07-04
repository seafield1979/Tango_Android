package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

import com.sunsunsoft.shutaro.tangobook.database.TangoBook;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by shutaro on 2017/06/27.
 */

public class SaveBook extends SaveItem {

    /**
     * Member Variables
     */

    /**
     * Constructor
     * @param buffer
     */
    public SaveBook(ByteBuffer buffer) {
        super(buffer);
    }

    /**
     * 単語帳データを１件書き込む
     * @param output    書き込み先のファイル
     * @param book      書き込み単語帳
     * @throws IOException
     */
    public void writeData(BufferedOutputStream output, TangoBook book) throws IOException
    {
        mBuf.clear();

        // id
        mBuf.putInt(book.getId());

        // name
        writeString(book.getName());
        // comment
        writeString(book.getComment());
        // color
        mBuf.putInt(book.getColor());

        // createTime   作成日時
        writeDate(book.getCreateTime());
        // lastStudiedTime 最終学習日
        writeDate(book.getLastStudiedTime());

        // newFlag
        mBuf.put((byte)(book.isNewFlag() ? 1 : 0));

        // ファイルに書き込み(サイズ + 本体)
        writeShort(output, (short)mBuf.position());
        output.write(mBuf.array(), 0, mBuf.position());
    }

    /**
     * バックアップファイルからTangoBookデータを読み込む
     * @param inputBuf データを読み込む元のバイナリデータ
     * @return
     */
    public Book readData(ByteBuffer inputBuf) throws IOException{
        // データのサイズを取得
        int size = inputBuf.getShort();

        mBuf.clear();
        // サイズ分読み込む
        inputBuf.get(mBuf.array(), 0, size);

        // 読み込んだバッファからデータを取得
        int id = mBuf.getInt();
        String name = readString();
        String comment = readString();
        int color = mBuf.getInt();
        Date createDate = readDate();
        Date studiedDate = readDate();
        boolean newFlag = mBuf.get() == 0 ? false : true;

        Book book = new Book(id, name, comment, color, createDate, studiedDate, newFlag);
        return book;
    }
}
