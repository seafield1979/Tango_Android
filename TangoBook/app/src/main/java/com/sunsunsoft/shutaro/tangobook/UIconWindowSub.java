package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2017/01/16.
 * Sub Windowのクラス  個別処理が増えたためクラス化した
 */

// コールバック
interface UIconWindowSubCallbacks {
    void IconWindowSubEdit(UIcon icon);
    void IconWindowSubCopy(UIcon icon);
    void IconWindowSubDelete(UIcon icon);
}

public class UIconWindowSub extends UIconWindow {
    /**
     * Enum
     */
    enum ButtonId {
        Edit,
        Copy,
        Delete
    }

    /**
     * Consts
     */
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 20;

    // button id
    private static final int buttonIdEdit = 300;
    private static final int buttonIdCopy = 301;
    private static final int buttonIdDelete = 302;

    /**
     * Member variables
     */
    // 親のアイコン
    private IconBook mParentIcon;


    // SubWindowの上に表示するアイコンボタン
    private UButtonImage[] mButtons = new UButtonImage[ButtonId.values().length];

    // コールバック用のインターフェース
    private UIconWindowSubCallbacks mIconWindowSubCallback;

    /**
     * Get/Set
     */
    public void setParentIcon(IconBook icon) {
        mParentIcon = icon;
    }
    public UIcon getParentIcon() {
        return mParentIcon;
    }


    /**
     * Constructor
     */
    public UIconWindowSub(UWindowCallbacks windowCallbacks,
                          UIconCallbacks iconCallbacks,
                          UIconWindowSubCallbacks iconWindowSubCallbacks,
                          boolean isHome, WindowDir dir,
                          int width, int height, int bgColor)
    {
        super(windowCallbacks, iconCallbacks, isHome, dir, width, height, bgColor);

        mIconWindowSubCallback = iconWindowSubCallbacks;
        // 閉じるボタンは表示しない
        closeIcon = null;
    }

    /**
     * Create class instance
     * It doesn't allow to create multi Home windows.
     * @return
     */
    public static UIconWindowSub createInstance( UWindowCallbacks windowCallbacks,
                                                    UIconCallbacks iconCallbacks,
                                              UIconWindowSubCallbacks iconWindowSubCallbacks,
                                              boolean isHome, WindowDir dir,
                                              int width, int height, int bgColor)
    {
        UIconWindowSub instance = new UIconWindowSub( windowCallbacks, iconCallbacks,
                iconWindowSubCallbacks, isHome, dir, width, height, bgColor);

        return instance;
    }

    /**
     * Methods
     */
    public void init() {
        super.init();

        // 閉じるボタンの位置を変更

        // アイコンボタンの初期化
        float x = MARGIN_H;
        mButtons[ButtonId.Edit.ordinal()] = UButtonImage.createButton(this, buttonIdEdit, 0, x,
                -ACTION_ICON_W - MARGIN_V,
                ACTION_ICON_W, ACTION_ICON_W, R.drawable.edit, -1);
        x += ACTION_ICON_W + MARGIN_H;

        mButtons[ButtonId.Copy.ordinal()] = UButtonImage.createButton(this, buttonIdCopy, 0, x,
                -ACTION_ICON_W - MARGIN_V,
                ACTION_ICON_W, ACTION_ICON_W, R.drawable.copy, -1);
        x += ACTION_ICON_W + MARGIN_H;

        mButtons[ButtonId.Delete.ordinal()] = UButtonImage.createButton(this, buttonIdDelete, 0, x,
                -ACTION_ICON_W - MARGIN_V,
                ACTION_ICON_W, ACTION_ICON_W, R.drawable.trash, -1);

    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (!isShow) return false;
        if (state == WindowState.icon_moving) return false;

        if (offset == null) {
            offset = new PointF(pos.x, pos.y);
        }
        if (super.touchEvent(vt, offset)) {
            return true;
        }

        // アイコンのタッチ処理
        for (UButtonImage button : mButtons) {
            if (button.touchEvent(vt, offset)) {
                return true;
            }
        }
        return super.touchEvent(vt, offset);
    }

    /**
     * 描画処理
     * UIconManagerに登録されたIconを描画する
     * @param canvas
     * @param paint
     * @return trueなら描画継続
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset)
    {
        super.drawContent(canvas, paint, offset);

        // アイコンの描画
        for (UButtonImage button : mButtons) {
            button.draw(canvas, paint, pos);
        }
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {
            case buttonIdEdit:
                if (mIconWindowSubCallback != null && mParentIcon != null ) {
                    mIconWindowSubCallback.IconWindowSubEdit(mParentIcon);
                }
                break;
            case buttonIdCopy:
                if (mIconWindowSubCallback != null && mParentIcon != null ) {
                    mIconWindowSubCallback.IconWindowSubCopy(mParentIcon);
                }
                break;
            case buttonIdDelete:
                if (mIconWindowSubCallback != null && mParentIcon != null ) {
                    mIconWindowSubCallback.IconWindowSubDelete(mParentIcon);
                }
                break;
        }
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }
        return false;
    }

}
