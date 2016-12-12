package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.LinkedList;

/**
 * 複数のボタンを管理するクラス
 * 領域内に均等な大きさのボタンを並べて配置する
 */

public class UButtons extends UDrawable {
    public static final String TAG = "UButtons";
    private static final int BUTTON_MARGIN = 10;


    protected LinkedList<UButton> buttons = new LinkedList<>();
    protected UButtonType type;
    protected UButtonCallbacks mCallbacks;
    protected int textColor;
    protected int mTextSize;
    protected int column;     // 列(縦)
    protected int row;        // 行(横)

    /**
     * インスタンス生成
     */
    public UButtons(UButtonCallbacks callbacks, UButtonType type,
                    int priority, int textSize, int color, int textColor,
                    int row, int column,
                    float x, float y, int width, int height )
    {
        super(priority, x, y, width, height);
        this.mCallbacks = callbacks;
        mTextSize = textSize;
        this.color = color;
        this.type = type;
        this.textColor = textColor;
        this.column = (column < 1) ? 1 : column;
        this.row = (row < 1) ? 1 : row;

        // 描画マネージャに登録
        UDrawManager.getInstance().addDrawable(this);
    }

    /**
     * ボタンを追加する
     * @return true:追加できた / false:追加できなかった
     */
    public boolean add(int id, String text) {
        int maxSize = column * row;
        if (buttons.size() > maxSize) {
            return false;
        }

        int count = buttons.size();
        int buttonW = (size.width - ((row + 1) * BUTTON_MARGIN)) / row;
        int buttonH = (size.height - ((column + 1) * BUTTON_MARGIN)) / column;

        UButtonText button = new UButtonText(mCallbacks, type, id, 0, text,
                BUTTON_MARGIN + (count % row) * (buttonW + BUTTON_MARGIN),
                BUTTON_MARGIN + (count / row) * (buttonH + BUTTON_MARGIN),
                buttonW, buttonH,
                mTextSize, Color.WHITE, color);
        button.setTextColor(textColor);
        buttons.add(button);

        return true;
    }

    /**
     * 行列の全てが埋まるようにまとめてボタンを追加する
     * @param baseId
     * @param baseText
     * @return
     */
    public boolean addFull(int baseId, String baseText) {
        if (baseText == null) {
            baseText = "";
        }

        buttons.clear();

        for (int i=0; i<column; i++) {
            for (int j=0; j<row; j++) {
                int id = baseId + i * row + j;
                add(id, baseText + id);
            }
        }
        return true;
    }

    /**
     * ボタンのインスタンスを取得する
     * @param index
     * @return
     */
    public UButton getButton(int index) {
        return buttons.get(index);
    }
    public UButton getButton(int row, int column) {
        return buttons.get(row * column + row);
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _offset = new PointF(pos.x, pos.y);
        if (offset != null) {
            _offset.x += offset.x;
            _offset.y += offset.y;
        }

        for (UButton button : buttons) {
            button.draw(canvas, paint, _offset);
        }
    }

    /**
     * タッチイベント
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {

        for (UButton button : buttons) {
            if (button.touchEvent(vt, pos)) {
                return true;
            }
        }

        return false;
    }
}
