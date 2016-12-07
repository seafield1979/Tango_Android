package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/12/06.
 *
 * 学習する単語帳をクリックした時に表示されるダイアログ
 */

public class IconInfoDialogBookStudy extends IconInfoDialog {

    /**
     * Consts
     */
    private static final String TAG = "IconInfoDialogBook";
    private static final int BG_COLOR = Color.LTGRAY;
    private static final int DLG_MARGIN = 100;
    private static final int TOP_ITEM_Y = 50;
    private static final int TEXT_VIEW_H = 100;
    private static final int ICON_W = 120;
    private static final int ICON_MARGIN_H = 30;
    private static final int MARGIN_V = 40;
    private static final int MARGIN_H = 40;
    private static final int TEXT_SIZE = 50;
    private static final int TITLE_WIDTH = 150;
    private static final int TITLE_WIDTH2 = 250;
    private static final int MIN_WIDTH = 700;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    /**
     * Member Variables
     */
    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された
    private UTextView textTitle, textNameTitle, textCountTitle;
    private UTextView textName;
    private UTextView textCount;
    private TangoBook mBook;
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public IconInfoDialogBookStudy(View parentView,
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
    }

    /**
     * createInstance
     */
    public static IconInfoDialogBookStudy createInstance(
            View parentView,
            IconInfoDialogCallbacks iconInfoDialogCallbacks,
            UWindowCallbacks windowCallbacks,
            UIcon icon,
            float x, float y)
    {
        IconInfoDialogBookStudy instance = new IconInfoDialogBookStudy( parentView,
                iconInfoDialogCallbacks, windowCallbacks, icon,
                x, y, BG_COLOR);

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
    public void drawContent(Canvas canvas, Paint paint) {
        if (isUpdate) {
            isUpdate = false;
            updateLayout(canvas);
        }

        // BG
        UDraw.drawRoundRectFill(canvas, paint, new RectF(getRect()), 20,
                bgColor, FRAME_WIDTH, FRAME_COLOR);

        // Buttons
        for (UButtonImage button : imageButtons) {
            button.draw(canvas, paint, pos);
        }

        textTitle.draw(canvas, paint, pos);
        textNameTitle.draw(canvas, paint, pos);
        textCountTitle.draw(canvas, paint, pos);
        textName.draw(canvas, paint, pos);
        textCount.draw(canvas, paint, pos);
    }

    /**
     * レイアウト更新
     * @param canvas
     */
    protected void updateLayout(Canvas canvas) {

        int y = TOP_ITEM_Y;

        List<ActionIcons> icons = ActionIcons.getBookStudyIcons();

        int width = ICON_W * icons.size() +
                ICON_MARGIN_H * (icons.size() + 1);
        if (width < MIN_WIDTH) width = MIN_WIDTH;

        // 種別
        textTitle = UTextView.createInstance( mContext.getString(R.string.book),
                TEXT_SIZE, 0,
                UDraw.UAlignment.CenterX, canvas.getWidth(), false, false,
                width / 2, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);
        y += TEXT_SIZE + 30;

        // Action buttons
        int x = ICON_MARGIN_H;
        for (ActionIcons icon : icons) {
            Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(),
                    icon.getImageId());

            UButtonImage imageButton = UButtonImage.createButton( this,
                    icon.ordinal(), 0,
                    x, y,
                    ICON_W, ICON_W, bmp, null);
            // アイコンの下に表示するテキストを設定
            imageButton.setTitle(icon.getTitle(mContext), 30, Color.BLACK);

            imageButtons.add(imageButton);
            ULog.showRect(imageButton.getRect());

            x += ICON_W + ICON_MARGIN_H;
        }
        y += ICON_W + MARGIN_V + 50;

        // Name
        textNameTitle = UTextView.createInstance( mContext.getString(R.string.name), TEXT_SIZE, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, true,
                MARGIN_H, y, TITLE_WIDTH, TEXT_COLOR, Color.argb(1,0,0,0));
        textNameTitle.setMarginH(false);

        textName = UTextView.createInstance( mBook.getName(), TEXT_SIZE, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, true,
                MARGIN_H + TITLE_WIDTH, y, width - (MARGIN_H * 2 + TITLE_WIDTH), TEXT_COLOR,
                TEXT_BG_COLOR);

        y += TEXT_VIEW_H + MARGIN_V;

        // Card count
        long count = RealmManager.getItemPosDao().countInParentType(
                TangoParentType.Book, mIcon.getTangoItem().getId()
        );

        // title
        textCountTitle = UTextView.createInstance( mContext.getString(R.string.card_count),
                TEXT_SIZE, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, true,
                MARGIN_H, y, TITLE_WIDTH2, TEXT_COLOR, Color.argb(1,0,0,0));
        textCountTitle.setMarginH(false);

        // body
        textCount = UTextView.createInstance( "" + count, TEXT_SIZE, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, true,
                MARGIN_H + TITLE_WIDTH2, y, width - (MARGIN_H * 2 + TITLE_WIDTH2), TEXT_COLOR,
                TEXT_BG_COLOR);

        y += TEXT_VIEW_H + MARGIN_V;


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

    public boolean touchEvent(ViewTouch vt) {
        PointF offset = pos;

        if (super.touchEvent(vt)) {
            return true;
        }

        for (UButtonImage button : imageButtons) {
            if (button.touchEvent(vt, offset)) {
                return true;
            }
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
            case Study:
                mIconInfoCallbacks.IconInfoStudy(mIcon);
                break;
            case Open:
                mIconInfoCallbacks.IconInfoOpenIcon(mIcon);
                break;
        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
    }

}
