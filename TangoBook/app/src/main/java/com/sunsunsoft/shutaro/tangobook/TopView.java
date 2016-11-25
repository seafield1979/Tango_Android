package com.sunsunsoft.shutaro.tangobook;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

enum WindowType {
    Icon1,
    Icon2,
    MenuBar,
    Log
}

/**
 * 単語帳編集ページ
 */

public class TopView extends View
        implements View.OnTouchListener, UMenuItemCallbacks,
        UIconCallbacks, ViewTouchCallbacks, UWindowCallbacks{

    public static final String TAG = "TopView";

    // Windows
    private UWindow[] mWindows = new UWindow[WindowType.values().length];
    // UIconWindow
    private UIconWindows mIconWindows;

    // MessageWindow
    private ULogWindow mLogWin;

    // Dialog
    private DebugDialogs debugDialogs;

    // メニューバー
    private UMenuBar mMenuBar;

    // サイズ更新用
    private boolean isFirst = true;

    // クリック判定の仕組み
    private ViewTouch vt = new ViewTouch(this);

    private Context mContext;
    private NestedScrollingParent mNestedScrollingParent;
    private Paint paint = new Paint();



    // get/set
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
        mNestedScrollingParent = nestedParent;
    }


    private void initWindows(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // DebugDialogs
        debugDialogs = new DebugDialogs(this);

        // UIconWindow
        PointF pos1, pos2;
        Size size1, size2;
        UIconWindow.WindowDir winDir;
        pos1 = new PointF(0, 0);
        size1 = new Size(width, height);
        pos2 = new PointF(0, 0);
        size2 = new Size(width, height);

        if (width <= height) {
            winDir = UIconWindow.WindowDir.Vertical;
        } else {
            winDir = UIconWindow.WindowDir.Horizontal;
        }

        // Main
        UIconWindow mainWindow = UIconWindow.createInstance(this, this, this, true, winDir, pos1.x, pos1.y, size1.width, size1.height, Color.WHITE);
        mWindows[WindowType.Icon1.ordinal()] = mainWindow;

        // Sub
        UIconWindow subWindow = UIconWindow.createInstance(this, this, this, false, winDir, pos2.x, pos2.y, size2.width, size2.height, Color.LTGRAY);
        subWindow.isShow = false;
        mWindows[WindowType.Icon2.ordinal()] = subWindow;

        mIconWindows = UIconWindows.createInstance(mainWindow, subWindow, width, height);
        mainWindow.setWindows(mIconWindows);
        subWindow.setWindows(mIconWindows);

        // アイコンの登録はMainとSubのWindowを作成後に行う必要がある
        mainWindow.init();
        subWindow.init();

        // UMenuBar
        mMenuBar = UMenuBar.createInstance(this, this, width, height,
                Color.BLACK);
        mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;

        // ULogWindow
        if (mLogWin == null) {
            mLogWin = ULogWindow.createInstance(getContext(), this, LogWindowType.AutoDisappear,
                    0, 0, width / 2, height);
            mWindows[WindowType.Log.ordinal()] = mLogWin;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initWindows(getWidth(), getHeight());
        }
        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        // アイコンWindow
        // アクション(手前から順に処理する)
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (win == null) continue;
            if (win.doAction()) {
                invalidate();
            }
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
        // 描画オブジェクトのタッチ処理はすべてUDrawManagerにまかせる
        if (UDrawManager.getInstance().touchEvent(vt)) {
            invalidate();
        }

        switch(vt.type) {
            case Moving:
                //mNestedScrollingParent.onNestedScroll()
                break;
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
     * 各Windowのタッチ処理を変更する
     * @param vt
     * @return
     */
    private boolean WindowTouchEvent(ViewTouch vt) {
        // 手前から順に処理する
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (!win.isShow()) continue;

            if (win.touchEvent(vt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * メニューアイテムをタップしてアイコンを追加する
     * Androidのバックキーが押された時の処理
     * @return
     */
    public boolean onBackKeyDown() {
        // サブウィンドウが表示されていたら閉じる
        UIconWindow subWindow = mIconWindows.getSubWindow();
        if (subWindow.isShow()) {
            if (mIconWindows.hideWindow(subWindow, true)) {
                return true;
            }
        }
        return false;
    }

    /**
     * UMenuItemCallbacks
     */
    /**
     * メニューアイテムをタップした時のコールバック
     */
    public void menuItemClicked(MenuItemId id) {
        switch (id) {
            case AddTop:
                break;
            case AddCard:
                addCardIcon();
                break;
            case AddBook:
                addBookIcon();
                break;
            case AddBox:
                addBoxIcon();
                break;
            case SortTop:
                break;
            case Sort1:
                break;
            case Sort2:
                break;
            case Sort3:
                break;
            case ListTypeTop:
                break;
            case ListType1:
                break;
            case ListType2:
                break;
            case ListType3:
                break;
            case Debug1:
                // ログウィンドウの表示切り替え
                mLogWin.toggle();
                invalidate();
                break;
            case Debug2:
                debugDialogs.showDialog(DebugDialogs.DialogType.SelectDao);
                break;
            case Debug3:
                debugDialogs.showDialog(DebugDialogs.DialogType.DeleteDao);
                break;
            case Debug4:
                debugDialogs.showDialog(DebugDialogs.DialogType.Icons);
                break;
            case Debug5:
                initWindows(getWidth(), getHeight());
                invalidate();
                break;
        }
        ULog.print(TAG, "menu item clicked " + id);
    }

    public void menuItemCallback2() {
        ULog.print(TAG, "menu item moved");
    }

    /**
     * Add icons
     */
    // card
    private void addCardIcon() {
        UIconManager iconManager = mIconWindows.getMainWindow().getIconManager();
        iconManager.addNewIcon(IconType.Card, AddPos.Tail);
        mIconWindows.getMainWindow().sortIcons(true);

        invalidate();
    }

    // book
    private void addBookIcon() {
        UIconManager iconManager = mIconWindows.getMainWindow().getIconManager();
        iconManager.addNewIcon(IconType.Book, AddPos.Tail);
        mIconWindows.getMainWindow().sortIcons(true);

        invalidate();
    }

    // box
    private void addBoxIcon() {
        UIconManager iconManager = mIconWindows.getMainWindow().getIconManager();
        iconManager.addNewIcon(IconType.Box, AddPos.Tail);
        mIconWindows.getMainWindow().sortIcons(true);

        invalidate();
    }






    /**
     * UIconCallbacks
     */
    public void clickIcon(UIcon icon) {
        ULog.print(TAG, "clickIcon");
        switch(icon.type) {
            case Card:
                break;
            case Book:
                // 配下のアイコンをSubWindowに表示する
                if (icon instanceof IconBook) {
                    UIconWindow window = mIconWindows.getSubWindow();
                    window.setIcons(TangoParentType.Book, icon.getTangoItem().getId());

                    // SubWindowを画面外から移動させる
                    mIconWindows.showWindow(window, true);
                    invalidate();
                }
                break;
            case Box: {
                // 配下のアイコンをSubWindowに表示する
                if (icon instanceof IconBox) {
                    UIconWindow window = mIconWindows.getSubWindow();
                    window.setIcons(TangoParentType.Box, icon.getTangoItem().getId());

                    // SubWindowを画面外から移動させる
                    mIconWindows.showWindow(window, true);
                    invalidate();
                }
            }
            break;
        }
    }
    public void longClickIcon(UIcon icon) {
        ULog.print(TAG, "longClickIcon");
    }
    public void dropToIcon(UIcon icon) {
        ULog.print(TAG, "dropToIcon");
    }

    /**
     * ViewTouchCallbacks
     */
    public void longPressed() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindowTouchEvent(vt);
                invalidate();
            }
        });
    }

    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
        // Windowを閉じる
        for (UIconWindow _window : mIconWindows.getWindows()) {
            if (window == _window) {
                mIconWindows.hideWindow(_window, true);
                break;
            }
        }
    }

    /**
     * UButtonCallbacks
     */
    public void UButtonClick(int id) {

    }
    public void UButtonLongClick(int id) {

    }

}
