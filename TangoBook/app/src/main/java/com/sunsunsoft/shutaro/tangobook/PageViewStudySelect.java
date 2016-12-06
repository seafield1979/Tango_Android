package com.sunsunsoft.shutaro.tangobook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 学習するBookを選択するページ
 * あくまでBookを選択するページなので編集機能はない
 * ホームにCardアイコンは表示しない
 */

public class PageViewStudySelect extends UPageView implements UMenuItemCallbacks,
        UIconCallbacks, ViewTouchCallbacks, UWindowCallbacks, UButtonCallbacks,
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

    /**
     * Member varialbes
     */
    // Windows
    private UWindow[] mWindows = new UWindow[WindowType.values().length];
    // UIconWindow
    private UIconWindows mIconWinManager;

    // MessageWindow
    private ULogWindow mLogWin;

    // Dialog
    private DebugDialogs debugDialogs;
    private UDialogWindow mDialog;

    // メニューバー
    private MenuBarStudySelect mMenuBar;

    // クリック判定の仕組み
    private ViewTouch vt = new ViewTouch(this);

    private IconInfoDialog mIconInfoDlg;

    // Fragmentで内容を編集中のアイコン
    private UIcon editingIcon;


    /**
     * Get/Set
     */
    public PageViewStudySelect(Context context, View parentView) {
        super(context, parentView, PageView.TangoSelect.getDrawId());
    }


    protected void initDrawables() {
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        // 描画オブジェクトクリア
        UDrawManager.getInstance().initPage(drawPageId);

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
        UIconWindowStudySelect mainWindow = UIconWindowStudySelect.createInstance(mParentView, this, this, true, winDir, size1.width, size1.height, Color.WHITE);
        mWindows[WindowType.Icon1.ordinal()] = mainWindow;

        // Sub
        UIconWindow subWindow = UIconWindow.createInstance(mParentView, this, this, false, winDir, size2.width, size2.height, Color.LTGRAY);
        subWindow.isShow = false;
        mWindows[WindowType.Icon2.ordinal()] = subWindow;

        mIconWinManager = UIconWindows.createInstance(mainWindow, subWindow, width, height);
        mainWindow.setWindows(mIconWinManager);
        subWindow.setWindows(mIconWinManager);

        // アイコンの登録はMainとSubのWindowを作成後に行う必要がある
        mainWindow.init();
        subWindow.init();

        // UMenuBar
        mMenuBar = MenuBarStudySelect.createInstance(mParentView, this, width, height,
                Color.BLACK);
        mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;

        // ULogWindow
        if (mLogWin == null) {
            mLogWin = ULogWindow.createInstance(mContext, mParentView, LogWindowType.Fix,
                    0, 0, width, height);
            mWindows[WindowType.Log.ordinal()] = mLogWin;
            ULog.setLogWindow(mLogWin);
        }
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @return
     */
    public boolean draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);

        // Windowの処理
        // アクション(手前から順に処理する)
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (win == null) continue;
            if (win.doAction()) {
                return true;
            }
        }
        return false;
    }

    public boolean touchEvent(ViewTouch vt) {
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
     * ViewTouchCallbacks
     */
    public void longPressed() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                touchEvent(vt);
                mParentView.invalidate();
            }
        });
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
        }
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClick(int id) {
        switch (id) {
            case UDialogWindow.CloseDialogId:
                mDialog.closeDialog();
                break;
        }
        return false;
    }
    public boolean UButtonLongClick(int id) {
        return false;
    }

    /**
     * EditCardDialogCallbacks
     */
    public void submitEditCard(Bundle args) {
        if (args == null) return;

        int mode = args.getInt(EditCardDialogFragment.KEY_MODE, EditCardDialogMode.Create.ordinal
                ());
        if (mode == EditCardDialogMode.Create.ordinal()) {
            // 新たにアイコンを追加する

            UIconManager iconManager = mIconWinManager.getMainWindow().getIconManager();
            IconCard iconCard = (IconCard)(iconManager
                    .addNewIcon(IconType.Card, AddPos.Tail));
            if (iconCard == null) {
                return;
            }
            TangoCard card = (TangoCard)iconCard.getTangoItem();

            // 戻り値を取得
            card.setWordA(args.getString(EditCardDialogFragment.KEY_WORD_A, ""));
            card.setWordB(args.getString(EditCardDialogFragment.KEY_WORD_B, ""));
            card.setHintAB(args.getString(EditCardDialogFragment.KEY_HINT_AB, ""));
            card.setHintBA(args.getString(EditCardDialogFragment.KEY_HINT_BA, ""));
            card.setComment(args.getString(EditCardDialogFragment.KEY_COMMENT, ""));

            iconCard.updateTitle();

            // DB更新
            RealmManager.getCardDao().updateOne(card);

            // アイコン整列
            mIconWinManager.getMainWindow().sortIcons(false);
        } else {
            // 既存のアイコンを更新する

            TangoCard card = (TangoCard)editingIcon.getTangoItem();
            card.setWordA(args.getString(EditCardDialogFragment.KEY_WORD_A, ""));
            card.setWordB(args.getString(EditCardDialogFragment.KEY_WORD_B, ""));
            card.setHintAB(args.getString(EditCardDialogFragment.KEY_HINT_AB, ""));
            card.setHintBA(args.getString(EditCardDialogFragment.KEY_HINT_BA, ""));
            card.setComment(args.getString(EditCardDialogFragment.KEY_COMMENT, ""));

            editingIcon.updateTitle();
            // DB更新
            RealmManager.getCardDao().updateOne(card);
        }

        mParentView.invalidate();
    }

    public void cancelEditCard() {

    }

    /**
     * IconInfoDialogCallbacks
     */
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
     * アイコンを開く
     */
    public void IconInfoOpenIcon(UIcon icon) {
        openIcon(icon);
        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;
    }

    /**
     * 学習を開始する
     */
    public void IconInfoStudy(UIcon icon) {
        UPageViewManager.getInstance().stackPage(PageView.TangoStudy);
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
