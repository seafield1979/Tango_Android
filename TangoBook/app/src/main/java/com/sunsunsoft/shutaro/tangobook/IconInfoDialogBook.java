package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/11/30.
 *
 * Bookアイコンをクリックした際に表示されるダイアログ
 * Bookの情報(Name)とアクションアイコン(ActionIcons)を表示する
 */
public class IconInfoDialogBook extends IconInfoDialog {

    /**
     * Enums
     */
    enum Items {
        Name,
        Comment,
        Count,
        Date
    }

    /**
     * Consts
     */
    private static final String TAG = "IconInfoDialogBook";
    private static final int BG_COLOR = Color.LTGRAY;
    private static final int DLG_MARGIN = 100;
    private static final int ICON_W = 120;
    private static final int ICON_MARGIN_H = 30;
    private static final int TEXT_SIZE = 50;
    private static final int TEXT_SIZE_S = 40;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    /**
     * Member Variables
     */
    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された
    private UTextView textTitle;
    private IconInfoItem[] mItems = new IconInfoItem[Items.values().length];
    private TangoBook mBook;
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();



    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public IconInfoDialogBook(View parentView,
                              IconInfoDialogCallbacks iconInfoDialogCallbacks,
                              UWindowCallbacks windowCallbacks,
                              UIcon icon,
                              float x, float y,
                              int color)
    {
        super( parentView, iconInfoDialogCallbacks, windowCallbacks, icon, x, y, color);
        if (icon instanceof IconBook) {
            IconBook bookIcon = (IconBook)icon;
            mBook = (TangoBook)bookIcon.getTangoItem();
        }

        if (color == 0) color = Color.LTGRAY;

        bgColor = UColor.setBrightness(color, 220);
        frameColor = UColor.setBrightness(color, 140);
    }

    /**
     * createInstance
     */
    public static IconInfoDialogBook createInstance(
            View parentView,
            IconInfoDialogCallbacks iconInfoDialogCallbacks,
            UWindowCallbacks windowCallbacks,
            UIcon icon,
            float x, float y)
    {
        IconInfoDialogBook instance = new IconInfoDialogBook( parentView,
                iconInfoDialogCallbacks, windowCallbacks, icon,
                x, y, ((TangoBook)icon.getTangoItem()).getColor());

        // 初期化処理
        instance.addCloseIcon(CloseIconPos.RightTop);
        UDrawManager.getInstance().addDrawable(instance);

        return instance;
    }

    /**
     * Methods
     */

