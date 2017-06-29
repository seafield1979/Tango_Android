package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.BackupFile;
import com.sunsunsoft.shutaro.tangobook.util.ConvDateMode;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;

import java.io.File;

/**
 * Created by shutaro on 2017/06/16.
 *
 * ListViewBackupDBに表示される項目
 */

public class ListItemBackup extends UListItem {
    /**
     * Enums
     */


    /**
     * Consts
     */
    // layout
    private static final int ITEM_H2 = 117;
    private static final int MARGIN_H = 10;
    private static final int MARGIN_V = 7;
    private static final int FRAME_WIDTH = 1;
    private static final int TEXT_SIZE = 17;
    private static final int TEXT_SIZE_S = 13;

    private static final int FRAME_COLOR = Color.BLACK;
    private static final int TEXT_COLOR = Color.BLACK;

    /**
     * Variables
     */
    private String mTitle;          // タイトル
    private String mText;           // バックアップ情報
    private BackupFile mBackup;

    /**
     * Getter/Setter
     */
    public void setText(String text) { mText = text; }
    public BackupFile getBackup() {
        return mBackup;
    }

    /**
     * Constructor
    */
    public ListItemBackup(UListItemCallbacks listItemCallbacks,
                          BackupFile backup,
                          float x, int width)
    {
        super(listItemCallbacks, true, x, width, UDpi.toPixel(ITEM_H2), UColor.WHITE, UDpi.toPixel(FRAME_WIDTH), FRAME_COLOR);

        mBackup = backup;

        // 自動バックアップと手動バックアップでタイトルの文字列が異なる
        if (backup.isAutoBackup()) {
            mTitle = UResourceManager.getStringById(R.string.backup_auto);
        } else {
            mTitle = String.format("%s%02d", UResourceManager.getStringById(R.string.backup), backup.getId());
        }

        // mText
        if (backup.isEnabled()) {
            File file = new File(backup.getFilePath());
            String filename = "";
            if (file != null) {
                filename = file.getName();
            }
            mText =  UUtil.convDateFormat(backup.getDateTime(), ConvDateMode.DateTime) + "\n" +
                    UResourceManager.getStringById(R.string.filename) +
                    " :  " + filename + "\n" +
                    UResourceManager.getStringById(R.string.card_count) +
                    " :  " + backup.getCardNum() + "\n" +
                    UResourceManager.getStringById(R.string.book_count) +
                    " :  " + backup.getBookNum() + "\n";

        } else {
            mText = UResourceManager.getStringById(R.string.empty);
        }
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        float y = _pos.y + UDpi.toPixel(MARGIN_V);

        super.draw(canvas, paint, _pos);

        // mTitle
        UDraw.drawTextOneLine(canvas, paint, mTitle, UAlignment.None, UDpi.toPixel(TEXT_SIZE_S),
                _pos.x + UDpi.toPixel(MARGIN_H), y, UColor.DarkBlue);
        y += UDpi.toPixel(TEXT_SIZE_S + MARGIN_V);

        // mText
        UDraw.drawText(canvas, mText, UAlignment.Center, UDpi.toPixel(TEXT_SIZE),
                _pos.x + size.width / 2,
                y + (size.height - UDpi.toPixel(TEXT_SIZE_S + MARGIN_V * 2)) / 2, TEXT_COLOR);
    }
}
