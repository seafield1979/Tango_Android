package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.BackupFile;
import com.sunsunsoft.shutaro.tangobook.save.XmlManager;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;

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
    private static final int ITEM_H2 = 350;
    private static final int MARGIN_H = 30;
    private static final int MARGIN_V = 20;
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_COLOR = Color.BLACK;
    private static final int TEXT_SIZE = 50;
    private static final int TEXT_SIZE_S = 40;
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
        super(listItemCallbacks, true, x, width, ITEM_H2, UColor.WHITE);

        mBackup = backup;

        // 自動バックアップと手動バックアップでタイトルの文字列が異なる
        if (backup.isAutoBackup()) {
            mTitle = UResourceManager.getStringById(R.string.backup_auto);
        } else {
            mTitle = String.format("%s%02d", UResourceManager.getStringById(R.string.backup), backup.getId());
        }

        // mText
        if (backup.isEnabled()) {
            mText = XmlManager.getInstance().getManualXmlInfo(backup.getId());
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
        float y = _pos.y + MARGIN_V;

        // BG　タッチ中は色を変更
        int _color = color;
        if (isTouchable && isTouching) {
            _color = pressedColor;
        }
        UDraw.drawRectFill(canvas, paint,
           new Rect((int) _pos.x, (int) _pos.y,
                (int) _pos.x + size.width, (int) _pos.y + size.height),
        _color, FRAME_WIDTH, FRAME_COLOR);

        // mTitle
        UDraw.drawTextOneLine(canvas, paint, mTitle, UAlignment.None, TEXT_SIZE_S,
                _pos.x + MARGIN_H, y, UColor.DarkBlue);
        y += TEXT_SIZE_S + MARGIN_V;

        // mText
        UDraw.drawText(canvas, mText, UAlignment.Center, TEXT_SIZE,
                _pos.x + size.width / 2, y + (size.height - TEXT_SIZE_S - MARGIN_V * 2) / 2, TEXT_COLOR);
    }
}
