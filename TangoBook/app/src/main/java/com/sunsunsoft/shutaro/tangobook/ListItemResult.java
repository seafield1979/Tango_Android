package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2016/12/11.
 */

public class ListItemResult extends UListItem implements UButtonCallbacks {
    /**
     * Enums
     */
    enum ListItemResultType {
        Title,
        OK,
        NG
    }

    /**
     * Constants
     */
    public static final String TAG = "ListItemResult";

    private static final int ButtonIdStar = 100100;
    private static final int TITLE_H = 80;
    private static final int CARD_H = 120;

    private static final int TEXT_SIZE = 50;
    private static final int TEXT_COLOR = Color.WHITE;

    private static final int STAR_ICON_W = 100;

    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private ListItemResultType mType;
    private String mText;
    private TangoCard mCard;
    private UButtonImage mStarButton;

    /**
     * Get/Set
     */
    public ListItemResultType getType() {
        return mType;
    }

    public TangoCard getCard() {
        return mCard;
    }

    /**
     * Constructor
     */
    public ListItemResult(UListItemCallbacks listItemCallbacks,
                          ListItemResultType type, boolean isTouchable,
                          float x, int width, int color) {
        super(listItemCallbacks, isTouchable, x, width, 0, color);
        mType = type;
    }

    // ListItemResultType.Title のインスタンスを生成する
    public static ListItemResult createTitle(String text, int width, int color)
    {
        ListItemResult instance = new ListItemResult(null, ListItemResultType.Title,
                false, 0, width, color);
        instance.mText = text;
        instance.size.height = TITLE_H;
        return instance;
    }

    // ListItemResultType.OKのインスタンスを生成する
    public static ListItemResult createOK(TangoCard card, int width, int color) {
        ListItemResult instance = new ListItemResult(null,
                ListItemResultType.OK, true,
                0, width, color);
        instance.mText = card.getWordA() + ":" + card.getWordB();
        instance.mCard = card;
        instance.size.height = CARD_H;
        // Starボタンを追加(On/Offあり)
        instance.mStarButton = new UButtonImage(instance, ButtonIdStar, 100,
                instance.size.width - 150, (instance.size.height - STAR_ICON_W) / 2,
                STAR_ICON_W, STAR_ICON_W, R.drawable.favorites, -1);
        instance.mStarButton.addState(R.drawable.favorites2);
        instance.mStarButton.setState(card.getStar() ? 1 : 0);

        return instance;
    }

    // ListItemResultType.NGのインスタンスを生成する
    public static ListItemResult createNG(TangoCard card, int width, int color) {
        ListItemResult instance = new ListItemResult(null,
                ListItemResultType.NG, true,
                0, width, color);
        instance.mText = card.getWordA() + ":" + card.getWordB();
        instance.size.height = CARD_H;
        return instance;
    }

    /**
     * Methods
     */
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

        // BG　タッチ中は色を変更
        int _color = color;
        if (isTouchable && isTouching) {
            _color = pressedColor;
        }
        ULog.print(TAG, "isTouching:" + isTouching);
        UDraw.drawRectFill(canvas, paint,
                    new Rect((int) _pos.x, (int) _pos.y, (int) _pos.x + size.width, (int) _pos.y + size.height),
                    _color, FRAME_WIDTH, FRAME_COLOR);

        switch(mType) {
            case Title:
            {
                UDraw.drawTextOneLine(canvas, paint, mText, UAlignment.Center, TEXT_SIZE,
                        _pos.x + size.width / 2, _pos.y + size.height / 2, TEXT_COLOR);
            }
                break;
            case OK:
            {
                UDraw.drawTextOneLine(canvas, paint, mText, UAlignment.Center, TEXT_SIZE,
                        _pos.x + size.width / 2, _pos.y + size.height / 2, TEXT_COLOR);
            }
                break;
            case NG:
                UDraw.drawTextOneLine(canvas, paint, mText, UAlignment.Center, TEXT_SIZE,
                        _pos.x + size.width / 2, _pos.y + size.height / 2, TEXT_COLOR);
                break;
        }

        if (mStarButton != null) {
            mStarButton.draw(canvas, paint, _pos);
        }
    }

    /**
     *
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        // Starボタンのクリック処理
        if (mStarButton != null) {
            PointF offset2 = new PointF(pos.x + offset.x, pos.y + offset.y);
            if (mStarButton.touchEvent(vt, offset2)) {
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
        if (id == ButtonIdStar) {
            boolean star = RealmManager.getCardDao().toggleStar(mCard);

            // 表示アイコンを更新
            mStarButton.setState(star ? 1 : 0);
            return true;
        }
        return false;
    }
}
