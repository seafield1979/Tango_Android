package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by shutaro on 2017/01/16.
 * Sub Windowのクラス  個別処理が増えたためクラス化した
 */

// コールバック
interface UIconWindowSubCallbacks {
    void IconWindowSubAction(UIconWindowSub.ActionId actionId, UIcon icon);
}

public class UIconWindowSub extends UIconWindow {
    // button id
    private static final int buttonIdClose = 299;
    private static final int buttonIdEdit = 300;
    private static final int buttonIdCopy = 301;
    private static final int buttonIdDelete = 302;
    private static final int buttonIdCleanup = 303;
    private static final int buttonIdExport = 304;

    // color
    private static final int ICON_BG_COLOR = UColor.LightYellow;

    /**
     * Enum
     */
    enum ActionId {
        Close(R.drawable.close, R.string.close, buttonIdClose),
        Edit(R.drawable.edit, R.string.edit, buttonIdEdit),
        Copy(R.drawable.copy, R.string.copy, buttonIdCopy),
        Delete(R.drawable.trash, R.string.trash, buttonIdDelete),
        Export(R.drawable.export, R.string.export, buttonIdExport),
        Cleanup(R.drawable.trash2, R.string.clean_up, buttonIdCleanup)
        ;

        private int imageId;
        private int buttonId;
        private String title;

        private ActionId(int imageId, int stringId, int buttonId) {
            this.imageId = imageId;
            this.buttonId = buttonId;
            this.title = UResourceManager.getStringById(stringId);
        }
        public int getImageId() {
            return imageId;
        }
        public int getButtonId() {
            return buttonId;
        }
        public String getTitle() {
            return title;
        }

        public static ActionId[] bookIds() {
            return new ActionId[]{Close, Edit, Copy, Export,
                    Delete};
        }
        public static ActionId[] trashIds() {
            return new ActionId[]{Close, Cleanup};
        }
    }

    /**
     * Consts
     */
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 20;
    private static final int MARGIN_V2 = 50;
    private static final int ICON_TEXT_SIZE = 30;
    private static final int ACTION_ICON_W = 100;

    /**
     * Member variables
     */
    // 親のアイコン
    private UIcon mParentIcon;


    // SubWindowの上に表示するアイコンボタン
    private UButtonImage[] mBookButtons = new UButtonImage[ActionId.bookIds().length];
    private UButtonImage[] mTrashButtons = new UButtonImage[ActionId.trashIds().length];

    // コールバック用のインターフェース
    private UIconWindowSubCallbacks mIconWindowSubCallback;

    /**
     * Get/Set
     */
    public void setParentIcon(UIcon icon) {
        mParentIcon = icon;
    }
    public UIcon getParentIcon() {
        return mParentIcon;
    }

    private UButtonImage[] getButtons() {
        return (getParentType() == TangoParentType.Book) ? mBookButtons : mTrashButtons;

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
        Bitmap image;
        float x = MARGIN_H;
        float y = -ACTION_ICON_W - MARGIN_V2;

        // Bookを開いたときのアイコンを初期化
        int i = 0;
        for (ActionId id : ActionId.bookIds()) {
            image = UResourceManager.getBitmapWithColor(id.getImageId(), UColor.DarkGreen);
            mBookButtons[i] = UButtonImage.createButton(this, id.getButtonId(), 0, x, y,
                    ACTION_ICON_W, ACTION_ICON_W, image, null);
            mBookButtons[i].setTitle(id.getTitle(), ICON_TEXT_SIZE, Color.BLACK);

            x += ACTION_ICON_W + MARGIN_H;
            i++;
        }

        // ゴミ箱を開いたときのアイコンを初期化
        x = MARGIN_H;
        i = 0;
        for (ActionId id : ActionId.trashIds()) {
            image = UResourceManager.getBitmapWithColor(id.getImageId(), UColor.DarkGreen);
            mTrashButtons[i] = UButtonImage.createButton(this, id.getButtonId(), 0, x, y,
                    ACTION_ICON_W, ACTION_ICON_W, image, null);
            mTrashButtons[i].setTitle(id.getTitle(), ICON_TEXT_SIZE, Color.BLACK);

            x += ACTION_ICON_W + MARGIN_H;
            i++;
        }


    }

    /**
     * 毎フレーム行う処理
     * @return true:再描画を行う(まだ処理が終わっていない)
     */
    public boolean doAction() {
        if (super.doAction()) {
            return true;
        }
        for (UButtonImage button : getButtons()) {
            if (button.doAction()) {
                return true;
            }
        }
        return false;
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
        for (UButtonImage button : getButtons()) {
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

        // アイコンの背景
        UButtonImage[] buttons = getButtons();

        float width = buttons.length * (ACTION_ICON_W + MARGIN_H) + MARGIN_H;
        final float height = ACTION_ICON_W + MARGIN_V + MARGIN_V2;
        float x = pos.x;
        float y = pos.y - MARGIN_V2 - MARGIN_V - ACTION_ICON_W;
        UDraw.drawRoundRectFill(canvas, paint, new RectF(x, y, x + width, y +
                height),
                30, ICON_BG_COLOR, 0, 0);

        // アイコンの描画
        for (UButtonImage button : buttons) {
            button.draw(canvas, paint, pos);
        }
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {
            case buttonIdClose:
                if (mIconWindowSubCallback != null) {
                    mIconWindowSubCallback.IconWindowSubAction(ActionId.Close, null);
                }
                break;
            case buttonIdEdit:
                if (mIconWindowSubCallback != null && mParentIcon != null ) {
                    mIconWindowSubCallback.IconWindowSubAction(ActionId.Edit, mParentIcon);
                }
                break;
            case buttonIdCopy:
                if (mIconWindowSubCallback != null && mParentIcon != null ) {
                    mIconWindowSubCallback.IconWindowSubAction(ActionId.Copy, mParentIcon);
                }
                break;
            case buttonIdExport:
                if (mIconWindowSubCallback != null && mParentIcon != null ) {
                    mIconWindowSubCallback.IconWindowSubAction(ActionId.Export, mParentIcon);
                }
                break;
            case buttonIdDelete:
                if (mIconWindowSubCallback != null && mParentIcon != null ) {
                    mIconWindowSubCallback.IconWindowSubAction(ActionId.Delete, mParentIcon);
                }
                break;
            case buttonIdCleanup:
                if (mIconWindowSubCallback != null) {
                    mIconWindowSubCallback.IconWindowSubAction(ActionId.Cleanup, null);
                }
                break;
        }
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }
        return false;
    }

}