    /**
     * Windowのコンテンツ部分を描画する
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        if (isUpdate) {
            isUpdate = false;
            updateLayout(canvas);

            // 閉じるボタンの再配置
            updateCloseIconPos();
        }

        // BG
        UDraw.drawRoundRectFill(canvas, paint, new RectF(getRect()), 20,
                bgColor, FRAME_WIDTH, frameColor);

        // Buttons
        for (UButtonImage button : imageButtons) {
            button.draw(canvas, paint, pos);
        }

        textTitle.draw(canvas, paint, pos);
        for (IconInfoItem item : mItems) {
            item.title.draw(canvas, paint, pos);
            item.body.draw(canvas, paint, pos);
        }
    }

    /**
     * レイアウト更新
     * @param canvas
     */
    protected void updateLayout(Canvas canvas) {
        int y = TOP_ITEM_Y;

        List<ActionIcons> icons = getBookIcons();

        int width = ICON_W * icons.size() +
                ICON_MARGIN_H * (icons.size() + 1);
        // 単語帳
        textTitle = UTextView.createInstance( mContext.getString(R.string.book),
                TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, false,
                MARGIN_H, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);
        y += TEXT_SIZE + 30;

        String titleStr = null;
        String bodyStr = null;
        int bgColor = Color.WHITE;
        for (Items item : Items.values()) {
            switch(item) {
                case Name:  // Book名
                    titleStr = mContext.getString(R.string.name);
                    bodyStr = mBook.getName();
                    break;
                case Comment:
                    titleStr = mContext.getString(R.string.comment);
                    bodyStr = mBook.getComment();
                    break;
                case Count:  // カード枚数
                    int bookId = mIcon.getTangoItem().getId();
                    long count = RealmManager.getItemPosDao().countInParentType(
                            TangoParentType.Book, bookId );
                    int ngCount = RealmManager.getItemPosDao().countCardInBook(bookId,
                            TangoItemPosDao.BookCountType.NG);

                    titleStr = mContext.getString(R.string.card_count);

                    String allCount = UResourceManager.getStringById(R.string.all_count);
                    String countUnit = UResourceManager.getStringById(R.string.card_count_unit);
                    bodyStr = allCount + " : " +
                            count + countUnit + "   " +
                            UResourceManager.getStringById(R.string.count_not_learned) +
                            " : " + ngCount + countUnit;
                    break;
                case Date:   // 最終学習日時
                    Date date = RealmManager.getBookHistoryDao().selectMaxDateByBook(mBook.getId());
                    String dateStr = (date == null) ? " --- " :
                            UUtil.convDateFormat(date, ConvDateMode.DateTime);

                    titleStr = mContext.getString(R.string.studied_date);
                    bodyStr = dateStr;
                    break;
            }
            mItems[item.ordinal()] = new IconInfoItem();
            // title
            mItems[item.ordinal()].title = UTextView.createInstance( titleStr ,
                    TEXT_SIZE_S, 0,
                    UAlignment.None, canvas.getWidth(), false, false,
                    MARGIN_H, y, size.width - MARGIN_H, TEXT_COLOR, 0);

            y += mItems[item.ordinal()].title.getHeight() + 10;

            // body
            mItems[item.ordinal()].body = UTextView.createInstance( bodyStr,
                    TEXT_SIZE_S, 0,
                    UAlignment.None, canvas.getWidth(), true, true,
                    MARGIN_H, y, size.width - MARGIN_H, TEXT_COLOR, bgColor);
            y += mItems[item.ordinal()].body.getHeight() + MARGIN_V_S;

            // 幅は最大サイズに合わせる
            int _width = mItems[item.ordinal()].body.getWidth() + MARGIN_H * 2;
            if (_width > width) {
                width = _width;
            }
        }
        y += MARGIN_V;

        // タイトルのwidthを揃える
        for (IconInfoItem item : mItems) {
            item.title.setWidth(width - MARGIN_H * 2);
        }

        // Action buttons
        int x = ICON_MARGIN_H;
        for (ActionIcons icon : icons) {
            UButtonImage imageButton = UButtonImage.createButton( this,
                    icon.ordinal(), 0,
                    x, y,
                    ICON_W, ICON_W, icon.getImageId(), -1);
            // アイコンの下に表示するテキストを設定
            imageButton.setTitle(icon.getTitle(mContext), 30, Color.BLACK);

            imageButtons.add(imageButton);
            ULog.showRect(imageButton.getRect());

            x += ICON_W + ICON_MARGIN_H;
        }
        y += ICON_W + MARGIN_V + 30;

        setSize(width, y);

        // Correct position
        if ( pos.x + size.width > mParentView.getWidth() - DLG_MARGIN) {
            pos.x = mParentView.getWidth() - size.width - DLG_MARGIN;
        }
        if (pos.y + size.height > mParentView.getHeight() - DLG_MARGIN) {
            pos.y = mParentView.getHeight() - size.height - DLG_MARGIN;
        }
        updateRect();
    }

    // Book
    // アイコンのリストを取得
    public static List<ActionIcons> getBookIcons() {
        LinkedList<ActionIcons> list = new LinkedList<>();
        list.add(ActionIcons.Open);
        list.add(ActionIcons.Edit);
        list.add(ActionIcons.Copy);
        list.add(ActionIcons.MoveToTrash);
        return list;
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        offset = pos;

        if (super.touchEvent(vt, offset)) {
            return true;
        }

        for (UButtonImage button : imageButtons) {
            if (button.touchEvent(vt, offset)) {
                return true;
            }
        }

        if (super.touchEvent2(vt, null)) {
            return true;
        }

        return false;
    }

    public boolean doAction() {
        return false;
    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }

        ULog.print(TAG, "UButtonCkick:" + id);
        switch(ActionIcons.toEnum(id)) {
            case Open:
                mIconInfoCallbacks.IconInfoOpenIcon(mIcon);
                break;
            case Edit:
                mIconInfoCallbacks.IconInfoEditIcon(mIcon);
                break;
            case MoveToTrash:
                mIconInfoCallbacks.IconInfoThrowIcon(mIcon);
                break;
            case Copy:
                mIconInfoCallbacks.IconInfoCopyIcon(mIcon);
                break;
        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
    }

}
