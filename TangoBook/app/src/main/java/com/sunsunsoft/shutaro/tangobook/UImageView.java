package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/13.
 *
 * 画像を表示するオブジェクト
 * 状態別に複数の画像を表示させることができる
 */

public class UImageView extends UDrawable {
    /**
     * Consts
     */
    public static final String TAG = "UImageView";
    private static final int TEXT_MARGIN = 10;

    /**
     * Member variables
     */

    protected LinkedList<Bitmap> images = new LinkedList<>();    // 画像
    protected String mTitle;             // 画像の下に表示するテキスト
    protected int mTitleSize;
    protected int mTitleColor;
    protected int mStateId;          // 現在の状態
    protected int mStateMax;         // 状態の最大値 addState で増える


    /**
     * Get/Set
     */
    public void setTitle(String text, int size, int color) {
        mTitle = text;
        mTitleSize = size;
        mTitleColor = color;
    }

    /**
     * Constructor
     */
    public UImageView(int priority, int imageId, float x, float y, int width, int height,
                      int color)
    {
        super(priority, x, y, width, height);


        Bitmap image = UResourceManager.getInstance().getBitmapWithColor(imageId, color);

        this.images.add(image);
        mStateId = 0;
        mStateMax = 1;
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        Bitmap _image;

        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        _image = images.get(mStateId);
        Rect _rect = new Rect((int)_pos.x, (int)_pos.y,
                (int)_pos.x + size.width,(int)_pos.y + size.height);

        // 領域の幅に合わせて伸縮
        canvas.drawBitmap(_image, new Rect(0,0,_image.getWidth(), _image.getHeight()),
                _rect, paint);

        // 下にテキストを表示
        if (mTitle != null) {
            UDraw.drawTextOneLine(canvas, paint, mTitle, UAlignment.CenterX, mTitleSize,
                    _rect.centerX(), _rect.bottom + TEXT_MARGIN, mTitleColor);
        }
    }

    /**
     * 状態を追加する
     * @param imageId 追加した状態の場合に表示する画像
     */
    public void addState(int imageId) {
        images.add(UResourceManager.getInstance().getBitmapById(imageId));
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

    public void setState(int state) {
        if (mStateMax > state) {
            mStateId = state;
        }
    }

    private int getNextStateId() {
        if (mStateMax >= 2) {
            return (mStateId + 1) % mStateMax;
        }
        return 0;
    }

}
