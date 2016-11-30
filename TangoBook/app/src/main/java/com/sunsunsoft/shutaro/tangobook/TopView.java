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
        UIconCallbacks, ViewTouchCallbacks, UWindowCallbacks, UButtonCallbacks,
        EditCardDialogCallbacks, EditBookDialogCallbacks, IconInfoDialogCallbacks,
        UDialogCallbacks
{

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
    private UIconWindows mIconWindows;

    // MessageWindow
    private ULogWindow mLogWin;

    // Dialog
    private DebugDialogs debugDialogs;
    private UDialogWindow mDialog;

    // メニューバー
    private UMenuBar mMenuBar;

    // サイズ更新用
    private boolean isFirst = true;

    // クリック判定の仕組み
    private ViewTouch vt = new ViewTouch(this);

    private IconInfoDialog mIconInfoDlg;

    private Context mContext;
    private NestedScrollingParent mNestedScrollingParent;
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
                    0, 0, width, height);
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
            case Debug6:
                UDrawManager.getInstance().showAllList(true, false);
                break;
        }
        ULog.print(TAG, "menu item clicked " + id);
    }

    public void menuItemCallback2() {
        ULog.print(TAG, "menu item moved");
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

    /**
     * UIconCallbacks
     */
    public void clickIcon(UIcon icon) {
        ULog.print(TAG, "clickIcon");
        if (mIconInfoDlg == null) {
            PointF winPos = icon.parentWindow.getPos();
            float x = winPos.x + icon.getX();
            float y = winPos.y + icon.getY();
            switch (icon.type) {
                case Card:
                    mIconInfoDlg = IconInfoDialogCard.createInstance(this, this, this, icon,
                            x, y);
                    break;
                case Book:
                    mIconInfoDlg = IconInfoDialogBook.createInstance(this, this, this, icon,
                            x, y);
                    break;
                case Trash:
                    mIconInfoDlg = IconInfoDialogTrash.createInstance(this, this, this, icon,
                            x, y);
                    break;
            }
        } else {
            mIconInfoDlg.closeWindow();
            mIconInfoDlg = null;
        }
        invalidate();
    }

    public void longClickIcon(UIcon icon) {
        ULog.print(TAG, "longClickIcon");
    }

    public void dropToIcon(UIcon icon) {
        ULog.print(TAG, "dropToIcon");
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
                UIconWindow window = mIconWindows.getSubWindow();
                window.setIcons(TangoParentType.Book, icon.getTangoItem().getId());

                // SubWindowを画面外から移動させる
                mIconWindows.showWindow(window, true);
                invalidate();
            }
                break;
            case Trash:
            {
                UIconWindow window = mIconWindows.getSubWindow();
                window.setIcons(TangoParentType.Trash, 0);

                // SubWindowを画面外から移動させる
                mIconWindows.showWindow(window, true);
                invalidate();
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
            case CleanupDialogButtonOK:
                break;
            case UDialogWindow.CloseDialogId:
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

            UIconManager iconManager = mIconWindows.getMainWindow().getIconManager();
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
            mIconWindows.getMainWindow().sortIcons(false);
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

        invalidate();
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
            UIconManager iconManager = mIconWindows.getMainWindow().getIconManager();
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
        mIconWindows.getMainWindow().sortIcons(false);
        invalidate();
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

    public void IconInfoCopyIcon(UIcon icon) {
        this.copyIcon(icon);
        mIconInfoDlg.closeWindow();
        mIconInfoDlg = null;
    }

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
                    getWidth(), getHeight(),
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
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {

    }
}
