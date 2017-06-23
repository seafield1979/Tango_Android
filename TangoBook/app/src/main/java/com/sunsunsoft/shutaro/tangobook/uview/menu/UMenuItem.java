package com.sunsunsoft.shutaro.tangobook.uview.menu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.TouchType;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;

import java.util.LinkedList;


/**
 * メニューに表示する項目
 * アイコンを表示してタップされたらIDを返すぐらいの機能しか持たない
 */
public class UMenuItem extends UDrawable {
    public static final String TAG = "UMenuItem";

    public static final int DRAW_PRIORITY = 200;
    public static final int ANIME_FRAME = 10;

    public static final int TOP_ITEM_W = 50;
    public static final int ITEM_W = 40;
    private static final int CHILD_MARGIN_V = 10;
    private static final int CHILD_MARGIN_H = 10;
    private static final int TEXT_SIZE = 13;

    /**
     * メンバ変数
     */
    protected UMenuBar mMenuBar;
    protected UMenuItemCallbacks mCallbacks;
    protected UTextView mTextTitle;
    protected int mItemId;
    protected int mNestCount;
    protected int mStateId;          // 現在の状態
    protected int mStateMax;         // 状態の最大値 addState で増える
    protected boolean mShowTitle;

    // 親アイテム
    protected UMenuItem mParentItem;
    // 子アイテムリスト
    protected LinkedList<UMenuItem> mChildItem;

    // 開いた状態、子アイテムを表示中かどうか
    protected boolean isOpened;

    // アイコン用画像
    protected LinkedList<Bitmap> icons = new LinkedList<>();
    protected int mAnimeColor;

    // 閉じている移動中かどうか
    protected boolean isClosing;

    // Dpi補正計算済みの値
    private int mItemW;

