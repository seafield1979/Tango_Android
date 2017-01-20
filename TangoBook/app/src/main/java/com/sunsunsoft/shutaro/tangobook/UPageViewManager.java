package com.sunsunsoft.shutaro.tangobook;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 各ページを管理するクラス
 * 現在のページ番号を元に配下の PageView の処理を呼び出す
 */

abstract public class UPageViewManager {
    /**
     * Enums
     */
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    protected Context mContext;
    protected View mParentView;
    protected UPageView[] pages = new UPageView[PageView.values().length];
    protected LinkedList<PageView> pageIdStack = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Methods
     */
    /**
     * カレントのページIDを取得する
     * @return カレントページID
     */
    public PageView currentPage() {
        if (pageIdStack.size() > 0) {
            return pageIdStack.getLast();
        }
        return null;
    }

    /**
     * 配下のページを追加する
     *
     */
    abstract public void initPage(PageView pageView);

    /**
     * 描画処理
     * 配下のUViewPageの描画処理を呼び出す
     * @param canvas
     * @param paint
     * @return
     */
    public boolean draw(Canvas canvas, Paint paint) {
        PageView pageId = currentPage();
        if (pageId == null) return false;

        return pages[pageId.ordinal()].draw(canvas, paint);
    }

    /**
     * バックキーが押されたときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        // スタックをポップして１つ前の画面に戻る
        PageView pageId = currentPage();
        if (pageId == null) return false;

        // 各ページで処理
        if (pages[pageId.ordinal()].onBackKeyDown()) {
            // 何かしら処理がされたら何もしない
            return true;
        }

        // スタックを１つポップする
        if (pageIdStack.size() > 1) {
            if (popPage()) {
                return true;
            }
        }
        // スタックのページが１つだけなら終了
        return false;
    }

    /**
     * ページ切り替え時に呼ばれる処理
     */
    public void pageChanged(PageView pageId) {
        UDrawManager.clearDebugPoint();
    }

    /**
     * 表示ページを切り替える
     * @param pageId
     */
    public void changePage(PageView pageId) {
        pageChanged(pageId);

        // ページが未初期化なら初期化
        if (pages[pageId.ordinal()] == null) {
            initPage(pageId);
        }

        if (pageIdStack.size() > 0) {
            // 古いページの後処理(onHide)
            PageView page = pageIdStack.getLast();
            pages[page.ordinal()].onHide();

            pageIdStack.removeLast();

        }
        pageIdStack.add(pageId);

        // 新しいページの前処理(onShow)
        pageId = pageIdStack.getLast();
        pages[pageId.ordinal()].onShow();
        setActionBarTitle(pages[pageId.ordinal()].getTitle());
    }

    /**
     * ページを取得する
     */
    public UPageView getPageView(PageView pageId) {
        pageChanged(pageId);

        if (pages[pageId.ordinal()] == null) {
            initPage(pageId);
        }
        return pages[pageId.ordinal()];
    }

    /**
     * ページをスタックする
     * ソフトウェアキーの戻るボタンを押すと元のページに戻れる
     * @param pageId
     */
    public UPageView stackPage(PageView pageId) {
        pageChanged(pageId);

        // ページが未初期化なら初期化
        if (pages[pageId.ordinal()] == null) {
            initPage(pageId);
        }

        // 古いページの後処理
        if (pageIdStack.size() > 0) {
            PageView page = pageIdStack.getLast();
            pages[page.ordinal()].onHide();
        }

        pageIdStack.add(pageId);

        UPageView page = pages[pageId.ordinal()];
        if (page != null) {
            page.onShow();
            setActionBarTitle(pages[pageId.ordinal()].getTitle());
        }

        // アクションバーに戻るボタンを表示
        if (pageIdStack.size() >= 2) {
            showActionBarBack(true);
        }
        return page;
    }

    /**
     * ページをポップする
     * 下にページがあったら移動
     */
    public boolean popPage() {
        if (pageIdStack.size() > 0) {
            // 古いページの後処理
            PageView page = pageIdStack.getLast();
            pages[page.ordinal()].onHide();

            pageIdStack.removeLast();

            // 新しいページの前処理
            page = pageIdStack.getLast();
            pages[page.ordinal()].onShow();
            setActionBarTitle(pages[page.ordinal()].getTitle());

            if (pageIdStack.size() <= 1) {
                showActionBarBack(false);
            }
            changePage(page);

            return true;
        }
        return false;
    }

    /**
     * 外部からアクションIDを受け取る
     * アクションIDを現在表示中のページで処理される
     */
    public void setActionId(int id) {
        getPageView(currentPage()).setActionId(id);
    }

    /**
     * アクションバーの戻るボタン(←)を表示する
     * @param show false:非表示 / true:表示
     */
    private void showActionBarBack(boolean show) {
        ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
        actionBar.setHomeButtonEnabled(show);
        actionBar.setDisplayHomeAsUpEnabled(show);
    }

    /**
     * アクションバーのタイトル文字を設定する
     * @param text
     */
    public void setActionBarTitle(String text) {
        ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
        actionBar.setTitle(text);
    }
}
