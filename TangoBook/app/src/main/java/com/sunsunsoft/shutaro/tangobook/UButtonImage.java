package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by shutaro on 2016/11/17.
 */

public class UButtonImage extends UButton {
    /**
     * Consts
     */
    public static final int TEXT_MARGIN = 10;

    /**
     * Member Variables
     */
    protected Bitmap image;             // 画像
    protected Bitmap pressedImage;      // タッチ時の画像
    protected String title;             // 画像の下に表示するテキスト
    protected int titleSize;
    protected int titleColor;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UButtonImage(UButtonCallbacks callbacks,
                        int id, int priority,
                        float x, float y,
                        int width, int height,
                        Bitmap image, Bitmap pressedImage )
    {
        super(callbacks, UButtonType.BGColor, id, priority, x, y, width, height, 0);
        this.image = image;
        this.pressedImage = pressedImage;
    }

    // 画像ボタン
    public static UButtonImage createButton(UButtonCallbacks callbacks,
                                            int id, int priority,
                                            float x, float y,
                                            int width, int height,
                                            Bitmap image, Bitmap pressedImage)
    {
        UButtonImage button = new UButtonImage(callbacks, id, priority,
                x, y, width, height,
                image, pressedImage);
        return button;
    }

    /**
     * Methods
     */
    /**
     * ボタンの下に表示するタイトルを設定する
     * @param title
     * @param titleSize
     * @param titleColor
     */
    public void setTitle(String title, int titleSize, int titleColor) {
        this.title = title;
        this.titleSize = titleSize;
        this.titleColor = titleColor;
    }


    /**
     * UDrawable
     */
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

        _image = image;
        Rect _rect = new Rect((int)_pos.x, (int)_pos.y,
                        (int)_pos.x + size.width,(int)_pos.y + size.height);
        if (isPressed) {
            if (pressedImage != null) {
                _image = pressedImage;
            } else {
                // BGの矩形を配置
                UDraw.drawRoundRectFill(canvas, paint,
                        new RectF(_rect.left - 10, _rect.top - 10,
                                _rect.right + 10, _rect.bottom + 10),
                        10, Color.argb(255,255,100,100), 0, 0);
            }
        }

        // 領域の幅に合わせて伸縮
        canvas.drawBitmap(_image, new Rect(0,0,_image.getWidth(), _image.getHeight()),
                _rect,
                paint);

        if (UDebug.drawRectLine) {
            this.drawRectLine(canvas, paint, offset, Color.YELLOW);
        }

        // 下にテキストを表示
        if (title != null) {
            UDraw.drawTextOneLine(canvas, paint, title, UDraw.UAlignment.CenterX, titleSize,
                    _rect.centerX(), _rect.bottom + TEXT_MARGIN, titleColor);
        }
    }
}