    /**
     * Get/Set
     */
    public LinkedList<UMenuItem> getmChildItem() {
        return mChildItem;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public int getmNestCount() {
        return mNestCount;
    }

    public void setmNestCount(int mNestCount) {
        this.mNestCount = mNestCount;
    }

    public void setCallbacks(UMenuItemCallbacks callbacks){
        mCallbacks = callbacks;
    }

    public void setTitle(String title) {
        if (mTextTitle != null) {
            mTextTitle.setText(title);
        }
    }

    public UMenuItem(UMenuBar menuBar, int id, boolean isTop, Bitmap icon) {
        super(DRAW_PRIORITY, 0,0, UDpi.toPixel(isTop ? TOP_ITEM_W : ITEM_W),
                UDpi.toPixel(isTop ? TOP_ITEM_W : ITEM_W));
        this.mMenuBar = menuBar;
        this.mItemId = id;
        this.mStateId = 0;
        this.mStateMax = 1;
        if (icon != null) {
            this.icons.add(icon);
        }
        this.mItemW = UDpi.toPixel(ITEM_W);
    }

    public void setmParentItem(UMenuItem mParentItem) {
        this.mParentItem = mParentItem;
    }

    /**
     * テキストを追加する
     */
    public void addTitle(String title, UAlignment alignment, float x, float y, int
            color, int bgColor) {
        mTextTitle = UTextView.createInstance(title, UDpi.toPixel(TEXT_SIZE), 0, alignment,
                0, false, true, x, y, 0, color, bgColor);
        mShowTitle = true;
        mTextTitle.setMargin(UDpi.toPixel(7), UDpi.toPixel(7));
    }

    /**
     * 子要素を追加する
     * @param child
     */
    public void addItem(UMenuItem child) {
        if (mChildItem == null) {
            mChildItem = new LinkedList<>();
        }
        // 親を設定する
        mParentItem = this;
        child.setmNestCount(this.mNestCount + 1);

        mChildItem.add(child);
    }

    /**
     * 状態を追加する
     * @param icon 追加した状態の場合に表示するアイコン
     */
    public void addState(Bitmap icon) {
        icons.add(icon);
        mStateMax++;
    }

    /**
     * 次の状態にすすむ
     */
    public int setNextState() {
        if (mStateMax >= 2) {
            mStateId = (mStateId + 1) % mStateMax;
        }
        return mStateId;
    }

    private int getNextStateId() {
        if (mStateMax >= 2) {
            return (mStateId + 1) % mStateMax;
        }
        return 0;
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param parentPos
     */
    public void draw(Canvas canvas, Paint paint, PointF parentPos) {
        if (!isShow) return;

        // スタイル(内部を塗りつぶし)
        paint.setStyle(Paint.Style.FILL);
        // 色
        paint.setColor(0);

        PointF drawPos = new PointF();
        drawPos.x = pos.x + parentPos.x;
        drawPos.y = pos.y + parentPos.y;

        if (icons.size() > 0) {
            // 次の状態のアイコンを表示する
            Bitmap icon = icons.get(getNextStateId());

            // アニメーション処理
            // フラッシュする
            if (isAnimating) {
                int alpha = getAnimeAlpha();
                paint.setColor((alpha << 24) | mAnimeColor);
            } else if (isMoving) {
                float ratio = (float)movingFrame / (float)movingFrameMax;
                if (isClosing) {
                    ratio = 1.0f - ratio;
                }
                int _color = (int)(255 * ratio) << 24;
                paint.setColor(_color);
            } else {
                paint.setColor(0xff000000);
            }

            // 領域の幅に合わせて伸縮
            canvas.drawBitmap(icon, new Rect(0,0,icon.getWidth(), icon.getHeight()),
                    new Rect((int)drawPos.x, (int)drawPos.y,
                            (int)drawPos.x + size.width,(int)drawPos.y + size.width),
                    paint);
            // タイトル
            if (mTextTitle != null ) {
                if (!isMoving && !isClosing) {
                    mTextTitle.draw(canvas, paint, drawPos);
                }
            }
        }

        // 子要素
        if (mChildItem != null) {
            for (UMenuItem item : mChildItem) {
                if (!item.isShow) continue;

                item.draw(canvas, paint, drawPos);
            }
        }
    }

    /**
     * アニメーション開始
     */
    public void startAnim() {
        mMenuBar.setAnimating(true);
        isAnimating = true;
        animeFrame = 0;
        animeFrameMax = ANIME_FRAME;
        mAnimeColor = Color.argb(0,255,255,255);
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
     * タッチイベント
     * MenuBarクラスで処理するのでここでは何もしない
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        return false;
    }

    /**
     * クリック処理
     * @param touchX
     * @param touchY
     * @return
     */
    public boolean checkTouch(ViewTouch vt, float touchX, float touchY) {
        if (vt.checkInsideCircle(touchX, touchY, pos.x + mItemW / 2, pos.y + mItemW / 2, mItemW / 2))
        {
            if (vt.type != TouchType.Touch) return false;

            // 子要素を持っていたら Open/Close
            if (mChildItem != null) {
                if (isOpened) {
                    isOpened = false;
                    closeMenu();
                } else {
                    isOpened = true;
                    openMenu();
                }
                ULog.print(TAG, "isOpened " + isOpened);
            } else {
                // タッチされた時の処理
                setNextState();

                if (mCallbacks != null) {
                    mCallbacks.menuItemClicked(mItemId, mStateId);
                }
            }
            // アニメーション
            startAnim();

            return true;
        }

        // 子要素
        if (isOpened() && mChildItem != null) {
            for (UMenuItem child : mChildItem) {
                // この座標系(親原点)に変換
                if (child.checkTouch(vt, touchX - pos.x, touchY - pos.y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * メニューをOpenしたときの処理
     */
    public void openMenu() {
        if (mChildItem == null) return;

        isOpened = true;

        int count = 1;
        for (UMenuItem item : mChildItem) {
            item.setPos(0, 0);
            // 親の階層により開く方向が変わる
            item.isClosing = false;
            item.setShow(true);

            if (mNestCount == 0) {
                // 縦方向
                item.startMoving(0, -count * (mItemW + UDpi.toPixel(CHILD_MARGIN_V)), ANIME_FRAME);
            } else if (mNestCount == 1) {
                // 横方向
                item.startMoving(count * (mItemW + UDpi.toPixel(CHILD_MARGIN_H)), 0, ANIME_FRAME);
            }
            count++;
        }
    }

    /**
     * メニューをCloseしたときの処理
     */
    public void closeMenu() {
        if (mChildItem == null) return;

        isOpened = false;

        for (UMenuItem item : mChildItem) {
            item.startMoving(0, 0, ANIME_FRAME);
            item.isClosing = true;
            if (item.isOpened) {
                item.closeMenu();
            }
        }
    }

    /**
     * 毎フレームの処理
     * @return true:処理中(再描画あり)
     */
    public DoActionRet doAction() {
        DoActionRet ret = DoActionRet.None;

        // 自分の処理
        // 移動
        if (autoMoving()) {
            ret = DoActionRet.Redraw;
        } else if (isClosing) {
            setShow(false);
        }

        // アニメーション
        if (animate()) {
            ret = DoActionRet.Redraw;
        }

        // 子要素のdoAction
        if (mChildItem != null) {
            for (UMenuItem item : mChildItem) {
                DoActionRet _ret = item.doAction();
                switch (_ret) {
                    case Done:
                        return _ret;
                    case Redraw:
                        ret = _ret;
                        break;
                }
            }
        }
        return ret;
    }

    /**
     * Drawableインターフェース
     */
    public PointF getDrawOffset() {
        return null;
    }
}
