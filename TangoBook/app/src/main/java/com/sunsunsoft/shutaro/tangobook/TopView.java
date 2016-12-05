package com.sunsunsoft.shutaro.tangobook;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 単語帳編集ページ
 */

public class TopView extends View
        implements View.OnTouchListener, ViewTouchCallbacks
{
    /**
     * Constants
     */
    public static final String TAG = "TopView";

    /**
     * Member varialbes
     */
    private UPageViewManager mPageManager;

    // クリック判定の仕組み
    private ViewTouch vt = new ViewTouch(this);

    private Context mContext;
    private Paint paint = new Paint();

    // Fragmentで内容を編集中のアイコン
    private UIcon editingIcon;


    /**
     * Get/Set
     */
    public TopView(Context context) {
        this(context, null);
    }

    public TopView(Context context, AttributeSet attrs) {
        this(context, attrs, null);
    }

    public TopView(Context context, AttributeSet attrs, NestedScrollingParent nestedParent) {
        super(context, attrs);
        this.setOnTouchListener(this);
        mContext = context;

        mPageManager = new UPageViewManager(context, this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        if (mPageManager.draw(canvas, paint)) {
            invalidate();
        }

        // マネージャに登録した描画オブジェクトをまとめて描画
        if (UDrawManager.getInstance().draw(canvas, paint)){
            invalidate();
        }
    }

    /**
     * タッチイベント処理
     * @param v
     * @param e
     * @return
     */
    public boolean onTouch(View v, MotionEvent e) {
        boolean ret = true;

        vt.checkTouchType(e);

        // PageManager以下のタッチ処理
        if (mPageManager.touchEvent(vt)) {
            invalidate();
        }
        // 描画オブジェクトのタッチ処理はすべてUDrawManagerにまかせる
        else if (UDrawManager.getInstance().touchEvent(vt)) {
            invalidate();
        }


        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // trueを返す。こうしないと以降のMoveイベントが発生しなくなる。
                ret = true;
                break;
            case MotionEvent.ACTION_UP:
                ret = true;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                break;
            default:
        }

        // コールバック
        return ret;
    }

    /**
     * メニューアイテムをタップしてアイコンを追加する
     * Androidのバックキーが押された時の処理
     * @return
     */
    public boolean onBackKeyDown() {
        return mPageManager.onBackKeyDown();
    }


    /**
     * ViewTouchCallbacks
     */
    public void longPressed() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPageManager.touchEvent(vt);
                invalidate();
            }
        });
    }
}
