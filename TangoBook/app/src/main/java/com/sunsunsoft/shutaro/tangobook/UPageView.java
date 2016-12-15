package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 *
 * UViewPageManager配下のページの基底クラス
 */

abstract public class UPageView {
    /**
     * Consts
     */
    public static final String TAG = "UPagaView";

    /**
     * Member Variables
     */
    protected Context mContext;
    protected View mParentView;
    protected String mTitle;

    // UDrawManagerで描画を行うページ番号
    protected boolean isFirst = true;

    /**
     * Get/Set
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Constructor
     */
    public UPageView(Context context, View parentView, String title) {
        mContext = context;
        mParentView = parentView;
        mTitle = title;
    }

    /**
     * Methods
     */
    /**
     * スタックの先頭になって表示され始める前に呼ばれる
     */
    protected void onShow() {
    }

    /**
     * スタックの先頭でなくなって表示されなくなる前に呼ばれる
     */
    protected void onHide() {
        isFirst = true;
    }

    /**
     * 描画処理
     * サブクラスのdrawでこのメソッドを最初に呼び出す
     * @param canvas
     * @param paint
     * @return
     */
    protected boolean draw(Canvas canvas, Paint paint) {
        if (isFirst) {
            isFirst = false;
            initDrawables();
        }
        return false;
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    abstract void initDrawables();

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    abstract boolean onBackKeyDown();

}
