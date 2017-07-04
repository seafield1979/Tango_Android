package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

import com.sunsunsoft.shutaro.tangobook.database.TangoItemPos;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by shutaro on 2017/06/27.
 *
 * TangoItemPosをバックアップファイルに保存、復元する処理を行うクラス
 */

public class SaveItemPos extends SaveItem {

    /**
     * Member Variables
     */

    /**
     * Constructor
     * @param buffer
     */
    public SaveItemPos(ByteBuffer buffer) {
        super(buffer);
    }

    /**
     * 単語アイテムの位置情報を1件書き込む
     * @param output    書き込み先のファイル
     * @param itemPos
     * @throws IOException
     */
    public void writeData(BufferedOutputStream output, TangoItemPos itemPos) throws IOException
    {
        mBuf.clear();

        // int parentType
        mBuf.put((byte)itemPos.getParentType());

        // int parentId
        mBuf.putInt(itemPos.getParentId());

        // int pos
        mBuf.putInt(itemPos.getPos());

        // int itemType
        mBuf.put((byte)itemPos.getItemType());

        // int itemId
        mBuf.putInt(itemPos.getItemId());

        // ファイルに書き込み(サイズ + 本体)
        writeShort(output, (short)mBuf.position());
        output.write(mBuf.array(), 0, mBuf.position());
    }

    /**
     * バックアップファイルからTangoBookデータを読み込む
     * @param inputBuf  データを読み込む元のバイナリデータ
     * @return
     */
    public Pos readData(ByteBuffer inputBuf) throws IOException {
        // カードデータのサイズを取得
        int size = inputBuf.getShort();

        mBuf.clear();
        // サイズ分読み込む
        inputBuf.get(mBuf.array(), 0, size);

        byte parentType = mBuf.get();
        int parentId = mBuf.getInt();
        int position = mBuf.getInt();
        byte itemType = mBuf.get();
        int itemId = mBuf.getInt();

        Pos pos = new Pos(parentType, parentId, position, itemType, itemId);
        return pos;
    }
}
