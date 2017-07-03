package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.preset.PresetBook;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.uview.FontSize;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonImage;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;

import static com.sunsunsoft.shutaro.tangobook.icon.UIconWindow.ICON_W;

/**
 * Created by shutaro on 2016/12/18.
 * <p>
 * プリセット単語帳ListViewのアイテム
 * プリセット追加用のボタンが表示されている
 *
 */

public class ListItemPresetBook extends UListItem implements UButtonCallbacks {
    /**
     * Enums
     */
    /**
     * Constants
     */
    public static final String TAG = "ListItemOption";

    public static final int ButtonIdAdd = 100100;
    private static final int ITEM_H = 67;
    private static final int MARGIN_H = 17;
    private static final int ICON_W = 34;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BG_COLOR = Color.WHITE;

    private static final int STAR_ICON_W = 34;

    private static final int FRAME_WIDTH = 2;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private PresetBook mBook;
    private UButtonImage mAddButton;

    // Dpi計算済み
    private int iconW, marginH;

    /**
     * Get/Set
     */
    public PresetBook getBook() {
        return mBook;
    }

    /**
     * Constructor
     */
    public ListItemPresetBook(UListItemCallbacks listItemCallbacks,
                          PresetBook book, int width)
    {
        super(listItemCallbacks, true, 0, width, UDpi.toPixel(ITEM_H), BG_COLOR, UDpi.toPixel(FRAME_WIDTH), FRAME_COLOR);
        mBook = book;

        iconW = UDpi.toPixel(ICON_W);
        marginH = UDpi.toPixel(MARGIN_H);

        int starIconW = UDpi.toPixel(STAR_ICON_W);
        // Add Button
        Bitmap image = UResourceManager.getBitmapWithColor(R.drawable.add, UColor.Green);
        mAddButton = UButtonImage.createButton(this, ButtonIdAdd, 0,
                size.width - UDpi.toPixel(50), (size.height - starIconW) / 2,
                starIconW, starIconW, image, null);
        mAddButton.scaleRect(2.0f, 1.5f);


    }

    /**
     * Methods
     */
    /**
     * 描画処理
     *
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

        super.draw(canvas, paint, _pos);

        int fontSize = UDraw.getFontSize(FontSize.M);
        float x = _pos.x + marginH;
        float marginV = (UDpi.toPixel(ITEM_H) - fontSize * 2) / 3;
        float y = _pos.y + marginV;
        // Icon image
        UDraw.drawBitmap(canvas, paint, UResourceManager.getBitmapWithColor(R.drawable.cards, mBook.mColor), x,
                _pos.y + (size.height - iconW) / 2,
                iconW, iconW );
        x += iconW + marginH;

        // Name
        UDraw.drawTextOneLine(canvas, paint, mBook.mName + " " + mBook.getFileName(), UAlignment
                .None, fontSize,
                x, y, TEXT_COLOR);
        y += UDraw.getFontSize(FontSize.M) + marginV;

        // Comment
        UDraw.drawTextOneLine(canvas, paint, mBook.mComment, UAlignment.None, fontSize,
                x, y, TEXT_COLOR);

        // Add Button
        if (mAddButton != null) {
            mAddButton.draw(canvas, paint, _pos);
        }
    }

    /**
     * 毎フレーム呼ばれる処理
     * @return
     */
    public DoActionRet doAction() {
        if (mAddButton != null ) {
            return mAddButton.doAction();
        }
        return DoActionRet.None;
    }

    /**
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (mAddButton != null) {
            PointF offset2 = new PointF(pos.x + offset.x, pos.y + offset.y);
            if (mAddButton.touchEvent(vt, offset2)) {
                return true;
            }
        }
        if (super.touchEvent(vt, offset)) {
            return true;
        }
        return false;
    }

    /**
     * 高さを返す
     */
    public int getHeight() {
        return size.height;
    }


    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        if (mListItemCallbacks != null) {
            mListItemCallbacks.ListItemButtonClicked(this, id);
            return true;
        }
        return false;
    }
}