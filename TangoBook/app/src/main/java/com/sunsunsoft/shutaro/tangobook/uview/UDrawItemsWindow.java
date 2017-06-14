package com.sunsunsoft.shutaro.tangobook.uview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.uview.UWindowCallbacks;

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
    protected float mOffsetX = MARGIN_H;
    protected float mOffsetY = MARGIN_V;


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
                             int width, int height, boolean newLine,
                             String text, int textColor, int color)
    {
        if (width == 0) {
            width = size.width - MARGIN_H * 2;
        }

        // 表示位置を更新
        updateOffsetPre(newLine, width, height);

        UButtonText button = new UButtonText(buttonCallbacks, UButtonType.Press,
                id, 0, text, mOffsetX, mOffsetY,
                width, height,
                BUTTON_TEXT_SIZE, textColor, color);
        mItems.add(button);
        isUpdate = true;

        // 表示位置を更新
        updateOffsetAfter(newLine, width, height);
        return button;
    }

    /**
     * TextViewを追加
     */
    public UTextView addTextView(String text, UAlignment alignment,
                                 boolean multiLine, boolean isDrawBG, boolean newLine,
                                 int width, int height,
                                 int textSize, int textColor, int bgColor)
    {
        float x = 0;

        // 表示位置を更新
        updateOffsetPre(newLine, width, height);

        switch(alignment) {
            case CenterX:
            case Center:
                x = size.width / 2;
                break;
            case CenterY:
            case None:
                x = mOffsetX;
                break;
        }
        UTextView textView = UTextView.createInstance(text, textSize, 0,
                alignment, size.width,
                multiLine, isDrawBG, x, mOffsetY, width, textColor, bgColor);
        mItems.add(textView);
        isUpdate = true;

        // 表示位置を更新
        updateOffsetAfter(newLine, width, height);

        return textView;
    }

    /**
     * Drawableを追加
     */
    public void addDrawable(UDrawable obj, boolean newLine) {
        // 表示位置を更新
        updateOffsetPre(newLine, obj.getWidth(), obj.getHeight());

        obj.pos.x = mOffsetX;
        obj.pos.y = mOffsetY;
        obj.updateRect();
        mItems.add(obj);

        isUpdate = true;

        // 表示位置を更新
        updateOffsetAfter(newLine, obj.getWidth(), obj.getHeight());
    }

    /**
     * 描画オブジェクトを追加後、最初の描画前に１回行う処理
     * @param canvas
     */
    private void updateLayout(Canvas canvas) {
        contentSize.height = (int) mOffsetY;

        updateWindow();
    }

    public void clear() {
        mItems.clear();
        mOffsetY = MARGIN_V;
    }

    /**
     * 描画オブジェクトを配置した後のオフセット座標の更新
     * @param newLine  必ず改行
     * @param objW   描画オブジェクトの幅
     * @param objH  描画オブジェクトの高さ
     */
    private void updateOffsetPre(boolean newLine, int objW, int objH) {
        if (newLine) {
            if (mOffsetX != MARGIN_H) {
                mOffsetX = MARGIN_H;
                mOffsetY += objH + MARGIN_V;
            }
        } else {
            // 幅がいっぱいになったら新しいライン
            if (mOffsetX + objW + MARGIN_H > size.width) {
                mOffsetX = MARGIN_H;
                mOffsetY += objH + MARGIN_V;
            }
        }
    }
    private void updateOffsetAfter(boolean newLine, int objW, int objH) {
        // 表示位置を更新
        if (newLine) {
            // 強制的に新しいライン
            mOffsetY += objH + MARGIN_V;
            mOffsetX = MARGIN_H;
        } else {
            mOffsetX += objW + MARGIN_H;
        }
    }

    /**
     * Window配下のアイテムのdoAction処理を呼び出す
     */
    public DoActionRet doAction(){
        if (!isShow) return DoActionRet.None;

        DoActionRet ret = DoActionRet.None;
        for (UDrawable item : mItems) {
            DoActionRet _ret = item.doAction();
            switch(_ret) {
                case Done:
                    return _ret;
                case Redraw:
                    ret = _ret;
                    break;
            }
        }
        return ret;
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

}