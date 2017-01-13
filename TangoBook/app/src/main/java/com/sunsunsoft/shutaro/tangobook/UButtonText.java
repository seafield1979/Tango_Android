package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;


/**
 * テキストを表示するボタン
 */
public class UButtonText extends UButton {
    /**
     * Enums
     */

    /**
     * Consts
     */
    public static final String TAG = "UButtonText";

    protected static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    protected static final int PULL_DOWN_COLOR = UColor.DarkGray;

    /**
     * Member Variables
     */
    private String mText;
    private int mTextColor;
    private int mTextSize;
    private Bitmap mImage;
    private PointF mTextOffset = new PointF();
    private PointF mImageOffset = new PointF();
    private Size mImageSize;

    /**
     * Get/Set
     */

    public String getmText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setImageId(int imageId, Size imageSize) {
        mImage = UResourceManager.getBitmapById(imageId);
        mImageSize = imageSize;
    }

    public void setTextOffset(float x, float y) {
        mTextOffset.x = x;
        mTextOffset.y = y;
    }

    public void setImageOffset(float x, float y) {
        mImageOffset.x = x;
        mImageOffset.y = y;
    }

    /**
     * Constructor
     */
    public UButtonText(UButtonCallbacks callbacks, UButtonType type, int id,
                       int priority, String text,
                       float x, float y, int width, int height,
                       int textSize, int textColor, int color)
    {
        super(callbacks, type, id, priority, x, y, width, height, color);
        this.mText = text;
        if (textColor == 0) {
            textColor = DEFAULT_TEXT_COLOR;
        }
        this.mTextColor = textColor;
        mTextSize = textSize;
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
        // 色
        // 押されていたら明るくする
        int _color = color;

        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        int _height = size.height;

        if (type == UButtonType.BGColor) {
            // 押したら色が変わるボタン
            if (!enabled) {
                _color = disabledColor;
            }
            else if (isPressed) {
                _color = pressedColor;
            }
        }
        else {
            int _pressedColor = pressedColor;
            // 押したら凹むボタン
            if (!enabled) {
                _color = disabledColor;
                _pressedColor = disabledColor2;
            }
            if (isPressed || pressedOn) {
                _pos.y += PRESS_Y;
            } else {
                // ボタンの影用に下に矩形を描画
                int height = PRESS_Y + 40;
                UDraw.drawRoundRectFill(canvas, paint,
                        new RectF(_pos.x, _pos.y + size.height - height,
                                _pos.x + size.width, _pos.y + size.height),
                        BUTTON_RADIUS, _pressedColor, 0, 0);
            }
            _height -= PRESS_Y;

        }
        UDraw.drawRoundRectFill(canvas, paint,
                new RectF(_pos.x, _pos.y, _pos.x + size.width, _pos.y + _height),
                BUTTON_RADIUS, _color, 0, 0);

        // 画像
        if (mImage != null) {
            UDraw.drawBitmap(canvas, paint, mImage,
                    _pos.x + mImageOffset.x + (size.width - mImageSize.width) / 2,
                    _pos.y + mImageOffset.y + (size.height - mImageSize.height) / 2,
                                        mImageSize.width, mImageSize.height);
        }
        // テキスト
        if (mText != null) {
            float y = _pos.y + mTextOffset.y + size.height / 2;
            if (isPressButton()) {
                y -= PRESS_Y / 2;
            }
            UDraw.drawText(canvas, mText, UAlignment.Center, mTextSize,
                    _pos.x + mTextOffset.x + size.width / 2,
                    y, mTextColor);
        }
        // プルダウン
        if (pullDownIcon) {
            UDraw.drawTriangleFill(canvas, paint,
                    new PointF(_pos.x + size.width - 50 , _pos.y + size.height / 2),
                    30, 180, PULL_DOWN_COLOR);
        }
    }
}
