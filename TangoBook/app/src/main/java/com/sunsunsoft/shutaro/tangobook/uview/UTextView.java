package com.sunsunsoft.shutaro.tangobook.uview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sunsunsoft.shutaro.tangobook.TouchType;
import com.sunsunsoft.shutaro.tangobook.util.Size;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;

/**
 * テキストを表示する
 */
public class UTextView extends UDrawable {
    /**
     * Constracts
     */
    // BGを描画する際の上下左右のマージン
    protected static final int MARGIN_H = 30;
    protected static final int MARGIN_V = 15;

    protected static final int DEFAULT_TEXT_SIZE = 50;
    protected static final int DEFAULT_COLOR = Color.BLACK;
    protected static final int DEFAULT_BG_COLOR = Color.WHITE;

    /**
     * Member variables
     */
    protected String text;
    protected UAlignment alignment;
    protected Size mMargin = new Size(MARGIN_H, MARGIN_V);
    protected int textSize;
    protected int bgColor;
    protected int canvasW;
    protected boolean multiLine;      // 複数行表示する

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
            setSize(size.width + mMargin.width * 2, size.height + mMargin.height * 2);
        } else {
            setSize(size.width, size.height);
        }
        updateRect();
    }

    public void setMargin(int width, int height) {
        mMargin.width = width;
        mMargin.height = height;
        updateSize();
    }

    /**
     * Constructor
     */
    public UTextView(String text, int textSize, int priority,
                     UAlignment alignment, int canvasW,
                     boolean multiLine, boolean isDrawBG, boolean marginH,
                     float x, float y,
                     int width,
                     int color, int bgColor)
    {
        super( priority, x, y, width, textSize);

        this.text = text;
        this.alignment = alignment;
        this.multiLine = multiLine;
        this.isDrawBG = isDrawBG;
        this.textSize = textSize;
        this.canvasW = canvasW;
        this.color = color;
        this.bgColor = bgColor;

        // テキストを描画した時のサイズを取得
        if (width == 0) {
            size = getTextSize(canvasW);
        }
        updateSize();
    }

    public static UTextView createInstance(String text, int textSize, int priority,
                                           UAlignment alignment, int canvasW,
                                           boolean multiLine, boolean isDrawBG,
                                           float x, float y,
                                           int width,
                                           int color, int bgColor)
    {
        UTextView instance = new UTextView(text, textSize, priority, alignment, canvasW,
                multiLine, isDrawBG, true,
                x, y, width, color, bgColor);

        return instance;
    }

    // シンプルなTextViewを作成
    public static UTextView createInstance(String text, int priority,
                                           int canvasW, boolean isDrawBG,
                                           float x, float y)
    {
        UTextView instance = new UTextView(text, DEFAULT_TEXT_SIZE, priority, UAlignment.None,
                canvasW, false, isDrawBG, true,
                x, y, 0, DEFAULT_COLOR, DEFAULT_BG_COLOR);
        return instance;
    }

    /**
     * Methods
     */

    protected void updateSize() {
        Size size = getTextSize(canvasW);
        if (isDrawBG) {
            size = addBGPadding(size);
        }
        setSize(size.width, size.height);
    }

    /**
     * テキストを囲むボタン部分のマージンを追加する
     * @param size
     * @return マージンを追加した Size
     */
    protected Size addBGPadding(Size size) {
        if (size == null) {
            return new Size(0, 0);
        }
        size.width += mMargin.width * 2;
        size.height += mMargin.height * 2;
        return size;
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        PointF _linePos = new PointF(_pos.x, _pos.y);

        UAlignment _alignment = alignment;

        if (isDrawBG) {
            PointF bgPos = new PointF(_pos.x, _pos.y);
            switch (alignment) {
                case CenterX:
                    bgPos.x -= size.width / 2;
                    _pos.y += mMargin.height;
                    break;
                case CenterY:
                    _pos.x += mMargin.height;
                    bgPos.y -= size.height / 2;
                    break;
                case Center:
                    bgPos.x -= size.width / 2;
                    bgPos.y -= size.height / 2;
                    break;
                case None:
                    _pos.x += mMargin.width;
                    _pos.y += mMargin.height;
                    break;
                case Right:
                    bgPos.x -= size.width;
                    _pos.x -= mMargin.width;
                    _pos.y += mMargin.height;
                    break;
                case Right_CenterY:
                    bgPos.x -= size.width;
                    _pos.x -= mMargin.width;
                    bgPos.y -= size.height / 2;
                    break;
            }

            if (!multiLine) {
                if (alignment == UAlignment.CenterX ||
                        alignment == UAlignment.None ||
                        alignment == UAlignment.Right)
                {
                    _pos.y += textSize / 2;
                }
            }

            // Background
            if (isDrawBG && bgColor != 0) {
                drawBG(canvas, paint, bgPos);
            }

            // BGの中央にテキストを表示したいため、aligmentを書き換える
            if (!multiLine) {
                switch (alignment) {
                    case CenterX:
                        _alignment = UAlignment.Center;
                        break;
                    case None:
                        _alignment = UAlignment.CenterY;
                        break;
                    case Right:
                        _alignment = UAlignment.Right_CenterY;
                        break;
                }
            }
        }
        if (multiLine) {
            UDraw.drawText(canvas, text, _alignment, textSize, _pos.x, _pos.y, color);
        } else {
            UDraw.drawTextOneLine(canvas, paint, text, _alignment, textSize,
                    _pos.x, _pos.y,
                    color);
        }

        // x,yにラインを表示 for Debug
        if (UDebug.drawTextBaseLine) {
            UDraw.drawLine(canvas, paint, _linePos.x - 50, _linePos.y,
                    _linePos.x + 50, _linePos.y, 3, Color.RED);
            UDraw.drawLine(canvas, paint, _linePos.x, _linePos.y - 50,
                    _linePos.x, _linePos.y + 50, 3, Color.RED);
        }
    }

    /**
     * 背景色を描画する
     * @param canvas
     * @param paint
     */
    protected void drawBG(Canvas canvas, Paint paint, PointF pos) {
        if (multiLine) {
            paint.setColor(bgColor);
            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(pos.x, pos.y, pos.x + size.width, pos.y + size.height),
                    20, bgColor, 0, 0);
        } else {
            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(pos.x, pos.y, pos.x + size.width, pos.y + size.height),
                    20, bgColor, 0, 0);
        }
    }

    /**
     * テキストのサイズを取得する（マルチライン対応）
     * @param canvasW
     * @return
     */
    public Size getTextSize(int canvasW) {

        if (multiLine) {
            return UDraw.getTextSize( canvasW, text, textSize);
        } else {
            return UDraw.getOneLineTextSize(new Paint(), text, textSize);
        }
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
