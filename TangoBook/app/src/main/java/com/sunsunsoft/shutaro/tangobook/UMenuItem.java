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
    private static final int TEXT_SIZE = 30;

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

    public UMenuItem(UMenuBar menuBar, int id, Bitmap icon) {
        super(DRAW_PRIORITY, 0,0,ITEM_W,ITEM_H);
        this.mMenuBar = menuBar;
        this.mItemId = id;
        this.mStateId = 0;
        this.mStateMax = 1;
        if (icon != null) {
            this.icons.add(icon);
        }
    }

    public void setmParentItem(UMenuItem mParentItem) {
        this.mParentItem = mParentItem;
    }

    /**
     * テキストを追加する
     */
    public void addTitle(String title, UAlignment alignment, float x, float y, int
            color, int bgColor) {
        mTextTitle = UTextView.createInstance(title, TEXT_SIZE, 0, alignment,
                0, false, true, x, y, 0, color, bgColor);
        mShowTitle = true;
        mTextTitle.setMargin(10, 10);
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
            } else {
                paint.setColor(0xff000000);
            }

            // 領域の幅に合わせて伸縮
            canvas.drawBitmap(icon, new Rect(0,0,icon.getWidth(), icon.getHeight()),
                    new Rect((int)drawPos.x, (int)drawPos.y,
                            (int)drawPos.x + ITEM_W,(int)drawPos.y + ITEM_H),
                    paint);
            // タイトル
            if (mTextTitle != null && MySharedPref.getMenuHelpMode() ==
                    MenuHelpMode.Name) {
                mTextTitle.draw(canvas, paint, drawPos);
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
        if (vt.checkInsideCircle(touchX, touchY, pos.x + ITEM_W / 2, pos.y + ITEM_W / 2, ITEM_W / 2))
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
                item.startMoving(0, -count * (ITEM_H + CHILD_MARGIN_V), ANIME_FRAME);
            } else if (mNestCount == 1) {
                // 横方向
                item.startMoving(count * (ITEM_W + CHILD_MARGIN_H), 0, ANIME_FRAME);
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
        if (mChildItem != null) {
            for (UMenuItem item : mChildItem) {
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
