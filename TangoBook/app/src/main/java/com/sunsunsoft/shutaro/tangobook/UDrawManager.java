package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeMap;

/**
 * 描画優先度
 */
enum DrawPriority {
    Dialog(5),
    DragIcon(11),
    IconWindow(100),
    ;
    private final int priority;

    private DrawPriority(final int priority) {
        this.priority = priority;
    }

    public int p() {
        return this.priority;
    }
}

/**
 * 描画オブジェクトを管理するクラス
 * 描画するオブジェクトを登録すると一括で描画を行ってくれる
 * ※シングルトンなので getInstance() でインスタンスを取得する
 */
public class UDrawManager {
    public static final String TAG = "UDrawManager";

    private static UDrawManager singleton = new UDrawManager();

    public static UDrawManager getInstance() { return singleton; }

    // タッチ中のDrawableオブジェクト
    // タッチを放すまで他のオブジェクトのタッチ処理はしない
    private UDrawable touchingObj;

    // 同じプライオリティーのDrawableリストを管理するリスト
    private TreeMap<Integer, DrawList> lists;

    private LinkedList<UDrawable> removeRequest = new LinkedList<>();


    // Get/Set

    public UDrawable getTouchingObj() {
        return touchingObj;
    }

    public void setTouchingObj(UDrawable touchingObj) {
        this.touchingObj = touchingObj;
    }

    /**
     * 初期化
     * アクティビティが生成されるタイミングで呼ぶ
     */
    public void init() {
        lists = new TreeMap<>();
    }

    /**
     * 描画オブジェクトを追加
     * @param obj
     * @return
     */
    public DrawList addWithNewPriority(UDrawable obj, int priority) {
        obj.drawPriority = priority;
        return addDrawable(obj);
    }
    public DrawList addDrawable(UDrawable obj) {
        // 挿入するリストを探す
        Integer _priority = new Integer(obj.getDrawPriority());
        DrawList list = lists.get(_priority);
        if (list == null) {
            // まだ存在していないのでリストを生成
            list = new DrawList(obj.getDrawPriority());
            lists.put(_priority, list);
        }
        list.add(obj);
        obj.setDrawList(list);
        return list;
    }

    /**
     * リストに登録済みの描画オブジェクトを削除
     * 削除要求をバッファに積んでおき、描画前に削除チェックを行う
     * @param obj
     * @return
     */
    public void removeDrawable(UDrawable obj) {
        removeRequest.add(obj);
    }

    /**
     * 削除要求のリストの描画オブジェクトを削除する
     */
    private void removeRequestedList() {
        for (UDrawable obj : removeRequest) {
            Integer _priority = new Integer(obj.getDrawPriority());
            DrawList list = lists.get(_priority);
            if (list != null) {
                list.remove(obj);
            }
        }
        removeRequest.clear();
    }

    /**
     * 指定のプライオリティのオブジェクトを全て削除
     * @param priority
     */
    public void removeWithPriority(int priority) {
        lists.remove(new Integer(priority));
    }

    /**
     * DrawListのプライオリティを変更する
     * @param list1  変更元のリスト
     * @param priority
     */
    public void setPriority(DrawList list1, int priority) {
        // 変更先のプライオリティーを持つリストを探す
        Integer _priority = new Integer(priority);
        DrawList list2 = lists.get(_priority);
        if (list2 != null) {
            // すでに変更先のプライオリティーのリストがあるので交換
            int srcPriority = list1.getPriority();
            Integer _srcPriority = new Integer(srcPriority);
            lists.put(_priority, list1);
            lists.put(_srcPriority, list2);
        } else {
            lists.put(_priority, list1);
        }
    }

    /**
     * 追加済みのオブジェクトのプライオリティーを変更する
     * @param obj
     * @param priority
     */
    public void setPriority(UDrawable obj, int priority) {
        // 探す
        for (Integer pri : lists.keySet()) {
            DrawList list = lists.get(pri);
            if (list.contains(obj)) {
                if (pri == priority) {
                    // すでに同じPriorityにいたら末尾に移動
                    list.toLast(obj);
                }
                else {
                    list.remove(obj);
                    addDrawable(obj);
                    return;
                }
            }
        }
    }

    /**
     * 配下の描画オブジェクトを全て描画する
     * @param canvas
     * @param paint
     * @return true:再描画あり / false:再描画なし
     */
    public boolean draw(Canvas canvas, Paint paint) {
        boolean redraw = false;

        // 削除要求のかかったオブジェクトを削除する
        removeRequestedList();

        ULog.startAllCount();
        for (DrawList list : lists.descendingMap().values()) {
            if (list.draw(canvas, paint) ) {
                redraw = true;
            }
        }
        ULog.showAllCount();
        return redraw;
    }

    /**
     * タッチイベント処理
     * 描画優先度の高い順に処理を行う
     * @param vt
     * @return true:再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        for (DrawList list : lists.values()) {
            if (list.touchEvent(vt) ) {
                return true;
            }
        }
        return false;
    }
}

/**
 * 描画オブジェクトのリストを管理するクラス
 * プライオリティーやクリップ領域を持つ
 */
class DrawList
{
    // 描画範囲 この範囲外には描画しない
//    public Rect clipRect;
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
            Rect objRect = obj.getRect();

            if (obj.animate()) {
                allDone = false;
            }
            ULog.count(UDrawManager.TAG + "_" + priority);
            PointF offset = obj.getDrawOffset();
            obj.draw(canvas, paint, offset);
            drawId(canvas, paint, obj.getRect(), priority);

            if (priority == UIconWindow.DRAG_ICON_PRIORITY) {
                ULog.print(UDrawManager.TAG, "" + obj.getRect().bottom);
            }

            if (UDebug.drawIconId) {
                Rect _rect = obj.getRect();
            }
        }
        return !allDone;
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
            if (manager.getTouchingObj().touchEvent(vt)) {
                return true;
            }
            return false;
        }

        for(ListIterator it = list.listIterator(list.size()); it.hasPrevious();){
            UDrawable obj = (UDrawable)it.previous();
            if (obj.touchEvent(vt)) {
                if (vt.type == TouchType.Touch) {
                    manager.setTouchingObj(obj);

                }
                return true;
            }
        }
        return false;
    }
}
