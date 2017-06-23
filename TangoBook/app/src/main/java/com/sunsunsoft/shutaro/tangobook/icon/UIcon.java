package com.sunsunsoft.shutaro.tangobook.icon;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.database.TangoItem;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.DrawList;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.DrawPriority;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;

import static com.sunsunsoft.shutaro.tangobook.util.UDebug.drawIconId;

/**
 * ViewのonDrawで描画するアイコンの情報
 */
abstract public class UIcon extends UDrawable {

    /**
     * Constants
     */
    private static final String TAG = "UIcon";
    private static final int DRAW_PRIORITY = 200;

    protected static final int TEXT_SIZE = 13;
    protected static final int TEXT_MARGIN = 4;

    // タッチ領域の拡張幅
    protected static final int TOUCH_MARGIN = 10;

    public static final int DISP_TITLE_LEN = 8;

    private static final int CHECKED_WIDTH = 24;    // 選択中のアイコンのチェックの幅
    private static final int CHECKED_FRAME = 3;    // 選択中のアイコンのチェックの枠

    protected static final int NEW_TEXT_SIZE = 10;
    protected static final int NEW_TEXT_MARGIN = 5;
    protected static final int NEW_TEXT_COLOR = Color.argb(200, 255, 80, 80);

    /**
     * Class variables
     */
    // "New" バッジ用
    protected static UTextView newTextView;


    /**
     * Member variables
     */
    private static int count;

    public int id;
    protected UIconWindow parentWindow;
    private UIconCallbacks callbacks;
    protected DrawList drawList;
    protected Bitmap image;

    // アニメーション用
    public static final int ANIME_FRAME = 20;

    // 各種状態
    protected boolean isChecking;      // 選択可能状態(チェックボックスが表示される)
    protected boolean isChecked;       // 選択中
    protected boolean isDraging;        // ドラッグ中
    protected boolean isDroped;        // ドロップ中(上に他のアイコンがドラッグ)
    protected boolean isTouched;        // タッチ中
    protected boolean isLongTouched;    // 長押し中

    protected int touchedColor;
    protected int longPressedColor;

    protected String title;
    protected IconType type;

    /**
     * Get/Set
     */
    private void clearFlags() {
        isTouched = false;
        isLongTouched = false;
        isDraging = false;
    }

    // 保持するTangoItemを返す
    abstract public TangoItem getTangoItem();

    public String getTitle() {
        return title;
    }

    abstract public void updateTitle();

    public Rect getRect() {
        return new Rect(rect.left - UDpi.toPixel(TOUCH_MARGIN),
                rect.top - UDpi.toPixel(TOUCH_MARGIN),
                rect.right + UDpi.toPixel(TOUCH_MARGIN),
                rect.bottom + UDpi.toPixel(TOUCH_MARGIN));
    }

    /**
     * Constructor
     */
    public UIcon(UIconWindow parentWindow, UIconCallbacks iconCallbacks, IconType
            type, float x,
                 float y, int width, int height)
    {
        super(DRAW_PRIORITY, x, y, width, height);
        this.parentWindow = parentWindow;
        this.callbacks = iconCallbacks;
        this.id = count;
        this.type = type;
        this.setPos(x, y);
        this.setSize(width, height);
        updateRect();
        count++;
    }

    public void setColor(int color) {
        this.color = color;
        this.touchedColor = UColor.addBrightness(color, 0.3f);
        this.longPressedColor = UColor.addBrightness(color, 0.6f);
    }

    public IconType getType() { return type; }


