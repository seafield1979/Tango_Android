package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

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

    protected static final int TEXT_COLOR = Color.BLACK;
    protected static final int MARGIN_H = 50;
    protected static final int MARGIN_V = 50;

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
     * アクションIDを処理する
     * サブクラスでオーバーライドして使用する
     */
    public void setActionId(int id) {
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
