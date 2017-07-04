package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shutaro on 2017/06/27.
 *
 * データをファイルに保存するクラスの親クラス
 */

abstract public class SaveItem {
    /**
     * Member Variables
     */
    ByteBuffer mBuf;
    private byte[] intBuf;  // BufferedInputStreamから int型のデータを取得するためのバッファ

    /**
     * Constructor
     * @param buffer
     */
    public SaveItem(ByteBuffer buffer) {
        mBuf = buffer;
        intBuf = new byte[4];
    }

    /**
     * Static Methods
     */
    /**
     * Date型のデータをバイナリ形式で書き込む
     * @param date     書き込む日付情報
     */
    public void writeDate(Date date) throws IOException {
        if (date == null) {
            // 全て0で書き込み
            mBuf.put(new byte[7], 0, 7);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            mBuf.putShort((short)calendar.get(Calendar.YEAR));
            mBuf.put((byte)calendar.get(Calendar.MONTH));
            mBuf.put((byte)calendar.get(Calendar.DAY_OF_MONTH));
            mBuf.put((byte)calendar.get(Calendar.HOUR));
            mBuf.put((byte)calendar.get(Calendar.MINUTE));
            mBuf.put((byte)calendar.get(Calendar.SECOND));
        }
    }

    /**
     * バイナリ形式のDateデータを読み込む
     */
    public Date readDate() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mBuf.getShort());
        calendar.set(Calendar.MONTH, mBuf.get());
        calendar.set(Calendar.DAY_OF_MONTH, mBuf.get());
        calendar.set(Calendar.HOUR, mBuf.get());
        calendar.set(Calendar.MINUTE, mBuf.get());
        calendar.set(Calendar.SECOND, mBuf.get());

        return calendar.getTime();
    }

    /**
     * 文字列を書き込む
     * @param str
     * @throws IOException
     */
    public void writeString(String str) throws IOException {
        if (str == null || str.length() == 0) {
            mBuf.putInt(0);
        } else {
            byte[] bytes = str.getBytes();
            mBuf.putInt(bytes.length);
            mBuf.put(bytes);
        }
    }

    /**
     * 文字列を読み込む
     * @return 読み込んだ文字列
     */
    public String readString() throws IOException {
        int strLen = mBuf.getInt();
        byte[] bytes = new byte[strLen];
        mBuf.get(bytes, 0, strLen);
        return new String(bytes);
    }

    /**
     * ファイルにShortの値(2byte)を書き込む
     * @param output   書き込み先のファイル
     * @param data     書き込みデータ
     * @throws IOException
     */
    public void writeShort(BufferedOutputStream output, short data) throws IOException {
        intBuf[0] = (byte)(data >> 8);
        intBuf[1] = (byte)(data & 0xff);

        output.write(intBuf, 0, 2);
    }

    /**
     * Shortの値(2byte)を読み込む
     * @param input
     * @return
     * @throws IOException
     */
    public short readShort(BufferedInputStream input) throws IOException {
        input.read(intBuf, 0, 4);

        return (short)((intBuf[0] << 8) | intBuf[1]);
    }

    /**
     * ファイルにIntの値(4byte)を書き込む
     * @param output   書き込み先のファイル
     * @param data     書き込みデータ
     * @throws IOException
     */
    public void writeInt(BufferedOutputStream output, short data) throws IOException {
        intBuf[0] = (byte)(data >> 24);
        intBuf[1] = (byte)(data >> 16);
        intBuf[2] = (byte)(data >> 8);
        intBuf[3] = (byte)(data & 0xff);

        output.write(intBuf, 0, 4);
    }

    /**
     * Intの値(4byte)を読み込む
     * @param input
     * @return
     */
    public int readInt(BufferedInputStream input) throws IOException {
        input.read(intBuf, 0, 4);

        return (intBuf[0] << 24) | (intBuf[1] << 16) | (intBuf[3] << 8) | intBuf[3];
    }
}
