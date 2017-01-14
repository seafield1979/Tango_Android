package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/11/17.
 *
 * 画像を表示するボタン
 * 画像の下にテキストを表示することも可能
 */

public class UButtonImage extends UButton {
    /**
     * Consts
     */
    public static final int TEXT_MARGIN = 10;

    /**
     * Member Variables
     */
    protected LinkedList<Bitmap> images = new LinkedList<>();    // 画像
    protected Bitmap pressedImage;      // タッチ時の画像
    protected Bitmap disabledImage;     // disable時の画像
    protected String title;             // 画像の下に表示するテキスト
    protected int titleSize;
    protected int titleColor;
    protected int stateId;          // 現在の状態
    protected int stateMax;         // 状態の最大値 addState で増える


    /**
     * Get/Set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            if (disabledImage == null) {
                disabledImage = UUtil.convToGrayBitmap(images.getFirst());
            }
        }
    }

    public void setImage(Bitmap image) {
        if (image != null) {
            images.add(image);
        }
    }
    public void setPressedImage(Bitmap image) {
        if (image != null) {
            pressedImage = image;
        }
    }

    /**
     * Constructor
     */
    public UButtonImage(UButtonCallbacks callbacks,
                        int id, int priority,
                        float x, float y,
                        int width, int height,
                        int imageId, int pressedImageId )
    {
        super(callbacks, UButtonType.BGColor, id, priority, x, y, width, height, 0);

        if (imageId != -1) {
            this.images.add(UResourceManager.getInstance().getBitmapById(imageId));
        }

        if ( pressedImageId != -1) {
            this.pressedImage = UResourceManager.getInstance().getBitmapById(pressedImageId);
        } else {
            pressedColor = UColor.LightPink;
        }
        stateId = 0;
        stateMax = 1;
    }

    // 画像ボタン
    public static UButtonImage createButton(UButtonCallbacks callbacks,
                                            int id, int priority,
                                            float x, float y,
                                            int width, int height,
                                            int imageId, int pressedImageId)
    {
        UButtonImage button = new UButtonImage(callbacks, id, priority,
                x, y, width, height,
                imageId, pressedImageId);
        return button;
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
                -1, -1);
        button.setImage(image);
        button.setPressedImage(pressedImage);
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
     * 状態を追加する
     * @param imageId 追加した状態の場合に表示する画像
     */
    public void addState(int imageId) {
        images.add(UResourceManager.getInstance().getBitmapById(imageId));
        stateMax++;
    }
    public void addState(Bitmap image) {
        images.add(image);
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

    public void setState(int state) {
        if (stateMax > state) {
            stateId = state;
        }
    }

    private int getNextStateId() {
        if (stateMax >= 2) {
            return (stateId + 1) % stateMax;
        }
        return 0;
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

        if (!enabled) {
            _image = disabledImage;
        } else {
            _image = images.get(stateId);
        }
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
                        10, pressedColor, 0, 0);
            }
        }

        // 領域の幅に合わせて伸縮
        UDraw.drawBitmap(canvas, paint, _image, _rect);

        if (UDebug.drawRectLine) {
            this.drawRectLine(canvas, paint, offset, Color.YELLOW);
        }

        // 下にテキストを表示
        if (title != null) {
            UDraw.drawTextOneLine(canvas, paint, title, UAlignment.CenterX, titleSize,
                    _rect.centerX(), _rect.bottom + TEXT_MARGIN, titleColor);
        }
    }
}
