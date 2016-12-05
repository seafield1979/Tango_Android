package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.LinkedList;


interface UMenuItemCallbacks {
    void menuItemClicked(int itemId, int stateId);
}

/**
 * メニューに表示する項目
 * アイコンを表示してタップされたらIDを返すぐらいの機能しか持たない
 */
public class UMenuItem extends UDrawable {
    public static final String TAG = "UMenuItem";

    public static final int DRAW_PRIORITY = 200;
    public static final int ITEM_W = 120;
    public static final int ITEM_H = 120;
    public static final int ANIME_FRAME = 10;

    private static final int CHILD_MARGIN_V = 30;
    private static final int CHILD_MARGIN_H = 30;


    /**
     * メンバ変数
     */
    protected UMenuBar menuBar;
    protected UMenuItemCallbacks mCallbacks;
    protected int itemId;
    protected int nestCount;
    protected int stateId;          // 現在の状態
    protected int stateMax;         // 状態の最大値 addState で増える

    // 親アイテム
    protected UMenuItem parentItem;
    // 子アイテムリスト
    protected LinkedList<UMenuItem> childItems;

    // 開いた状態、子アイテムを表示中かどうか
    protected boolean isOpened;

    // アイコン用画像
    protected LinkedList<Bitmap> icons = new LinkedList<>();
    protected int animeColor;

    // 閉じている移動中かどうか
    protected boolean isClosing;

    /**
     * Get/Set
     */
    public LinkedList<UMenuItem> getChildItems() {
        return childItems;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public int getNestCount() {
        return nestCount;
    }

    public void setNestCount(int nestCount) {
        this.nestCount = nestCount;
    }

    public void setCallbacks(UMenuItemCallbacks callbacks){
        mCallbacks = callbacks;
    }

    public UMenuItem(UMenuBar menuBar, int id, Bitmap icon) {
        super(DRAW_PRIORITY, 0,0,0,0);
        this.menuBar = menuBar;
        this.itemId = id;
        this.stateId = 0;
        this.stateMax = 1;
        if (icon != null) {
            this.icons.add(icon);
        }
    }

    public void setParentItem(UMenuItem parentItem) {
        this.parentItem = parentItem;
    }

    /**
     * 子要素を追加する
     * @param child
     */
    public void addItem(UMenuItem child) {
        if (childItems == null) {
            childItems = new LinkedList<>();
        }
        // 親を設定する
        parentItem = this;
        child.setNestCount(this.nestCount + 1);

        childItems.add(child);
    }

    /**
     * 状態を追加する
     * @param icon 追加した状態の場合に表示するアイコン
     */
    public void addState(Bitmap icon) {
        icons.add(icon);
        stateMax++;
    }

    /**
     * 次の状態にすすむ
     */
    public int setNextState() {
        if (stateMax >= 2) {
            stateId = (stateId + 1) % stateMax;
        }
        return stateId;
    }

    private int getNextStateId() {
        if (stateMax >= 2) {
            return (stateId + 1) % stateMax;
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
                paint.setColor((alpha << 24) | animeColor);
            } else {
                paint.setColor(0xff000000);
            }

            // 領域の幅に合わせて伸縮
            canvas.drawBitmap(icon, new Rect(0,0,icon.getWidth(), icon.getHeight()),
                    new Rect((int)drawPos.x, (int)drawPos.y, (int)drawPos.x + ITEM_W,(int)drawPos.y + ITEM_H),
                    paint);
        }

        // 子要素
        if (childItems != null) {
            for (UMenuItem item : childItems) {
                if (!item.isShow) continue;

                item.draw(canvas, paint, drawPos);
            }
        }
    }

    /**
     * アニメーション開始
     */
    public void startAnim() {
        menuBar.setAnimating(true);
        isAnimating = true;
        animeFrame = 0;
        animeFrameMax = ANIME_FRAME;
        animeColor = Color.argb(0,255,255,255);
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
    public boolean touchEvent(ViewTouch vt) {
        return false;
    }

    /**
     * クリック処理
     * @param touchX
     * @param touchY
     * @return
     */
    public boolean checkTouch(ViewTouch vt, float touchX, float touchY) {
        if (vt.checkInsideCircle(touchX, touchY, pos.x + ITEM_W / 2, pos.y + ITEM_W / 2, ITEM_W / 2))
        {
            if (vt.type != TouchType.Touch) return false;

            // 子要素を持っていたら Open/Close
            if (childItems != null) {
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
                    mCallbacks.menuItemClicked(itemId, stateId);
                }
            }
            // アニメーション
            startAnim();

            return true;
        }

        // 子要素
        if (isOpened() && childItems != null) {
            for (UMenuItem child : childItems) {
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
        if (childItems == null) return;

        isOpened = true;

        int count = 1;
        for (UMenuItem item : childItems) {
            item.setPos(0, 0);
            // 親の階層により開く方向が変わる
            item.isClosing = false;
            item.setShow(true);

            if (nestCount == 0) {
                // 縦方向
                item.startMovingPos(0, -count * (ITEM_H + CHILD_MARGIN_V), ANIME_FRAME);
            } else if (nestCount == 1) {
                // 横方向
                item.startMovingPos(count * (ITEM_W + CHILD_MARGIN_H), 0, ANIME_FRAME);
            }
            count++;
        }
    }

    /**
     * メニューをCloseしたときの処理
     */
    public void closeMenu() {
        if (childItems == null) return;

        isOpened = false;

        for (UMenuItem item : childItems) {
            item.startMovingPos(0, 0, ANIME_FRAME);
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
    public boolean doAction() {
        boolean allFinished = true;

        // 自分の処理
        // 移動
        if (autoMoving()) {
            allFinished = false;
        } else if (isClosing) {
            setShow(false);
        }

        // アニメーション
        if (animate()) {
            allFinished = false;
        }

        // 子要素のdoAction
        if (childItems != null) {
            for (UMenuItem item : childItems) {
                if (item.doAction()) {
                    allFinished = false;
                }
            }
        }
        return !allFinished;
    }

    /**
     * Drawableインターフェース
     */
    public PointF getDrawOffset() {
        return null;
    }
}
