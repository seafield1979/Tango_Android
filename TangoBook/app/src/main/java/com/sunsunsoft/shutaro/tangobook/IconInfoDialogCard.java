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
 * カードアイコンをクリックした際に表示されるダイアログ
 * カードの情報(WordA,WordB)とアクションアイコン(ActionIcons)を表示する
 */
public class IconInfoDialogCard extends IconInfoDialog {


    /**
     * Consts
     */
    private static final String TAG = "IconInfoDialogCard";
    private static final int BG_COLOR = Color.LTGRAY;
    private static final int DLG_MARGIN = 100;
    private static final int TOP_ITEM_Y = 100;
    private static final int TEXT_VIEW_W = 300;
    private static final int TEXT_VIEW_H = 100;
    private static final int ICON_W = 120;
    private static final int ICON_MARGIN_H = 30;
    private static final int MARGIN_V = 40;
    private static final int MARGIN_H = 40;
    private static final int TEXT_SIZE = 50;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    /**
     * Member Variables
     */
    private View mParentView;
    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された
    private UTextView textWordA;
    private UTextView textWordB;
    private TangoCard mCard;
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();
    private boolean favoriteFlag;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public IconInfoDialogCard(View parentView,
                              IconInfoDialogCallbacks iconInfoDialogCallbacks,
                              UWindowCallbacks windowCallbacks,
                              UIcon icon,
                              float x, float y,
                              int color)
    {
        super( parentView, iconInfoDialogCallbacks, windowCallbacks, icon, x, y, color);
        mParentView = parentView;
        if (icon instanceof IconCard) {
            IconCard cardIcon = (IconCard)icon;
            mCard = (TangoCard)cardIcon.getTangoItem();
        }
    }

    /**
     * createInstance
     */
    public static IconInfoDialogCard createInstance(
            View parentView,
            IconInfoDialogCallbacks iconInfoDialogCallbacks,
            UWindowCallbacks windowCallbacks,
            UIcon icon,
            float x, float y)
    {
        IconInfoDialogCard instance = new IconInfoDialogCard( parentView,
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

        textWordA.draw(canvas, paint, pos);
        textWordB.draw(canvas, paint, pos);
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

        List<ActionIcons> icons = ActionIcons.getCardIcons();

        int width = ICON_W * icons.size() +
                ICON_MARGIN_H * (icons.size() + 1);

        // アクションボタン
        int x = ICON_MARGIN_H;
        for (ActionIcons icon : icons) {
            Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(),
                    icon.getImageId());

            UButtonImage imageButton = UButtonImage.createButton( this,
                            icon.ordinal(), 0,
                            x, y,
                            ICON_W, ICON_W, bmp, null);
            // アイコンの下に表示するテキストを設定
            imageButton.setTitle(icon.getTitle(), 30, Color.BLACK);

            imageButtons.add(imageButton);
            ULog.showRect(imageButton.getRect());

            x += ICON_W + ICON_MARGIN_H;
        }
        y += ICON_W + MARGIN_V + 50;

        // WordA
        textWordA = UTextView.createInstance( mCard.getWordA(), TEXT_SIZE, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, true,
                MARGIN_H, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);

        y += TEXT_VIEW_H + MARGIN_V;

        // WordB
        textWordB = UTextView.createInstance( mCard.getWordB(), TEXT_SIZE, 0,
                UDraw.UAlignment.None, TEXT_VIEW_W, false, true,
                MARGIN_H, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);

        y += TEXT_VIEW_H + MARGIN_V + 50;

        setSize(width, y);

        // 座標補正
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
    public boolean UButtonClick(int id) {
        if (super.UButtonClick(id)) {
            return true;
        }

        ULog.print(TAG, "UButtonCkick:" + id);
        switch(ActionIcons.toEnum(id)) {
            case Edit:
                mIconInfoCallbacks.IconInfoEditIcon(mIcon);
                break;
            case MoveToTrash:
                mIconInfoCallbacks.IconInfoThrowIcon(mIcon);
                break;
            case Copy:
                mIconInfoCallbacks.IconInfoCopyIcon(mIcon);
                break;
            case Favorite:

                break;
        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
    }

}