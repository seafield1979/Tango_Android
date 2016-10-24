package com.sunsunsoft.shutaro.tangobook;

/**
 * Created by shutaro on 2016/10/24.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


import static com.sunsunsoft.shutaro.tangobook.TopViewSettings.drawIconId;

/**
 * ViewのonDrawで描画するアイコンの情報
 */
abstract public class IconBase {

    private static final String TAG = "MyIcon";
    private static int count;

    public int id;
    protected float x,y;
    protected int width,height;

    // 移動用
    protected boolean isMoving;
    protected int movingFrame;
    protected int movingFrameMax;
    protected float srcX, srcY;
    protected float dstX, dstY;

    protected IconType type;

    protected int color;

    public IconBase(IconType type, float x, float y, int width, int height) {
        this(type, x,y,width,height, Color.rgb(0,0,0));
    }

    public IconBase(IconType type, float x, float y, int width, int height, int color) {
        this.id = count;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        count++;
    }

    abstract public void draw(Canvas canvas, Paint paint);

    public IconType getShape() { return type; }
    // 座標、サイズのGet/Set
    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    public float getRight() {
        return x + width;
    }
    public float getBottom() {
        return y + height;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int w) {
        width = w;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int h) {
        height = h;
    }

    public Size getSize() {
        return new Size(width, height);
    }
    public void setSize(int w, int h) {
        width = w;
        height = h;
    }

    // 移動
    public void move(float moveX, float moveY) {
        x += moveX;
        y += moveY;
    }

    // 色
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * 自動移動開始
     * @param dstX  目的位置x
     * @param dstY  目的位置y
     * @param frame  移動にかかるフレーム数
     */
    protected void startMove(float dstX, float dstY, int frame) {
        if (x == dstX && y == dstY) {
            return;
        }
        srcX = x;
        srcY = y;
        this.dstX = dstX;
        this.dstY = dstY;
        movingFrame = 0;
        movingFrameMax = frame;
        isMoving = true;
    }

    /**
     * 移動
     * 移動開始位置、終了位置、経過フレームから現在位置を計算する
     * @return 移動完了したらtrue
     */
    protected boolean move() {
        if (!isMoving) return true;

        float ratio = (float)movingFrame / (float)movingFrameMax;
        x = srcX + ((dstX - srcX) * ratio);
        y = srcY + ((dstY - srcY) * ratio);


        movingFrame++;
        if (movingFrame >= movingFrameMax) {
            isMoving = false;
            x = dstX;
            y = dstY;
            return true;
        }
        return false;
    }

    public void click() {
        Log.v(TAG, "click");
    }
    public void longClick() {
        Log.v(TAG, "long click");
    }
    public void moving() {
        Log.v(TAG, "moving");
    }
    public void drop() {
        Log.v(TAG, "drop");
    }

    /**
     * クリックのチェックとクリック処理。このメソッドはすでにクリック判定された後の座標が渡される
     * @param clickX
     * @param clickY
     * @return
     */
    public boolean checkClick(float clickX, float clickY) {
        if (x <= clickX && clickX <= getRight() &&
                y <= clickY && clickY <= getBottom() )
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
        if (x <= dropX && dropX <= getRight() &&
                y <= dropY && dropY <= getBottom() )
        {
            return true;
        }
        return false;
    }


    // ドロップ処理
    //protected abstract void dropFunc();

    /**
     *
     */
    protected void drawId(Canvas canvas, Paint paint) {
        // idを表示
        if (drawIconId) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(30);
            canvas.drawText("" + id, x+10, y+height-30, paint);
        }
    }
}
