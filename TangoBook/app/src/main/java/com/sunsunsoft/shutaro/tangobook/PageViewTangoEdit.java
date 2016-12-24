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

import java.util.List;

/**
 * Created by shutaro on 2016/12/05.
 *
 *  単語編集ページのUPageView
 */


public class PageViewTangoEdit extends UPageView implements UMenuItemCallbacks,
        UIconCallbacks, UWindowCallbacks, UButtonCallbacks,
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

    private static final int MARGIN_H = 50;

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

    private IconInfoDialog mIconInfoDlg;

    // Fragmentで内容を編集中のアイコン
    private UIcon editingIcon;


    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public PageViewTangoEdit(Context context, View parentView, String title) {
        super(context, parentView, title);
    }


    /**
     * Methods
     */
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
        UIconWindow mainWindow = UIconWindow.createInstance(this, this, true, winDir, size1.width, size1.height, Color.WHITE);
        mainWindow.addToDrawManager();
        mWindows[WindowType.Icon1.ordinal()] = mainWindow;

        // Sub
        UIconWindow subWindow = UIconWindow.createInstance(this, this, false, winDir, size2.width, size2.height, Color.LTGRAY);
        subWindow.addToDrawManager();
        subWindow.isShow = false;
        mWindows[WindowType.Icon2.ordinal()] = subWindow;

        mIconWinManager = UIconWindows.createInstance(mainWindow, subWindow, width, height);
        mainWindow.setWindows(mIconWinManager);
        subWindow.setWindows(mIconWinManager);

        // アイコンの登録はMainとSubのWindowを作成後に行う必要がある
        mainWindow.init();
        subWindow.init();

        // UMenuBar
        mMenuBar = MenuBarTangoEdit.createInstance(mParentView, this, width, height, 0);
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
        // アイコンダイアログが開いていたら閉じる
        if (mIconInfoDlg != null) {
            mIconInfoDlg.closeWindow();
            mIconInfoDlg = null;
            return true;
        }

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

    // ダミーのCardを追加
    private void addDummyCard() {
        addCardIcon();
        mParentView.invalidate();
    }

    // ダミーのBookを追加
    private void addDummyBook() {
        addBookIcon();
    }

    // プリセットの単語帳を追加する
    private void addPresetBook() {
        PageViewManager.getInstance().stackPage(PageView.PresetBook);
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
        UIcon newIcon = iconManager.copyIcon(icon, AddPos.SrcNext);
        if (newIcon == null) {
            return;
        }

        // 単語帳なら配下のカードをコピーする
        if(icon.getType() == IconType.Book) {
            IconBook srcBook = (IconBook)icon;
            IconBook dstBook = (IconBook)newIcon;
            List<UIcon> icons = srcBook.getIcons();
            if (icons != null) {
                for (UIcon _icon : icons) {
                    if (_icon instanceof IconCard) {
                        IconCard newCardIcon = (IconCard)iconManager.copyIcon(_icon, null);
                        dstBook.mIconManager.addIcon(newCardIcon);

                        // DBに位置情報を追加
                        RealmManager.getItemPosDao().addOne(_icon.getTangoItem(), TangoParentType
                                .Book, dstBook.getTangoItem().getId());
                    }
                }
            }
        }

        icon.parentWindow.sortIcons(true);
    }

    // card
    private IconCard addCardIcon() {

        UIconManager iconManager = null;
        UIconWindow window = null;
        IconCard cardIcon = null;

        // Bookのサブウィンドウが開いていたらそちらに追加する
        window = mIconWinManager.getSubWindow();
        if (window.isShow() && window.getParentType() == TangoParentType.Book) {
            // サブウィンドウに追加
            iconManager = window.getIconManager();
            cardIcon = (IconCard)iconManager.addNewIcon(IconType.Card, TangoParentType.Book,
                    window.getParentId(), AddPos.Tail);
            // 親の単語帳アイコンのアイコンリストにも追加

        } else {
            window = mIconWinManager.getMainWindow();
            iconManager = window.getIconManager();
            cardIcon = (IconCard)iconManager.addNewIcon(IconType.Card, TangoParentType.Home, 0,
                    AddPos.Tail);
        }
        window.sortIcons(true);

        return cardIcon;
    }

    // book
    private void addBookIcon() {
        UIconManager iconManager = mIconWinManager.getMainWindow().getIconManager();
        iconManager.addNewIcon(IconType.Book, TangoParentType.Home, 0, AddPos.Tail);
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
            case AddDummyCard:
                addDummyCard();
                break;
            case AddDummyBook:
                addDummyBook();
                break;
            case AddPresetBook:
                addPresetBook();
                break;
            case SortByWordAsc: {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .TitleAsc);
                window.sortIcons(true);
            }
                break;
            case SortByWordDesc:
            {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .TitleDesc);
                window.sortIcons(true);
            }
                break;
            case SortByTimeAsc:
            {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .CreateDateAsc);
                window.sortIcons(true);
            }
                break;
            case SortByTimeDesc:
            {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .CreateDateDesc);
                window.sortIcons(true);
            }
                break;
            case Debug1:
                // ログウィンドウの表示切り替え
                mLogWin.toggle();
                mParentView.invalidate();
                break;
            case ShowMenuName:
            {
                MenuHelpMode helpMode = MySharedPref.getMenuHelpMode();
                if (helpMode == MenuHelpMode.Name) {
                    helpMode = MenuHelpMode.None;
                } else {
                    helpMode = MenuHelpMode.Name;
                }
                MySharedPref.setMenuHelpMode(helpMode);
            }
                break;
            case ShowMenuHelp: {
                MenuHelpMode helpMode = MySharedPref.getMenuHelpMode();
                if (helpMode == MenuHelpMode.Help) {
                    helpMode = MenuHelpMode.None;
                } else {
                    helpMode = MenuHelpMode.Help;
                }
                MySharedPref.setMenuHelpMode(helpMode);
            }
                break;
            case Setting:

                break;
            case SearchCard:
                PageViewManager.getInstance().stackPage(PageView.SearchCard);
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
     * カレントIconWindowを取得する
     * サブが開いていたらサブを、開いていなかったらメインを返す
     * @return
     */
    private UIconWindow getCurrentWindow() {
        if (mIconWinManager.getSubWindow().isShow()) {
            return mIconWinManager.getSubWindow();
        }
        return mIconWinManager.getMainWindow();
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
                UIconWindow subWindow = mIconWinManager.getSubWindow();
                subWindow.setIcons(TangoParentType.Book, icon.getTangoItem().getId());

                // SubWindowを画面外から移動させる
                mIconWinManager.showWindow(subWindow, true);
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

            IconCard iconCard = addCardIcon();
            if (iconCard == null) {
                return;
            }
            TangoCard card = (TangoCard)iconCard.getTangoItem();

            // 戻り値を取得
            card.setWordA(args.getString(EditCardDialogFragment.KEY_WORD_A, ""));
            card.setWordB(args.getString(EditCardDialogFragment.KEY_WORD_B, ""));
            card.setComment(args.getString(EditCardDialogFragment.KEY_COMMENT, ""));

            iconCard.updateTitle();

            // DB更新
            RealmManager.getCardDao().updateOne(card);
        } else {
            // 既存のアイコンを更新する

            TangoCard card = (TangoCard)editingIcon.getTangoItem();
            card.setWordA(args.getString(EditCardDialogFragment.KEY_WORD_A, ""));
            card.setWordB(args.getString(EditCardDialogFragment.KEY_WORD_B, ""));
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
            IconBook bookIcon = (IconBook) (iconManager.addNewIcon(
                    IconType.Book, TangoParentType.Home, 0, AddPos.Tail));
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

            IconBook bookIcon = (IconBook)editingIcon;
            TangoBook book = (TangoBook)bookIcon.getTangoItem();

            book.setName(args.getString(EditBookDialogFragment.KEY_NAME, ""));
            book.setComment(args.getString(EditCardDialogFragment.KEY_COMMENT, ""));
            int color = book.getColor();
            book.setColor(args.getInt(EditBookDialogFragment.KEY_COLOR, 0));

            // アイコンの画像を更新する
            if (color != book.getColor()) {
                bookIcon.setColor(book.getColor());
                bookIcon.updateIconImage();
            }

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
                    true,
                    mParentView.getWidth(), mParentView.getHeight(),
                    Color.rgb(200,100,100), Color.WHITE);
            mDialog.addToDrawManager();

            // 確認のダイアログを表示する
            mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_cleanup_trash));

            // ボタンを追加
            mDialog.addButton(CleanupDialogButtonOK, "OK", Color.BLACK,
                    UColor.LightGreen);
            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
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
