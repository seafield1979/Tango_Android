package com.sunsunsoft.shutaro.tangobook.uview;

/**
 * Created by shutaro on 2017/06/14.
 */


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import com.sunsunsoft.shutaro.tangobook.TouchType;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;
import com.sunsunsoft.shutaro.tangobook.util.ULog;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 描画オブジェクトのリストを管理するクラス
 * プライオリティーやクリップ領域を持つ
 */
public class DrawList
{
    // 描画範囲 この範囲外には描画しない
    private int priority;
    private LinkedList<UDrawable> list = new LinkedList<>();

    public DrawList(int priority) {
        this.priority = priority;
    }

    // Get/Set
    public int getPriority() {
        return priority;
    }

    /**
     * リストに追加
     * すでにリストにあった場合は末尾に移動
     * @param obj
     */
    public void add(UDrawable obj) {
        list.remove(obj);
        list.add(obj);
    }

    public boolean remove(UDrawable obj) {
        return list.remove(obj);
    }

    public void toLast(UDrawable obj) {
        list.remove(obj);
        list.add(obj);
    }

    /**
     * Is contain in list
     * @param obj
     * @return
     */
    public boolean contains(UDrawable obj) {
        for (UDrawable _obj : list) {
            if (obj == _obj) {
                return true;
            }
        }
        return false;
    }

    /**
     * リストの描画オブジェクトを描画する
     * @param canvas
     * @param paint
     * @return true:再描画あり (まだアニメーション中のオブジェクトあり)
     */
    public boolean draw(Canvas canvas, Paint paint) {
        // 分けるのが面倒なのでアニメーションと描画を同時に処理する
        boolean allDone = true;
        for (UDrawable obj : list) {

            if (obj.animate()) {
                allDone = false;
            }
            ULog.count(UDrawManager.TAG);
            PointF offset = obj.getDrawOffset();
            obj.draw(canvas, paint, offset);
            drawId(canvas, paint, obj.getRect(), priority);
        }
        return !allDone;
    }

    /**
     * 毎フレームの処理
     * @return
     */
    public DoActionRet doAction() {

        DoActionRet ret = DoActionRet.None;
        for (UDrawable obj : list) {
            DoActionRet _ret = obj.doAction();
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
     * プライオリティを表示する
     * @param canvas
     * @param paint
     */
    protected void drawId(Canvas canvas, Paint paint, Rect rect, int priority) {
        // idを表示
        if (!UDebug.drawIconId) return;

        paint.setColor(Color.BLACK);
        paint.setTextSize(30);

        String text = "" + priority;
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        canvas.drawText("" + priority, rect.centerX() - textRect.width() / 2, rect.centerY() - textRect.height() / 2, paint);

    }

    /**
     * タッチアップイベント処理
     * @param vt
     * @return
     */
    protected boolean touchUpEvent(ViewTouch vt) {
        boolean isRedraw = false;

        for(UDrawable obj : list){
            if (obj.touchUpEvent(vt)) {
                isRedraw = true;
            }
        }
        return isRedraw;
    }

    /**
     * タッチイベント処理
     * リストの末尾(手前に表示されている)から順に処理する
     * @param vt
     * @return true:再描画
     */
    protected boolean touchEvent(ViewTouch vt) {
        UDrawManager manager = UDrawManager.getInstance();

        if (vt.isTouchUp()) {
            manager.setTouchingObj(null);
        }
        // タッチを放すまではタッチしたオブジェクトのみ処理する
        if (manager.getTouchingObj() != null &&
                vt.type != TouchType.Touch)
        {
            if (manager.getTouchingObj().touchEvent(vt, null)) {
                return true;
            }
            return false;
        }

        // 手前に表示されたものから処理したいのでリストを逆順で処理する
        for(ListIterator it = list.listIterator(list.size()); it.hasPrevious();) {
            UDrawable obj = (UDrawable)it.previous();
            if (!obj.isShow()) continue;
            PointF offset = obj.getDrawOffset();

            if (obj.touchEvent(vt, offset)) {
                if (vt.type == TouchType.Touch) {
                    manager.setTouchingObj(obj);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * for Debug
     */
    /**
     * 描画オブジェクトをすべて出力する
     * @param isShowOnly  画面に表示中のもののみログを出力する
     */
    public void showAll(boolean ascending, boolean isShowOnly) {
        // パッケージ名を除去
        final String packageName = DrawList.class.getPackage().getName();

        if (ascending) {
            for (UDrawable obj : list) {
                if (!isShowOnly || obj.isShow) {
                    String objStr = obj.toString();
                    objStr = objStr.replace(packageName + ".", "");
                    ULog.print(UDrawManager.TAG, objStr + " isShow:" + obj.isShow);
                }
            }
        } else {
            for(ListIterator it = list.listIterator(list.size()); it.hasPrevious();){
                UDrawable obj = (UDrawable)it.previous();
                if (!isShowOnly || obj.isShow) {
                    String objStr = obj.toString();
                    objStr = objStr.replace(packageName + ".", "");
                    ULog.print(UDrawManager.TAG, objStr + " isShow:" + obj.isShow);
                }
            }
        }
    }
}
