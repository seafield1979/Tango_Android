package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 学習するBookを選択するページ
 * あくまでBookを選択するページなので編集機能はない
 * ホームにCardアイコンは表示しない
 */

public class PageViewStudySelect extends UPageView implements UMenuItemCallbacks,
        UIconCallbacks, UWindowCallbacks, UButtonCallbacks,
        IconInfoDialogCallbacks, UDialogCallbacks
{
    /**
     * Enums
     */
    enum WindowType {
        Icon1,
        Icon2,
        MenuBar,
        Log
    }

    // メニューをタッチした時に返されるID
    enum MenuItemId {
        AddTop,
        AddCard,
        AddBook,
        SortTop,
        Sort1,
        Sort2,
        Sort3,
        ListTypeTop,
        ListType1,
        ListType2,
        ListType3,
        DebugTop,
        Debug1,
        Debug2,
        Debug3,
        Debug4,
        Debug5,
        Debug6,
        Debug2Top,
        Debug2RealmCopy,
        Debug2RealmRestore
    }


    /**
     * Constants
     */
    public static final String TAG = "TopView";

    // 開始ダイアログ(PreStudyWindow)でボタンが押されたときに使用する
    public static final int ButtonIdStartStudy = 2001;
    public static final int ButtonIdCancel = 2002;

    /**
     * Member varialbes
     */
    // Windows
    private UWindow[] mWindows = new UWindow[WindowType.values().length];
    private UIconWindows mIconWinManager;
    private PreStudyWindow mPreStudyWindow;
    private ULogWindow mLogWin;

    // Dialog
    private DebugDialogs debugDialogs;

    // メニューバー
    private MenuBarStudySelect mMenuBar;

    private IconInfoDialog mIconInfoDlg;

    // 選択中のBookIcon
    private IconBook mIconBook;


    /**
     * Get/Set
     */
    public PageViewStudySelect(Context context, View parentView, String title) {
        super(context, parentView, title);
    }

    public void onShow() {

    }

    public void onHide() {
        super.onHide();
    }

    protected void initDrawables() {
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // DebugDialogs
        debugDialogs = new DebugDialogs(mParentView);

        // UIconWindow
        Size size1, size2;
        UIconWindow.WindowDir winDir;
        size1 = new Size(width, height);
        size2 = new Size(width, height);

        if (width <= height) {
            winDir = UIconWindow.WindowDir.Vertical;
        } else {
            winDir = UIconWindow.WindowDir.Horizontal;
        }

        // Main
        UIconWindowStudySelect mainWindow = UIconWindowStudySelect.createInstance( this, this, true, winDir, size1.width, size1.height, Color.WHITE);
        mWindows[WindowType.Icon1.ordinal()] = mainWindow;

        // Sub
        UIconWindow subWindow = UIconWindow.createInstance( this, this, false, winDir, size2.width, size2.height, Color.LTGRAY);
        subWindow.isShow = false;
        mWindows[WindowType.Icon2.ordinal()] = subWindow;

        mIconWinManager = UIconWindows.createInstance(mainWindow, subWindow, width, height);
        mainWindow.setWindows(mIconWinManager);
        subWindow.setWindows(mIconWinManager);

        // アイコンの登録はMainとSubのWindowを作成後に行う必要がある
        mainWindow.init();
        subWindow.init();

        // PreStudyWindow 学習開始前に設定を行うウィンドウ
        mPreStudyWindow = new PreStudyWindow( this, this, mParentView);
        mPreStudyWindow.addToDrawManager();

        // UMenuBar
        mMenuBar = MenuBarStudySelect.createInstance( this, width, height,
                Color.BLACK);
        mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;

        // ULogWindow
        if (mLogWin == null) {
            mLogWin = ULogWindow.createInstance(mContext, LogWindowType.Fix,
                    0, 0, width, height);
            mWindows[WindowType.Log.ordinal()] = mLogWin;
            ULog.setLogWindow(mLogWin);
        }
    }

    /**
     * Androidのバックキーが押された時の処理
     * @return
     */
    public boolean onBackKeyDown() {
        // サブウィンドウが表示されていたら閉じる
        UIconWindow subWindow = mIconWinManager.getSubWindow();
        if (subWindow.isShow()) {
            if (mIconWinManager.hideWindow(subWindow, true)) {
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
    public void menuItemClicked(int id, int stateId) {
        MenuBarStudySelect.MenuItemId itemId = MenuBarStudySelect.MenuItemId.toEnum(id);

        switch (itemId) {
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
                mParentView.invalidate();
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
                break;
            case Debug6:
                UDrawManager.getInstance().showAllList(true, false);
                break;
        }
        ULog.print(TAG, "menu item clicked " + id);
    }

    /**
     * UIconCallbacks
     */
    public void iconClicked(UIcon icon) {
        ULog.print(TAG, "iconClicked");

        if (icon instanceof IconBook) {
            mIconBook = (IconBook)icon;
        }
        if (mIconInfoDlg != null) {
            mIconInfoDlg.closeWindow();
            mIconInfoDlg = null;
        }
        PointF winPos = icon.parentWindow.getPos();
        float x = winPos.x + icon.getX();
        float y = winPos.y + icon.getY() + UIconWindow.ICON_H;  // ちょい下

        // ゴミ箱のWindow内なら別のダイアログを表示
        if (icon.parentWindow.getParentType() == TangoParentType.Trash) {
            mIconInfoDlg = IconInfoDialogInTrash.createInstance(mParentView, this, this, icon,
                    x, y);
        } else {
            switch (icon.type) {
                case Card:
                    break;
                case Book:
                    mIconInfoDlg = IconInfoDialogBookStudy.createInstance(mParentView, this, this, icon,
                            x, y);
                    break;
            }
        }
        mParentView.invalidate();
    }

    public void longClickIcon(UIcon icon) {
        ULog.print(TAG, "longClickIcon");
    }

    public void iconDroped(UIcon icon) {
        ULog.print(TAG, "iconDroped");
    }

    /**
     * アイコンを開く
     * サブウィンドウに中のアイコンリストを表示
     * @param icon
     */
    public void openIcon(UIcon icon) {
        // 配下のアイコンをSubWindowに表示する
        switch (icon.getType()) {
            case Book:
            {
                UIconWindow window = mIconWinManager.getSubWindow();
                window.setIcons(TangoParentType.Book, icon.getTangoItem().getId());

                // SubWindowを画面外から移動させる
                mIconWinManager.showWindow(window, true);
                mParentView.invalidate();
            }
            break;
            case Trash:
            {
                UIconWindow window = mIconWinManager.getSubWindow();
                window.setIcons(TangoParentType.Trash, 0);

                // SubWindowを画面外から移動させる
                mIconWinManager.showWindow(window, true);
                mParentView.invalidate();
            }
        }
    }

    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
        // Windowを閉じる
        for (UIconWindow _window : mIconWinManager.getWindows()) {
            if (window == _window) {
                mIconWinManager.hideWindow(_window, true);
                break;
            }
        }
        if (mIconInfoDlg == window) {
            mIconInfoDlg.closeWindow();
            mIconInfoDlg = null;
        } else if (mPreStudyWindow == window) {
            mPreStudyWindow.setShow(false);
        }

    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {
            case ButtonIdStartStudy:
                // 学習開始
                PageViewManager.getInstance().startStudyPage((TangoBook)mIconBook.getTangoItem()
                        , true);
                break;
            case ButtonIdCancel:
                mPreStudyWindow.setShow(false);
                break;
        }
        return false;
    }

    /**
     * IconInfoDialogCallbacks
     */
    /**
     * 学習を開始する
     */
    public void IconInfoStudy(UIcon icon) {
        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;

        if (icon instanceof IconBook) {
            TangoBook book = (TangoBook)icon.getTangoItem();
            mPreStudyWindow.showWithBook(book);
        }
    }

    /**
     * アイコンを開く
     */
    public void IconInfoOpenIcon(UIcon icon) {
        openIcon(icon);
        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;
    }


    public void IconInfoEditIcon(UIcon icon) {
    }

    /**
     * アイコンをコピー
     */
    public void IconInfoCopyIcon(UIcon icon) {
    }

    /**
     * アイコンをゴミ箱に移動
     */
    public void IconInfoThrowIcon(UIcon icon) {
    }


    /**
     * アイコン配下をクリーンアップする
     */
    public void IconInfoCleanup(UIcon icon) {
    }

    /**
     * ゴミ箱内のアイコンを元に戻す
     * @param icon
     */
    public void IconInfoReturnIcon(UIcon icon) {
    }

    /**
     * ゴミ箱内のアイコンを１件削除する
     * @param icon
     */
    public void IconInfoDeleteIcon(UIcon icon) {
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {

    }
}
