package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * 描画可能なクラス
 * Drawableの共通処理を実装済み
 *
 * このクラスのサブクラスをDrawManagerで管理できる
 *
 * 描画の他に、自動移動、アニメーション等の機能も持っている
 */

abstract public class UDrawable {
    private static final String TAG = "UDrawable";

    public static double RAD = 3.1415 / 180.0;

    /**
     * メンバ変数
     */
    protected DrawList drawList;    // DrawManagerに描画登録するとnull以外になる
    protected PointF pos = new PointF();
    protected Size size = new Size();
    protected Rect rect = new Rect();
    protected int color;
    protected int drawPriority;     // DrawManagerに渡す描画優先度

    // 自動移動用
    protected boolean isMoving;
    protected boolean isMovingPos;
    protected boolean isMovingSize;
    protected int movingFrame;
    protected int movingFrameMax;
    protected PointF srcPos = new PointF();
    protected PointF dstPos = new PointF();
    protected Size srcSize = new Size();
    protected Size dstSize = new Size();

    // アニメーション用
    public static final int ANIME_FRAME = 20;
    protected boolean isAnimating;
    protected int animeFrame;
    protected int animeFrameMax;
    protected float animeRatio;

    public UDrawable(int priority, float x, float y, int width, int height)
    {
        this.setPos(x, y);
        this.setSize(width, height);
        updateRect();

        this.drawPriority = priority;
        this.color = Color.rgb(0,0,0);
    }

    /**
     *  Get/Set
     */
    public float getX() {
        return pos.x;
    }
    public void setX(float x) {
        pos.x = x;
    }

    public float getY() {
        return pos.y;
    }
    public void setY(float y) {
        pos.y = y;
    }

    public PointF getPos() {
        return pos;
    }

    public void setPos(float x, float y) {
        setPos(x, y, true);
    }
    public void setPos(float x, float y, boolean update) {
        pos.x = x;
        pos.y = y;
        if (rect != null && update) {
            updateRect();
        }
    }
    public void setPos(PointF pos) {
        setPos(pos, true);
    }

    public void setPos(PointF pos, boolean update) {
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        updateRect();
    }

    protected void updateRect() {
        if (rect == null) {
            rect = new Rect((int)pos.x, (int)pos.y, (int)pos.x + size.width, (int)pos.y + size.height);
        } else {
            rect.left = (int) pos.x;
            rect.right = (int) pos.x + size.width;
            rect.top = (int) pos.y;
            rect.bottom = (int) pos.y + size.height;
        }
    }

    public float getRight() {
        return pos.x + size.width;
    }
    public float getBottom() {
        return pos.y + size.height;
    }

    public int getWidth() {
        return size.width;
    }
    public void setWidth(int w) {
        size.width = w;
    }

    public int getHeight() {
        return size.height;
    }
    public void setHeight(int h) {
        size.height = h;
    }

