package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * テキストを表示する
 */

public class UTextView extends UDrawable {
    /**
     * Constracts
     */
    // BGを描画する際の上下左右のマージン
    protected static final int MARGIN_H = 50;
    protected static final int MARGIN_V = 20;

    /**
     * Member variables
     */
    protected String text;
    protected UDraw.UAlignment alignment;
    protected int textSize;
    protected int bgColor;
    protected int canvasW;

    protected boolean isDrawBG;
    protected boolean isOpened;

    /**
     * Get/Set
     */
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;

        // サイズを更新
        Size size = getTextSize(canvasW);
        if (isDrawBG) {
            setSize(size.width + MARGIN_H * 2, size.height + MARGIN_V * 2);
        } else {
            setSize(size.width, size.height);
        }
        updateRect();
    }

    /**
     * Constructor
     */
    public UTextView(String text, int textSize, int priority,
                     UDraw.UAlignment alignment, int canvasW,
                     boolean isDrawBG,
                     float x, float y,
                     int width,
                     int color, int bgColor)
    {
        super( priority, x, y, width, textSize);

        this.text = text;
        this.alignment = alignment;
        this.isDrawBG = isDrawBG;
        this.textSize = textSize;
        this.canvasW = canvasW;
        this.color = color;
        this.bgColor = bgColor;

        // テキストを描画した時のサイズを取得
        Size size = getTextSize(canvasW);
        if (isDrawBG) {
            size = addBGPadding(size);
        }
        setSize(size.width, size.height);
    }

    public static UTextView createInstance(String text, int textSize, int priority,
                                           UDraw.UAlignment alignment, int canvasW,
                                           boolean isDrawBG,
                                           float x, float y,
                                           int width,
                                           int color, int bgColor)
    {
        UTextView instance = new UTextView(text, textSize, priority, alignment, canvasW, isDrawBG,
                x, y, width, color, bgColor);

        return instance;
    }

    /**
     * Methods
     */

    /**
     * テキストを囲むボタン部分のマージンを追加する
     * @param size
     * @return マージンを追加した Size
     */
    protected Size addBGPadding(Size size) {
        return new Size(size.width + MARGIN_H * 2, size.height + MARGIN_V * 2);
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x = pos.x + offset.x;
            _pos.y = pos.y + offset.y;
        }
        switch (alignment) {
            case CenterX:
                _pos.x = _pos.x - size.width / 2;
                break;
            case CenterY:
                _pos.y = _pos.y - size.height / 2;
                break;
            case Center:
                _pos.x = _pos.x - size.width / 2;
                _pos.y = _pos.y - size.height / 2;
                break;
        }

        if (isDrawBG) {
            drawBG(canvas, paint, _pos);

            _pos.x += MARGIN_H;
            _pos.y += MARGIN_V;
        }

        if (text != null) {
            // 改行ができるようにTextPaintとStaticLayoutを使用する
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(textSize);
            textPaint.setColor(color);

            StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                    canvas.getWidth() * 4 / 5, Layout.Alignment.ALIGN_NORMAL,
                    1.0f, 0.0f, false);

            canvas.save();
            canvas.translate(_pos.x, _pos.y);


            ///テキストの描画位置の指定
            textPaint.setColor(color);
            mTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 背景色を描画する
     * @param canvas
     * @param paint
     */
    protected void drawBG(Canvas canvas, Paint paint, PointF pos) {
        paint.setColor(bgColor);
        UDraw.drawRoundRectFill(canvas, paint,
                new RectF(pos.x, pos.y, pos.x + size.width, pos.y + size.height),
                20, bgColor);
    }

    /**
     * テキストのサイズを取得する（マルチライン対応）
     * @param canvasW
     * @return
     */
    public Size getTextSize(int canvasW) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        StaticLayout textLayout = new StaticLayout(text, textPaint,
                canvasW, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);

        int height = textLayout.getHeight();
        int maxWidth = 0;
        int _width;

        // 各行の最大の幅を計算する
        for (int i = 0; i < textLayout.getLineCount(); i++) {
            _width = (int)textLayout.getLineWidth(i);
            if (_width > maxWidth) {
                maxWidth = _width;
            }
        }

        return new Size(maxWidth, height);
    }

    /**
     * 矩形を取得
     * @return
     */
    public Rect getRect() {
        return new Rect((int)pos.x, (int)pos.y, (int)pos.x + size.width, (int)pos.y +
                size.height);
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return this.touchEvent(vt, null);
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (vt.type == TouchType.Touch) {
            if (offset == null) {
                offset = new PointF();
            }
            if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                isOpened = !isOpened;
                return true;
            }
        }
        return false;
    }
}
