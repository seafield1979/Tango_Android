package com.sunsunsoft.shutaro.tangobook.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shutaro on 2017/06/26.
 */

public class URandomAccessFile extends RandomAccessFile {

    public URandomAccessFile(File file, String mode) throws FileNotFoundException{
        super(file, mode);
    }

    /**
     * Date型のデータをバイナリ形式で書き込む
     * @param date
     */
    public void writeDate(Date date) throws IOException {
        if (date == null) {
            // 全て0で書き込み
            write(new byte[7], 0, 7);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            this.writeShort(calendar.get(Calendar.YEAR));
            this.writeByte(calendar.get(Calendar.MONTH));
            this.writeByte(calendar.get(Calendar.DAY_OF_MONTH));
            this.writeByte(calendar.get(Calendar.HOUR));
            this.writeByte(calendar.get(Calendar.MINUTE));
            this.writeByte(calendar.get(Calendar.SECOND));
        }
    }

    /**
     * バイナリ形式のDateデータを読み込む
     */
    public Date readDate() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, this.readShort());
        calendar.set(Calendar.MONTH, this.readByte());
        calendar.set(Calendar.DAY_OF_MONTH, this.readByte());
        calendar.set(Calendar.HOUR, readByte());
        calendar.set(Calendar.MINUTE, readByte());
        calendar.set(Calendar.SECOND, readByte());

        return calendar.getTime();
    }

    /**
     * 文字列を書き込む
     * @param str
     * @throws IOException
     */
    public void writeString(String str) throws IOException {
        if (str == null || str.length() == 0) {
            writeInt(0);
        } else {
            byte[] bytes = str.getBytes();
            writeInt(bytes.length);
            writeBytes(str);
        }
    }

    /**
     * 文字列を読み込む
     * @return 読み込んだ文字列
     */
    public String readString() throws IOException {
        int strLen = readInt();
        byte[] bytes = new byte[strLen];
        read(bytes, 0, strLen);
        return new String(bytes);
    }
}