    public UIconWindow getParentWindow() {
        return parentWindow;
    }
    public void setParentWindow(UIconWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void click() {
        startAnim();
        if (isChecking) {
            if(isChecked) {
                isChecked = false;
            }
            else {
                isChecked = true;
                this.drawPriority = DrawPriority.DragIcon.p();
            }
        } else {
            if (callbacks != null) {
                callbacks.iconClicked(this);
            }
        }
    }
    public void longClick() {
        if (callbacks != null) {
            callbacks.longClickIcon(this);
        }
    }
    public void moving() {
        Log.v(TAG, "moving");
    }
    public void drop() {
        Log.v(TAG, "drop");
        if (callbacks != null) {
            callbacks.iconDroped(this);
        }
    }

    /**
     * Newバッジ作成
     */
    protected void createNewBadge(Canvas canvas) {
        newTextView = UTextView.createInstance("New", UDpi.toPixel(NEW_TEXT_SIZE), 0, UAlignment.Center,
                canvas.getWidth(), false, true, 0, 0, 100, Color.WHITE, NEW_TEXT_COLOR);
        // 文字の周りのマージン
        newTextView.setMargin(UDpi.toPixel(NEW_TEXT_MARGIN), UDpi.toPixel(NEW_TEXT_MARGIN));
    }

    /**
     * Newフラグ設定
     */
    abstract public void setNewFlag(boolean newFlag);

    /**
     * アイコンのタッチ処理
     * @param tx
     * @param ty
     * @return
     */
    public boolean checkTouch(float tx, float ty) {
        if (pos.x <= tx && tx <= getRight() &&
                pos.y <= ty && ty <= getBottom() )
        {
            return true;
        }
        return false;
    }

    /**
     * クリックのチェックとクリック処理。このメソッドはすでにクリック判定された後の座標が渡される
     * @param clickX
     * @param clickY
     * @return
     */
    public boolean checkClick(float clickX, float clickY) {
        if (getRect().contains((int)clickX, (int)clickY))
        {
            click();
            return true;
        }
        return false;
    }

    /**
     * ドロップをチェックする
     */
    public boolean checkDrop(float dropX, float dropY) {
        if (getRect().contains((int)dropX, (int)dropY))
        {
            return true;
        }
        return false;
    }

    /**
     * アイコンを描画
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        drawIcon(canvas, paint, offset);

        if (isChecking) {
            float _x = pos.x + offset.x;
            float _y = pos.y + offset.y;
            int width = UDpi.toPixel(CHECKED_WIDTH);
            UDraw.drawCheckbox(canvas, paint, isChecked, _x + UDpi.toPixel(CHECKED_FRAME), _y + size.height - width - UDpi.toPixel(CHECKED_FRAME), width,
                    Color.rgb
                            (100,100,200));
        }
    }

    /**
     * アイコンを描画する
     */
    abstract protected void drawIcon(Canvas canvas, Paint paint, PointF offset);

    /*
        Drawableインターフェース
     */
    public void setDrawList(DrawList drawList) {
        this.drawList = drawList;
    }

    public DrawList getDrawList() {
        return drawList;
    }


    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        // 親Windowの座標とスクロール量を取得
        if (parentWindow != null) {
            return new PointF(parentWindow.getPos().x - parentWindow.getContentTop().x,
                    parentWindow.getPos().y - parentWindow.getContentTop().y);
        }
        return null;
    }

    /**
     * アニメーション開始
     */
    public void startAnim() {
        isAnimating = true;
        animeFrame = 0;
        animeFrameMax = ANIME_FRAME;
        if (parentWindow != null) {
            parentWindow.setAnimating(true);
        }
    }

    /**
     * アニメーション処理
     * といいつつフレームのカウンタを増やしているだけ
     * @return true:アニメーション中
     */
    public boolean animate() {
        if (!isAnimating) return false;
        if (animeFrame >= animeFrameMax) {
            isAnimating = false;
            return false;
        }

        animeFrame++;
        return true;
    }

    /**
     * アニメーション中かどうか
     * @return
     */
    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * タッチイベント処理
     * 親のUIconWindowで処理するのでここでは何もしない
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return touchEvent(vt, null);
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        boolean done = false;

        if (offset == null) {
            offset = new PointF();
        }

        if (vt.isTouchUp()) {
            clearFlags();
        }
        switch (vt.type) {
            case Touch:
                if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                    isTouched = true;
                    done = true;
                }
                break;
            case LongPress:
                if (getRect().contains((int)vt.getX(offset.x), (int)vt.getY(offset.y))) {
                    isLongTouched = true;
                    isChecking = true;
                    isChecked = true;
                    done = true;
                }
                break;
            case Click:
                if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                    click();
                    done = true;
                }
                break;
            case LongClick:
                break;
            case Moving:
                if (vt.isMoveStart()) {
                    if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                        isDraging = true;
                        done = true;
                    }
                }
                if (isDraging) {
                    done = true;
                }
                break;
            case MoveEnd:
                isDraging = false;
                break;
            case MoveCancel:
                isDraging = false;
                break;
        }

        return done;
    }


    /**
     * 画像を更新する
     * アイコンの色が変更された際に呼び出す
     */
    public void updateIconImage() {
        image = UUtil.convBitmapColor(image, color);
    }

    /**
     * Drag & Drop
     */

    /**
     * ドロップ可能かどうか
     * ドラッグ中のアイコンを他のアイコンの上に重ねたときにドロップ可能かを判定してアイコンの色を変えたりする
     * @param dstIcon
     * @return
     */
    abstract public boolean canDrop(UIcon dstIcon, float x, float y);

    /**
     * ドロップして中に入れることができるかどうか？
     * 例: Card -> Book は OK
     *    Book -> Card/Book は NG
     * @return
     */
    abstract public boolean canDropIn(UIcon dstIcon, float x, float y);
}

