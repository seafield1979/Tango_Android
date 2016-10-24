package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collections;
import java.util.LinkedList;

/**
 * アイコンの整列とアニメーション、挿入のテスト
 */
public class IconsView extends View implements View.OnTouchListener {
    enum viewState {
        none,
        drag,               // アイコンのドラッグ中
        icon_moving,        // アイコンの一変更後の移動中
    }

    private static final int RECT_ICON_NUM = 10;
    private static final int CIRCLE_ICON_NUM = 10;
    private static final int ICON_W = 200;
    private static final int ICON_H = 150;
    private static final int MOVING_TIME = 10;
    private boolean firstDraw = false;
    private int skipFrame = 3;  // n回に1回描画
    private int skipCount;

    // アイコンを動かす仕組み
    private IconBase dragIcon;

    // クリック判定の仕組み
    private ViewTouch viewTouch = new ViewTouch();

    // アニメーション用
    private viewState state = viewState.none;

    private Paint paint = new Paint();
    private TouchEventCallbacks _callbacks;
    private LinkedList<IconBase> icons = new LinkedList<IconBase>();

    public void setCallbacks(TouchEventCallbacks callbacks){
        _callbacks = callbacks;
    }

    public IconsView(Context context) {
        this(context, null);
    }

    public IconsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);

        // アイコンを追加
        for (int i=0; i<RECT_ICON_NUM; i++) {
            IconBase icon = new IconBook(0, 0, ICON_W, ICON_H);
            icons.add(icon);
            int color = 0;
            switch (i%3) {
                case 0:
                    color = Color.rgb(255,0,0);
                    break;
                case 1:
                    color = Color.rgb(0,255,0);
                    break;
                case 2:
                    color = Color.rgb(0,0,255);
                    break;
            }
            icon.setColor(color);
        }

        for (int i=0; i<CIRCLE_ICON_NUM; i++) {
            IconBase icon = new IconCard(0, 0, ICON_W, ICON_H);
            icons.add(icon);
            int color = 0;
            switch (i%3) {
                case 0:
                    color = Color.rgb(255,0,0);
                    break;
                case 1:
                    color = Color.rgb(0,255,0);
                    break;
                case 2:
                    color = Color.rgb(0,0,255);
                    break;
            }
            icon.setColor(color);
        }

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (firstDraw == false) {
            firstDraw = true;
            sortRects(false);
        }

        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        switch (state) {
            case none:
                for (IconBase icon : icons) {
                    if (icon == null) continue;
                    icon.draw(canvas, paint);
                }
                break;
            case drag:
                for (IconBase icon : icons) {
                    if (icon == null || icon == dragIcon) continue;
                    icon.draw(canvas, paint);
                }
                if (dragIcon != null) {
                    dragIcon.draw(canvas, paint);
                }
                break;
            case icon_moving:
                boolean allFinish = true;
                for (IconBase icon : icons) {
                    if (icon == null || icon == dragIcon) continue;
                    if (!icon.move()) {
                        allFinish = false;
                    }
                    icon.draw(canvas, paint);
                }
                if (allFinish) {
                    state = viewState.none;
                } else {
                    invalidate();
                }
                break;
        }
    }

    /**
     * アイコンを整列する
     * Viewのサイズが確定した時点で呼び出す
     */
    public void sortRects(boolean animate) {
        int column = this.getWidth() / (ICON_W + 20);
        if (column <= 0) {
            return;
        }

        if (animate) {
            int i=0;
            for (IconBase icon : icons) {
                int x = (i%column) * (ICON_W + 20);
                int y = (i/column) * (ICON_H + 20);
                icon.startMove(x,y,MOVING_TIME);
                i++;
            }
            state = viewState.icon_moving;
            invalidate();
        }
        else {
            int i=0;
            for (IconBase icon : icons) {
                int x = (i%column) * (ICON_W + 20);
                int y = (i/column) * (ICON_H + 20);
                icon.setPos(x, y);
                i++;
            }
        }
    }

    /**
     * アイコンをクリックする処理
     * @param vt
     */
    private void clickIcons(ViewTouch vt) {
        // どのアイコンがクリックされたかを判定
        for (IconBase icon : icons) {
            if (icon.checkClick(vt.touchX, vt.touchY)) {
                break;
            }
        }
    }

    /**
     * アイコンをロングクリックする処理
     * @param vt
     */
    private void longClickIcons(ViewTouch vt) {

    }

    /**
     * アイコンをドラッグ開始
     * @param vt
     */
    private void dragStart(ViewTouch vt) {
        // タッチされたアイコンを選択する
        // 一番上のアイコンからタッチ判定したいのでリストを逆順（一番手前から）で参照する
        Collections.reverse(icons);
        for (IconBase icon : icons) {
            // 座標判定
            if (icon.x <= vt.touchX && vt.touchX < icon.getRight() &&
                    icon.y <= vt.touchY && vt.touchY < icon.getBottom())
            {
                dragIcon = icon;
                break;
            }
        }
        Collections.reverse(icons);

        state = viewState.drag;

        invalidate();
    }

    private void dragMove(ViewTouch vt) {
        // ドラッグ中のアイコンを移動
        if (dragIcon != null) {
            dragIcon.move((int)vt.moveX, (int)vt.moveY);
        }

        skipCount++;
        if (skipCount >= skipFrame) {
            invalidate();
            skipCount = 0;
        }
    }

    private void dragEnd(ViewTouch vt) {
        // ドロップ処理
        // 他のアイコンの上にドロップされたらドロップ処理を呼び出す
        if (dragIcon == null) return;

        boolean isDroped = false;
        for (IconBase icon : icons) {
            if (icon == dragIcon) continue;
            if (icon.checkDrop(vt.x, vt.y)) {
                switch(icon.getShape()) {
                    case CIRCLE:
                        // ドラッグ位置のアイコンと場所を交換する
                    {
                        int index = icons.indexOf(icon);
                        int index2 = icons.indexOf(dragIcon);
                        icons.remove(dragIcon);
                        icons.add(index, dragIcon);
                        icons.remove(icon);
                        icons.add(index2, icon);

                        // 再配置
                        sortRects(true);
                    }
                    break;
                    case RECT:
                        // ドラッグ位置にアイコンを挿入する
                    {
                        int index = icons.indexOf(icon);
                        icons.remove(dragIcon);
                        icons.add(index, dragIcon);

                        // 再配置
                        sortRects(true);
                    }
                    break;
                    case IMAGE:
                        break;
                }
                isDroped = true;
                break;
            }
        }

        // その他の場所にドロップされた場合
        if (!isDroped) {
            // 最後のアイコンの後の空きスペースにドロップされた場合
            IconBase lastIcon = icons.getLast();
            if ((lastIcon.getY() <= vt.y && vt.y <= lastIcon.getBottom() &&
                    lastIcon.getRight() <= vt.x) ||
                    (lastIcon.getBottom() <= vt.y))
            {
                // ドラッグ中のアイコンをリストの最後に移動
                icons.remove(dragIcon);
                icons.add(dragIcon);
            }

            // 再配置
            sortRects(true);
        }

        dragIcon = null;
    }

    public boolean onTouch(View v, MotionEvent e) {
        boolean ret = true;

        if (state == viewState.icon_moving) return true;

        TouchType touchType = viewTouch.checkTouchType(e);

        if (viewTouch.checkLongTouch()) {
            // ロングタッチの処理
            Log.v("view5", "Long Touch");
        }

        switch(touchType) {
            case Click:
                clickIcons(viewTouch);
                break;
            case LongClick:
                longClickIcons(viewTouch);
                break;
            case MoveStart:
                dragStart(viewTouch);
                break;
            case Moving:
                dragMove(viewTouch);
                break;
            case MoveEnd:
                dragEnd(viewTouch);
                break;
            case MoveCancel:
                sortRects(false);
                dragIcon = null;
                invalidate();
                break;
        }

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // trueを返す。こうしないと以降のMoveイベントが発生しなくなる。
                ret = true;
                if (dragIcon != null) {
                    _callbacks.touchCallback(e.getAction());
                }
                break;
            case MotionEvent.ACTION_UP:
                ret = true;

                _callbacks.touchCallback(e.getAction());
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                _callbacks.touchCallback(e.getAction());
                break;
            default:
        }

        // コールバック
        return ret;
    }
}
