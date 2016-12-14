package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/11/30.
 *
 * ゴミ箱アイコンをクリックした際に表示されるダイアログ
 */

public class IconInfoDialogTrash extends IconInfoDialog {

    /**
     * Consts
     */
    private static final String TAG = "IconInfoDialogTrash";
    private static final int BG_COLOR = Color.LTGRAY;
    private static final int DLG_MARGIN = 100;
    private static final int TEXT_VIEW_H = 100;
    private static final int ICON_W = 120;
    private static final int ICON_MARGIN_H = 30;
    private static final int MARGIN_V = 40;
    private static final int MARGIN_H = 40;
    private static final int TEXT_SIZE = 50;
    private static final int TITLE_WIDTH = 250;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    private static final int MIN_WIDTH = 700;

    /**
     * Member Variables
     */
    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された
    private UTextView textNumber;
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();
    private UTextView textTitle, textCountTitle;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public IconInfoDialogTrash(View parentView,
                              IconInfoDialogCallbacks iconInfoDialogCallbacks,
                              UWindowCallbacks windowCallbacks,
                               UIcon icon,
                              float x, float y,
                              int color)
    {
        super( parentView, iconInfoDialogCallbacks, windowCallbacks, icon, x, y, color);
    }

    /**
     * createInstance
     */
    public static IconInfoDialogTrash createInstance(
            View parentView,
            IconInfoDialogCallbacks iconInfoDialogCallbacks,
            UWindowCallbacks windowCallbacks,
            UIcon icon,
            float x, float y)
    {
        IconInfoDialogTrash instance = new IconInfoDialogTrash( parentView,
                iconInfoDialogCallbacks, windowCallbacks,
                icon,
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

            // 閉じるボタンの再配置
            updateCloseIconPos();
        }

        // BG
        UDraw.drawRoundRectFill(canvas, paint, new RectF(getRect()), 20,
                bgColor, FRAME_WIDTH, FRAME_COLOR);

        textTitle.draw(canvas, paint, pos);
        textCountTitle.draw(canvas, paint, pos);
        textNumber.draw(canvas, paint, pos);

        // Buttons
        for (UButtonImage button : imageButtons) {
            button.draw(canvas, paint, pos);
        }
    }

    /**
     * レイアウト更新
     * @param canvas
     */
    protected void updateLayout(Canvas canvas) {

        int y = TOP_ITEM_Y;

        List<ActionIcons> icons = ActionIcons.getTrashIcons();

        int width = ICON_W * icons.size() +
                ICON_MARGIN_H * (icons.size() + 1);
        if (width < MIN_WIDTH) width = MIN_WIDTH;

        // 種別
        textTitle = UTextView.createInstance( mParentView.getContext().getString(R.string.trash),
                TEXT_SIZE, 0,
                UAlignment.CenterX, canvas.getWidth(), false, false,
                width / 2, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);
        y += TEXT_SIZE + 30;

        // Action buttons
        int x = (width - ICON_W * 2 - MARGIN_H) / 2;
        for (ActionIcons icon : icons) {
            UButtonImage imageButton = UButtonImage.createButton( this,
                    icon.ordinal(), 0,
                    x, y,
                    ICON_W, ICON_W, icon.getImageId(), -1);

            // アイコンの下に表示するテキストを設定
            imageButton.setTitle(icon.getTitle(mParentView.getContext()), 30, Color.BLACK);

            imageButtons.add(imageButton);
            ULog.showRect(imageButton.getRect());

            x += ICON_W + ICON_MARGIN_H;
        }
        y += ICON_W + MARGIN_V + 50;

        // Number of items in trash
        long count = RealmManager.getItemPosDao().countInParentType(
                TangoParentType.Trash, 0
        );

        textCountTitle = UTextView.createInstance( mParentView.getContext().getString(R.string
                        .item_count), TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, true,
                MARGIN_H, y, TITLE_WIDTH, TEXT_COLOR, Color.argb(1,0,0,0));
        textCountTitle.setMarginH(false);

        textNumber = UTextView.createInstance( "" + count, TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, true,
                MARGIN_H * 2 + textCountTitle.size.width, y, width - (MARGIN_H * 3 + textCountTitle
                        .size.width),
                TEXT_COLOR,
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
            case Open:
                mIconInfoCallbacks.IconInfoOpenIcon(mIcon);
                break;
            case CleanUp:
                mIconInfoCallbacks.IconInfoCleanup(mIcon);
                break;
        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
    }

}