    public void setSize(int width, int height) {
        size.width = width;
        size.height = height;
        updateRect();
    }
    public Rect getRect() {return rect;}
    public Rect getRectWithOffset(PointF offset) {
        return new Rect(rect.left + (int)offset.x, rect.top + (int)offset.y,
                rect.right + (int)offset.x, rect.bottom + (int)offset.y);
    }
    // 枠の分太いRectを返す
    public Rect getRectWithOffset(PointF offset, int frameWidth) {
        return new Rect(rect.left + (int)offset.x - frameWidth,
                rect.top + (int)offset.y - frameWidth,
                rect.right + (int)offset.x + frameWidth,
                rect.bottom + (int)offset.y + frameWidth);
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    abstract void draw(Canvas canvas, Paint paint, PointF offset);

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    abstract public boolean touchEvent(ViewTouch vt);

    /**
     * 移動
     * @param moveX
     * @param moveY
     */
    public void move(float moveX, float moveY) {
        pos.x += moveX;
        pos.y += moveY;
        updateRect();
    }

    /**
     * 自動移動(座標)
     * @param dstX  目的x
     * @param dstY  目的y
     * @param frame  移動にかかるフレーム数
     */
    public void startMovingPos(float dstX, float dstY, int frame) {
        if (!setMovingPos(dstX, dstY)) {
            // 移動不要
            return;
        }

        isMoving = true;
        isMovingPos = true;
        isMovingSize = false;
        movingFrame = 0;
        movingFrameMax = frame;
    }

    private boolean setMovingPos(float dstX, float dstY) {
        if (pos.x == dstX && pos.y == dstY) {
            return false;
        }
        srcPos.x = pos.x;
        srcPos.y = pos.y;
        dstPos.x = dstX;
        dstPos.y = dstY;
        return true;
    }

    /**
     * 自動移動(サイズ)
     * @param dstW
     * @param dstH
     * @param frame
     */
    public void startMovingSize(int dstW, int dstH, int frame) {
        if (!setMovingSize(dstW, dstH)) {
            // 移動不要
            return;
        }

        isMoving = true;
        isMovingPos = false;
        isMovingSize = true;
        movingFrame = 0;
        movingFrameMax = frame;
    }

    private boolean setMovingSize(int dstW, int dstH) {
        if (size.width == dstW && size.height == dstH) {
            return false;
        }
        srcSize.width = size.width;
        srcSize.height = size.height;
        dstSize.width = dstW;
        dstSize.height = dstH;
        return true;
    }

    /**
     * 自動移動(座標 & サイズ)
     * @param dstX
     * @param dstY
     * @param dstW
     * @param dstH
     * @param frame
     */
    public void startMoving(float dstX, float dstY, int dstW, int dstH, int frame) {
        boolean noMoving = true;

        if (setMovingPos(dstX, dstY)) {
            noMoving = false;
        }
        if (setMovingSize(dstW, dstH)) {
            noMoving = false;
        }
        if (!noMoving) {
            isMovingPos = true;
            isMovingSize = true;
            movingFrame = 0;
            movingFrameMax = frame;
            isMoving = true;
        }
    }

    /**
     * 移動
     * 移動開始位置、終了位置、経過フレームから現在位置を計算する
     * @return true:移動中
     */
    public boolean autoMoving() {
        if (!isMoving) return false;

        movingFrame++;
        if (movingFrame >= movingFrameMax) {
            if (isMovingPos) {
                setPos(dstPos);
            }
            if (isMovingSize) {
                setSize(dstSize.width, dstSize.height);
            }

            isMoving = false;
            isMovingPos = false;
            isMovingSize = false;
            updateRect();
            endMoving();
        } else {
            float ratio = (float)movingFrame / (float)movingFrameMax;
            if (isMovingPos) {
                setPos(srcPos.x + ((dstPos.x - srcPos.x) * ratio),
                        srcPos.y + ((dstPos.y - srcPos.y) * ratio));
            }
            if (isMovingSize) {
                setSize((int)(srcSize.width + (dstSize.width - srcSize.width) * ratio),
                        (int)(srcSize.height + (dstSize.height - srcSize.height) * ratio));
            }
        }
        return true;
    }

    /**
     * 自動移動完了時の処理
     */
    public void endMoving() {}

    /**
     * Drawableインターフェース
     */
    public void setDrawList(DrawList drawList) {
        this.drawList = drawList;
    }

    public DrawList getDrawList() {
        return drawList;
    }

    public int getDrawPriority() {
        return drawPriority;
    }

    public void setDrawPriority(int drawPriority) {
        this.drawPriority = drawPriority;
    }

    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        return null;
    }

    /**
     * アニメーション開始
     */
    public void startAnimation() {
        startAnimation(ANIME_FRAME);
    }
    public void startAnimation(int frameMax) {
        isAnimating = true;
        animeFrame = 0;
        animeFrameMax = frameMax;
        animeRatio = 0f;
    }

    /**
     * アニメーション終了時に呼ばれる処理
     */
    public void endAnimation() {
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
            endAnimation();
        } else {
            animeFrame++;
            animeRatio = (float) animeFrame / (float) animeFrameMax;
        }
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
     * アニメーションフレームからアルファ値(1.0 -> 0.0 -> 1.0)を取得する
     *
     * @return
     */
    public int getAnimeAlpha() {
        double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
        return (int)((1.0 -  Math.sin(v1 * RAD)) * 255);
    }
}
