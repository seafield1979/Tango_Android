package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/23.
 *
 * 描画オブジェクトを追加できるWindow
 * 描画オブジェクトのサイズに合わせてWindowサイズが自動で伸縮する
 * 画面に収まりきらなかったら(MaxHeightを超えたら)自動でスクロールバーを表示する
 */

public class UDrawItemsWindow extends UScrollWindow {
    /**
     * Enums
     */
    /**
     * Constants
     */
    public static final String TAG = "UDrawItemsWindow";
    protected static final int MARGIN_H = 50;
    protected static final int MARGIN_V = 50;
    protected static final int BUTTON_TEXT_SIZE = 50;

    /**
     * Member variables
     */
    protected LinkedList<UDrawable> mItems = new LinkedList<>();
    protected Rect mClipRect;
    protected boolean isUpdate;

    // リストの最後のアイテムの下端の座標
    protected float mBottomY = MARGIN_V;


    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UDrawItemsWindow(UWindowCallbacks callbacks,
                     int priority, float x, float y, int width, int
                             height, int color)
    {
        super(callbacks, priority, x, y, width, height, color, 0, 0, 0);
        mClipRect = new Rect();
    }

    /**
     * Methods
     */
    /**
     * ボタンを追加
     * ボタンを追加した時点では座標は設定しない
     * @param text
     * @param color
     */
    public UButton addButton(UButtonCallbacks buttonCallbacks, int id,
                             int width, int height,
                             String text, int textColor, int color)
    {
        if (width == 0) {
            width = size.width - MARGIN_H * 2;
        }
        UButtonText button = new UButtonText(buttonCallbacks, UButtonType.Press,
                id, 0, text, MARGIN_V, mBottomY,
                width, height,
                BUTTON_TEXT_SIZE, textColor, color);
        mItems.add(button);
        isUpdate = true;

        // 表示位置を更新
        mBottomY += button.size.height + MARGIN_V;
        return button;
    }

    /**
     * TextViewを追加
     */
    public UTextView addTextView(String text, UAlignment alignment,
                                 boolean multiLine, boolean isDrawBG,
                                 int textSize, int textColor,
                                 int bgColor)
    {
        float x = 0;
        switch(alignment) {
            case CenterX:
            case Center:
                x = size.width / 2;
                break;
            case CenterY:
            case None:
                x = MARGIN_H;
                break;
        }
        UTextView textView = UTextView.createInstance(text, textSize, 0,
                alignment, size.width,
                multiLine, isDrawBG, x, mBottomY, size.width - MARGIN_H * 2, textColor, bgColor);
        mItems.add(textView);
        isUpdate = true;

        mBottomY += textView.getHeight() + MARGIN_V;
        return textView;
    }

    /**
     * Drawableを追加
     */
    public void addDrawable(UDrawable obj) {
        obj.pos.y = mBottomY;
        obj.updateRect();
        mItems.add(obj);

        isUpdate = true;

        mBottomY += obj.getHeight() + MARGIN_V;
    }

    private void updateLayout(Canvas canvas) {
        contentSize.height = (int)mBottomY;

        updateWindow();
    }

    public void clear() {
        mItems.clear();
        mBottomY = MARGIN_V;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        if (!isShow) return;
        if (isUpdate) {
            isUpdate = false;
            updateLayout(canvas);
        }

        // Window内部
        PointF _pos = new PointF(frameSize.width, frameSize.height + topBarH);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        super.draw(canvas, paint, offset);
    }

    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        // クリッピング前の状態を保存
        canvas.save();

        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        // クリッピングを設定
        mClipRect.left = (int)_pos.x;
        mClipRect.right = (int)_pos.x + clientSize.width;
        mClipRect.top = (int)_pos.y;
        mClipRect.bottom = (int)_pos.y + clientSize.height;

        canvas.clipRect(mClipRect);

        // アイテムを描画

        // スクロール分を加算
        _pos.x -= contentTop.x;
        _pos.y -= contentTop.y;
        int drawCnt = 0;
        for (UDrawable item : mItems) {
            if (item.getBottom() < contentTop.y) continue;
            item.draw(canvas, paint, _pos);
            drawCnt++;

            if (item.pos.y + item.size.height > contentTop.y + clientSize.height) {
                // アイテムの下端が画面外にきたので以降のアイテムは表示されない
                break;
            }
        }
        ULog.print(TAG, "drawCnt:" + drawCnt);

        // クリッピングを解除
        canvas.restore();
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (offset == null) {
            offset = new PointF();
        }
        // 領域外なら何もしない
        if (!getClientRect().contains((int)vt.touchX(-pos.x - offset.x),
                (int)vt.touchY(-pos.y - offset.y)))
        {
            return false;
        }

        // アイテムのクリック判定処理
        PointF _offset = new PointF(pos.x + offset.x, pos.y - contentTop.y + offset.y);
        boolean isDraw = false;

        for (UDrawable item : mItems) {
            if (item.getBottom() < contentTop.y) continue;
            if (item.touchEvent(vt, _offset)) {
                isDraw = true;
            }
            if (item.pos.y + item.size.height > contentTop.y + clientSize.height) {
                // アイテムの下端が画面外にきたので以降のアイテムは表示されない
                break;
            }
        }

        if (super.touchEvent(vt, offset)) {
            isDraw = true;
        }
        return isDraw;
    }

    public boolean touchUpEvent(ViewTouch vt) {
        boolean isDraw = false;
        if (vt.isTouchUp()) {
            for (UDrawable item : mItems) {
                item.touchUpEvent(vt);
                isDraw = true;
            }
        }
        return isDraw;
    }

    /**
     * for Debug
     */
    public void addDummyItems(int count) {

        updateWindow();
    }
}