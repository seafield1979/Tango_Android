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
 *  単語編集ページのUPageView
 */


public class PageViewTangoEdit extends UPageView implements UMenuItemCallbacks,
        UIconCallbacks, ViewTouchCallbacks, UWindowCallbacks, UButtonCallbacks,
        EditCardDialogCallbacks, EditBookDialogCallbacks, IconInfoDialogCallbacks,
        UDialogCallbacks
{

    enum WindowType {
        Icon1,
        Icon2,
        MenuBar,
        Log
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
    private MenuBarTangoEdit mMenuBar;

    // クリック判定の仕組み
    private ViewTouch vt = new ViewTouch(this);

    private IconInfoDialog mIconInfoDlg;

    // Fragmentで内容を編集中のアイコン
    private UIcon editingIcon;


    /**
     * Get/Set
     */
    public PageViewTangoEdit(Context context, View parentView) {
        super(context, parentView, PageView.TangoEdit.getDrawId());
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
        UIconWindow mainWindow = UIconWindow.createInstance(mParentView, this, this, true, winDir, size1.width, size1.height, Color.WHITE);
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
        mMenuBar = MenuBarTangoEdit.createInstance(mParentView, this, width, height,
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
//        for (int i=mWindows.length - 1; i >= 0; i--) {
//            UWindow win = mWindows[i];
//            if (!win.isShow()) continue;
//
//            if (win.touchEvent(vt)) {
//                return true;
//            }
//        }
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
     * Add icon
     */

    // Card追加用のダイアログを表示
    private void addCardDialog() {
        EditCardDialogFragment dialogFragment = EditCardDialogFragment.createInstance(this);

        dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                "fragment_dialog");
    }

    // Book追加用のダイアログを表示
    private void addBookDialog() {
        EditBookDialogFragment dialogFragment = EditBookDialogFragment.createInstance(this);

        dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                "fragment_dialog");
    }

    /**
     * Edit icon
     */
    private void editCardDialog(IconCard iconCard) {
        EditCardDialogFragment dialogFragment =
                EditCardDialogFragment.createInstance(this, (TangoCard)iconCard.getTangoItem());

        dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                "fragment_dialog");
    }

    private void editBookDialog(IconBook iconBook) {
        EditBookDialogFragment dialogFragment =
                EditBookDialogFragment.createInstance(this, (TangoBook)iconBook.getTangoItem());

        dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                "fragment_dialog");
    }

    /**
     * Copy icon
     */
    private void copyIcon(UIcon icon) {
        UIconManager iconManager =  icon.parentWindow.getIconManager();

        // コピー先のカードアイコンを作成
        UIcon newIcon = iconManager.copyIcon(icon, null);
        if (newIcon == null) {
            return;
        }

        // DB上のデータもコピーする
        switch (icon.getType()) {
            case Card: {

            }
            break;
            case Book: {
            }
            break;
        }
        icon.parentWindow.sortIcons(true);
    }

    // card
    private void addCardIcon() {
        UIconManager iconManager = mIconWinManager.getMainWindow().getIconManager();
        iconManager.addNewIcon(IconType.Card, AddPos.Tail);
        mIconWinManager.getMainWindow().sortIcons(true);

        mParentView.invalidate();
    }

    // book
    private void addBookIcon() {
        UIconManager iconManager = mIconWinManager.getMainWindow().getIconManager();
        iconManager.addNewIcon(IconType.Book, AddPos.Tail);
        mIconWinManager.getMainWindow().sortIcons(true);

        mParentView.invalidate();
    }

    /**
     * UMenuItemCallbacks
     */
    /**
     * メニューアイテムをタップした時のコールバック
     */
    public void menuItemClicked(int id, int stateId) {
        MenuBarTangoEdit.MenuItemId itemId = MenuBarTangoEdit.MenuItemId.toEnum(id);

        switch (itemId) {
            case AddTop:
                break;
            case AddCard:
                addCardDialog();
                break;
            case AddBook:
                addBookDialog();
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
            case Debug2RealmCopy:
            {
                RealmManager.createCopyToStorage();
                UPopupWindow popup = new UPopupWindow( UPopupType.OK,
                        "Copy realm file", true, mParentView.getWidth(), mParentView.getHeight());
                UDrawManager.getInstance().addDrawable(popup);
            }
            break;
            case Debug2RealmRestore:
                break;
        }
        ULog.print(TAG, "menu item clicked " + id);
    }

    public void menuItemCallback2() {
        ULog.print(TAG, "menu item moved");
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
                    mIconInfoDlg = IconInfoDialogCard.createInstance(mParentView, this, this, icon,
                            x, y);
                    break;
                case Book:
                    mIconInfoDlg = IconInfoDialogBook.createInstance(mParentView, this, this, icon,
                            x, y);
                    break;
                case Trash:
                    mIconInfoDlg = IconInfoDialogTrash.createInstance(mParentView, this, this, icon,
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
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {
            case CleanupDialogButtonOK:
                // ゴミ箱を空にする
                RealmManager.getItemPosDao().deleteItemsInTrash();
                mDialog.closeDialog();
                mIconInfoDlg.closeWindow();
                mIconInfoDlg = null;
                mIconWinManager.getSubWindow().sortIcons(false);
                break;
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
     * EditBookDialogCallbacks
     */
    public void submitEditBook(Bundle args) {
        if (args == null) return;

        int mode = args.getInt(EditCardDialogFragment.KEY_MODE, EditCardDialogMode.Create.ordinal
                ());
        if (mode == EditCardDialogMode.Create.ordinal()) {
            // 新たにアイコンを追加する
            UIconManager iconManager = mIconWinManager.getMainWindow().getIconManager();
            IconBook bookIcon = (IconBook) (iconManager.addNewIcon(IconType.Book, AddPos.Tail));
            if (bookIcon == null) {
                return;
            }
            TangoBook book = (TangoBook) bookIcon.getTangoItem();

            // 戻り値を取得
            book.setName(args.getString(EditBookDialogFragment.KEY_NAME, ""));
            book.setComment(args.getString(EditBookDialogFragment.KEY_COMMENT, ""));
            book.setColor(args.getInt(EditBookDialogFragment.KEY_COLOR, 0));
            bookIcon.updateTitle();

            // DB更新
            RealmManager.getBookDao().updateOne(book);
        } else {
            // 既存のアイコンを更新する

            TangoBook book = (TangoBook)editingIcon.getTangoItem();
            book.setName(args.getString(EditBookDialogFragment.KEY_NAME, ""));
            book.setComment(args.getString(EditCardDialogFragment.KEY_COMMENT, ""));

            editingIcon.updateTitle();
            // DB更新
            RealmManager.getBookDao().updateOne(book);
        }

        // アイコン整列
        mIconWinManager.getMainWindow().sortIcons(false);
        mParentView.invalidate();
    }
    public void cancelEditBook() {

    }

    /**
     * IconInfoDialogCallbacks
     */
    public void IconInfoEditIcon(UIcon icon) {
        switch (icon.getType()) {
            case Card: {
                editingIcon = icon;
                if (icon instanceof IconCard) {
                    editCardDialog((IconCard)editingIcon);
                    mIconInfoDlg.closeWindow();
                    mIconInfoDlg = null;
                }
            }
            break;
            case Book: {
                editingIcon = icon;
                if (icon instanceof IconBook) {
                    editBookDialog((IconBook)editingIcon);
                    mIconInfoDlg.closeWindow();
                    mIconInfoDlg = null;
                }
            }
            break;
        }
    }

    /**
     * アイコンをコピー
     */
    public void IconInfoCopyIcon(UIcon icon) {
        this.copyIcon(icon);
        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;
    }

    /**
     * アイコンをゴミ箱に移動
     */
    public void IconInfoThrowIcon(UIcon icon) {
        mIconWinManager.getMainWindow().moveIconIntoTrash(icon);
        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;
        mParentView.invalidate();
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
     * 学習開始
     */
    public void IconInfoStudy(UIcon icon) {
        // 編集ページでは学習開始は行えない
    }

    /**
     * アイコン配下をクリーンアップする
     */
    public static final int CleanupDialogButtonOK = 101;

    public void IconInfoCleanup(UIcon icon) {
        if (icon.getType() == IconType.Trash) {
            if (mDialog != null) {
                mDialog.closeDialog();
                mDialog = null;
            }
            // Daoデバッグ用のダイアログを表示
            mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                    this, this,
                    UDialogWindow.ButtonDir.Vertical, UDialogWindow.DialogPosType.Center,
                    false,
                    mParentView.getWidth(), mParentView.getHeight(),
                    Color.rgb(200,100,100), Color.WHITE);


            // 確認のダイアログを表示する
            mDialog.setTitle("Do you clean up?");

            // ボタンを追加
            mDialog.addButton(CleanupDialogButtonOK, "OK", Color.WHITE,
                    Color.rgb(150, 80, 80));
            mDialog.addCloseButton("Cancel");

            // 描画マネージャに登録
            mDialog.setDrawPriority(DrawPriority.Dialog.p());
        }
    }

    /**
     * ゴミ箱内のアイコンを元に戻す
     * @param icon
     */
    public void IconInfoReturnIcon(UIcon icon) {
        icon.parentWindow.moveIconIntoHome(icon, mIconWinManager.getMainWindow());

        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;
    }

    /**
     * ゴミ箱内のアイコンを１件削除する
     * @param icon
     */
    public void IconInfoDeleteIcon(UIcon icon) {
        icon.parentWindow.removeIcon(icon);

        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;

        mParentView.invalidate();
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {

    }
}
