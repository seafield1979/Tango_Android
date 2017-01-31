package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Color;
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
        UDialogCallbacks, UIconWindowSubCallbacks
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

    // ゴミ箱に捨てるアイコン
    private UIcon mThrowIcon;

    // CSV出力アイコン
    private IconBook mExportIcon;


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

    /**
     * UPageView
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
        UIconWindowSub subWindow = UIconWindowSub.createInstance(this, this, this, false, winDir, size2.width, size2.height, Color.LTGRAY);
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
     * アクションIDを処理する
     * サブクラスでオーバーライドして使用する
     */
    public void setActionId(int id) {
        switch(id) {
            case R.id.action_move_to_trash: {
                if (mDialog != null) {
                    return;
                }
                // ゴミ箱に移動するかの確認ダイアログを表示する
                mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this, this,
                        UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                        true, mParentView.getWidth(), mParentView.getHeight(),
                        Color.BLACK, Color.LTGRAY);
                mDialog.addToDrawManager();
                mDialog.setTitle(mContext.getString(R.string.confirm_moveto_trash));
                mDialog.addButton(ButtonIdMoveIconsToTrash, "OK", Color.BLACK, Color.WHITE);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
                mParentView.invalidate();
            }
                break;
            case R.id.action_sort_word_asc: {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .TitleAsc);
                window.sortIcons(true);
                mParentView.invalidate();
            }
                break;
            case R.id.action_sort_word_desc: {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .TitleDesc);
                window.sortIcons(true);
                mParentView.invalidate();
            }
                break;
            case R.id.action_sort_time_asc: {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .CreateDateAsc);
                window.sortIcons(true);
                mParentView.invalidate();
            }
                break;
            case R.id.action_sort_time_desc:
            {
                UIconWindow window = getCurrentWindow();
                window.mIconManager.sortWithMode(UIconManager.SortMode
                        .CreateDateDesc);
                window.sortIcons(true);
                mParentView.invalidate();
            }
                break;
            case R.id.action_card_name_a:
                // カードアイコンの名前を英語で表示
                MySharedPref.writeBoolean(MySharedPref.EditCardNameKey, false);
                mIconWinManager.resetCardTitle();
                mParentView.invalidate();
                break;
            case R.id.action_card_name_b:
                // カードアイコンの名前を日本語で表示
                MySharedPref.writeBoolean(MySharedPref.EditCardNameKey, true);
                mIconWinManager.resetCardTitle();
                mParentView.invalidate();
                break;
            case R.id.action_search_card:
                PageViewManager.getInstance().stackPage(PageView.SearchCard);
                break;
            case R.id.action_settings:
                PageViewManager.getInstance().startOptionPage(PageViewOptions.Mode.Edit);
                mParentView.invalidate();
                break;
        }
    }

    /**
     * Androidのバックキーが押された時の処理
     * @return
     */
    public boolean onBackKeyDown() {
        // 各種ダイアログ
        if (mDialog != null) {
            mDialog.startClosing();
            return true;
        }
        // アイコンダイアログが開いていたら閉じる
        if (mIconInfoDlg != null) {
            mIconInfoDlg.closeWindow();
            mIconInfoDlg = null;
            return true;
        }

        // メニューが開いていたら閉じる
        if (mMenuBar.onBackKeyDown()) {
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

    // Csvファイルから単語帳を追加する
    private void addCsvBook() {
        PageViewManager.getInstance().stackPage(PageView.CsvBook);
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
            List<TangoCard> cards = srcBook.getItems();
            if (cards != null) {
                for (TangoCard card : cards) {
                    // DBに位置情報を追加
                    // Card
                    TangoCard newCard = RealmManager.getCardDao().copyOne(card);
                    // ItemPos
                    RealmManager.getItemPosDao().addOne(newCard, TangoParentType
                            .Book, dstBook.getTangoItem().getId());

                }
            }
        }

        icon.parentWindow.sortIcons(true);
    }

    // card
    private IconCard addCardIcon() {

        UIconManager iconManager;
        UIconWindow window;
        IconCard cardIcon;

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
     * アイコンをゴミ箱に移動する
     * @param icon
     */
    private void moveIconToTrash(UIcon icon) {
        mIconWinManager.getMainWindow().moveIconIntoTrash(icon);
        if (mIconInfoDlg != null) {
            mIconInfoDlg.closeWindow();
            mIconInfoDlg = null;
        }
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
            case AddCsvBook:
                addCsvBook();
                break;
//            case ShowMenuName:
//            {
//                MenuHelpMode helpMode = MySharedPref.getMenuHelpMode();
//                if (helpMode == MenuHelpMode.Name) {
//                    helpMode = MenuHelpMode.None;
//                } else {
//                    helpMode = MenuHelpMode.Name;
//                }
//                MySharedPref.setMenuHelpMode(helpMode);
//            }
//                break;
//            case ShowMenuHelp: {
//                MenuHelpMode helpMode = MySharedPref.getMenuHelpMode();
//                if (helpMode == MenuHelpMode.Help) {
//                    helpMode = MenuHelpMode.None;
//                } else {
//                    helpMode = MenuHelpMode.Help;
//                }
//                MySharedPref.setMenuHelpMode(helpMode);
//            }
//                break;
//            case Setting:
//
//                break;
//            case SearchCard:
//                PageViewManager.getInstance().stackPage(PageView.SearchCard);
//                break;
        }
        ULog.print(TAG, "menu item clicked " + id);
    }

    public void menuItemCallback2() {
        ULog.print(TAG, "menu item moved");
    }

    /**
     * UIconCallbacks
     */
    /**
     * IconWindow上のアイコンがクリックされた
     * アイコンの種類に合わせたダイアログを表示する
     * @param icon
     */
    public void iconClicked(UIcon icon) {
        ULog.print(TAG, "iconClicked");
        if (mIconInfoDlg != null) {
            if (icon == mIconInfoDlg.mIcon) {
                if (icon.type == IconType.Card) {
                    // カードなら編集
                    IconInfoEditIcon(icon);
                    return;
                }
            }
            else {
                mIconInfoDlg.closeWindow();
                mIconInfoDlg = null;
            }
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
                    // newフラグをクリア
                    icon.setNewFlag(false);
                    break;
                case Book:
                    IconInfoOpenIcon(icon);
                    // newフラグをクリア
                    icon.setNewFlag(false);
                    break;
                case Trash:
                    IconInfoOpenIcon(icon);
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
                UIconWindowSub subWindow = mIconWinManager.getSubWindow();
                subWindow.setIcons(TangoParentType.Book, icon.getTangoItem().getId());
                subWindow.setParentIcon(icon);

                // SubWindowを画面外から移動させる
                mIconWinManager.showWindow(subWindow, true);
                mParentView.invalidate();
            }
            break;
            case Trash:
            {
                UIconWindow window = mIconWinManager.getSubWindow();
                window.setIcons(TangoParentType.Trash, 0);
                mIconWinManager.getSubWindow().setParentIcon(icon);

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
                mIconWinManager.getSubWindow().getIcons().clear();

                mDialog.closeDialog();
                mIconWinManager.getSubWindow().sortIcons(false);
                return true;
            case TrashDialogButtonOK:
                // 単語帳をゴミ箱に捨てる
                moveIconToTrash(mThrowIcon);
                UIconWindowSub subWindow = mIconWinManager.getSubWindow();
                if (subWindow.isShow()) {
                    if (subWindow.windowCallbacks != null) {
                        subWindow.windowCallbacks.windowClose(subWindow);
                    }
                }
                mDialog.closeDialog();
                return true;

            case ButtonIdMoveIconsToTrash:
                // チェックしたアイコンをゴミ箱に移動する
                UIcon trashIcon = mIconWinManager.getMainWindow().getIconManager().getTrashIcon();
                for (UIconWindow window : mIconWinManager.getWindows()) {
                    window.moveIconsIntoBox(window.getIconManager().getCheckedIcons(),
                        trashIcon);
                }

                if(mDialog != null) {
                    mDialog.startClosing();
                }
                return true;
            case ExportDialogButtonOK:
                // CSVファイルに出力する
            {
                List<TangoCard> cards = mExportIcon.getItems();
                String path = PresetBookManager.getInstance()
                        .exportToCsvFile((TangoBook)mExportIcon.getTangoItem(), cards);

                String message;
                if (path == null) {
                    // 失敗
                    message = UResourceManager.getStringById(R.string.failed_backup);
                } else {
                    // 成功
                    message = path + "\n" + UResourceManager.getStringById(R.string.finish_export);
                }

                if (mDialog != null) {
                    mDialog.closeDialog();
                }
                mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this, this,
                        UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                        true, mParentView.getWidth(), mParentView.getHeight(),
                        Color.BLACK, Color.LTGRAY);
                mDialog.addToDrawManager();
                mDialog.setTitle(message);
                mDialog.addButton(ExportFinishedDialogButtonOk, "OK", Color.BLACK, Color.WHITE);
            }
                break;
            case ExportFinishedDialogButtonOk:
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
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
            // 新規作成

            IconCard iconCard = addCardIcon();
            if (iconCard == null) {
                return;
            }
            TangoCard card = (TangoCard)iconCard.getTangoItem();

            // 戻り値を取得
            card.setWordA(args.getString(EditCardDialogFragment.KEY_WORD_A, ""));
            card.setWordB(args.getString(EditCardDialogFragment.KEY_WORD_B, ""));
            card.setComment(args.getString(EditCardDialogFragment.KEY_COMMENT, ""));
            card.setColor(args.getInt(EditBookDialogFragment.KEY_COLOR, 0));

            iconCard.updateTitle();
            iconCard.setColor(card.getColor());
            iconCard.updateIconImage();
            // DB更新
            RealmManager.getCardDao().updateOne(card);
        } else {
            // 更新
            TangoCard card = (TangoCard)editingIcon.getTangoItem();
            card.setWordA(args.getString(EditCardDialogFragment.KEY_WORD_A, ""));
            card.setWordB(args.getString(EditCardDialogFragment.KEY_WORD_B, ""));
            card.setComment(args.getString(EditCardDialogFragment.KEY_COMMENT, ""));
            int color = card.getColor();
            card.setColor(args.getInt(EditCardDialogFragment.KEY_COLOR, 0));

            // アイコンの画像を更新する
            IconCard cardIcon = (IconCard)editingIcon;
            if (color != card.getColor()) {
                cardIcon.setColor(card.getColor());
                cardIcon.updateIconImage();
            }

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

            bookIcon.setColor(book.getColor());
            bookIcon.updateIconImage();

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
        if (icon != null) {
            moveIconToTrash(icon);
        }
    }

    /**
     * アイコンを開く
     */
    public void IconInfoOpenIcon(UIcon icon) {
        if (mIconWinManager.getSubWindow().isShow() == false ||
                icon != mIconWinManager.getSubWindow().getParentIcon())
        {
            openIcon(icon);
        }

        if (mIconInfoDlg != null) {
            mIconInfoDlg.closeWindow();
            mIconInfoDlg = null;
        }
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
    public static final int TrashDialogButtonOK = 102;
    public static final int ExportDialogButtonOK = 103;
    public static final int ExportFinishedDialogButtonOk = 104;
    public static final int ButtonIdMoveIconsToTrash = 105;

    public void IconInfoCleanup(UIcon icon) {
        if (icon == null || icon.getType() == IconType.Trash) {
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
        if (dialog == mDialog) {
            mDialog = null;
        }
    }

    /**
     * UIconWindowSubCallbacks
     */
    public void IconWindowSubAction(UIconWindowSub.ActionId id, UIcon icon) {
        switch (id) {
            case Close:
                mIconWinManager.hideWindow(mIconWinManager.getSubWindow(), true);
                break;
            case Edit:
                editingIcon = icon;
                if (icon instanceof IconBook) {
                    editBookDialog((IconBook)editingIcon);
                }
                break;
            case Copy:
                copyIcon(icon);
                break;
            case Delete: {
                // 確認のダイアログを表示する
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                // Daoデバッグ用のダイアログを表示
                mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this, this,
                        UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                        true,
                        mParentView.getWidth(), mParentView.getHeight(),
                        Color.rgb(200,100,100), Color.WHITE);
                mDialog.addToDrawManager();

                // 確認のダイアログを表示する
                mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_moveto_trash));

                // ボタンを追加
                mDialog.addButton(TrashDialogButtonOK, "OK", Color.BLACK,
                        UColor.LightGreen);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

                // 捨てるアイコンを保持
                mThrowIcon = icon;
            }
                break;
            case Export:
            {
                // 確認のダイアログを表示する
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }

                mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this, this,
                        UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                        true,
                        mParentView.getWidth(), mParentView.getHeight(),
                        Color.rgb(200,100,100), Color.WHITE);
                mDialog.addToDrawManager();

                // 確認のダイアログを表示する
                mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_export_csv));

                // ボタンを追加
                mDialog.addButton(ExportDialogButtonOK, "OK", Color.BLACK,
                        UColor.LightGreen);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

                // アイコンを保持
                if (icon.getType() == IconType.Book) {
                    mExportIcon = (IconBook)icon;
                }
            }
                break;
            case Cleanup:
                // ゴミ箱を空にする
                IconInfoCleanup(null);
                break;
        }
    }
}
