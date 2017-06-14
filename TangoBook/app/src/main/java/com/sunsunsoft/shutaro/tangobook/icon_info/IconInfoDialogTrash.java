package com.sunsunsoft.shutaro.tangobook.icon_info;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoItemType;
import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.UButtonImage;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.icon.UIcon;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.UWindowCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/11/30.
 *
 * ゴミ箱アイコンをクリックした際に表示されるダイアログ
 */

public class IconInfoDialogTrash extends IconInfoDialog {
    /**
     * Enums
     */
    // アクションアイコンボタンのIndex
    enum ButtonIndex {
        Open,
        EmptyTrash
    }

    /**
     * Consts
     */
    private static final String TAG = "IconInfoDialogTrash";
    private static final int BG_COLOR = Color.LTGRAY;
    private static final int DLG_MARGIN = 100;
    private static final int TEXT_VIEW_H = 100;
    private static final int ICON_W = 120;
    private static final int ICON_MARGIN_H = 30;
    private static final int TEXT_SIZE = 50;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    private static final int MIN_WIDTH = 700;

    /**
     * Member Variables
     */
    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();
    private UTextView textTitle;
    private UTextView titleBoxNum, textBoxNum;
    private UTextView titleCardNum, textCardNum;

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
    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
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
        titleBoxNum.draw(canvas, paint, pos);
        textBoxNum.draw(canvas, paint, pos);

        titleCardNum.draw(canvas, paint, pos);
        textCardNum.draw(canvas, paint, pos);

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
        int x = MARGIN_H;
        int y = TOP_ITEM_Y;

        List<ActionIcons> icons = ActionIcons.getTrashIcons();

        int boxCount = RealmManager.getItemPosDao().countInParentType(
                TangoParentType.Trash, 0, TangoItemType.Book
        );
        int cardCount = RealmManager.getItemPosDao().countInParentType(
                TangoParentType.Trash, 0, TangoItemType.Card
        );

        int width = ICON_W * icons.size() +
                ICON_MARGIN_H * (icons.size() + 1);
        if (width < MIN_WIDTH) width = MIN_WIDTH;

        // 種別(ゴミ箱)
        textTitle = UTextView.createInstance( mParentView.getContext().getString(R.string.trash),
                TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, false,
                x, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);
        y += TEXT_SIZE + MARGIN_V;

        // Book count
        titleBoxNum = UTextView.createInstance( UResourceManager
                .getStringById(R.string.book_count), TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, true,
                x, y, width - MARGIN_H * 2,
                TEXT_COLOR, 0);
        x += titleBoxNum.getWidth() + MARGIN_H;

        textBoxNum = UTextView.createInstance( "" + boxCount,
                TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, true,
                x, y, 300,
                TEXT_COLOR, Color.WHITE);

        y += TEXT_VIEW_H + MARGIN_V_S;

        // Card count
        x = MARGIN_H;
        titleCardNum = UTextView.createInstance( UResourceManager
                        .getStringById(R.string.card_count), TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, true,
                x, y, width - MARGIN_H * 2,
                TEXT_COLOR, 0);
        x += titleCardNum.getWidth() + MARGIN_H;

        textCardNum = UTextView.createInstance( "" + cardCount,
                TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, true,
                x, y, 300,
                TEXT_COLOR, Color.WHITE);

        y += TEXT_VIEW_H + MARGIN_V;


        // Action buttons
        x = (width - ICON_W * 2 - MARGIN_H) / 2;
        for (ActionIcons icon : icons) {
            Bitmap image = UResourceManager.getBitmapWithColor(icon.getImageId(), UColor
                    .DarkOrange);
            UButtonImage imageButton = UButtonImage.createButton( this,
                    icon.ordinal(), 0,
                    x, y,
                    ICON_W, ICON_W, image, null);

            // アイコンの下に表示するテキストを設定
            imageButton.setTitle(icon.getTitle(mParentView.getContext()), 30, Color.BLACK);

            imageButtons.add(imageButton);
            ULog.showRect(imageButton.getRect());

            x += ICON_W + ICON_MARGIN_H;
        }
        // ゴミ箱を空にするアイコンはアイテム数が０ならDisable
        if (cardCount + boxCount == 0) {
            imageButtons.get(ButtonIndex.EmptyTrash.ordinal()).setEnabled(false);
        }

        y += ICON_W + MARGIN_V + 50;

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

    public DoActionRet doAction() {
        DoActionRet ret = DoActionRet.None;
        for (UButtonImage button : imageButtons) {
            DoActionRet _ret = button.doAction();
            switch(_ret) {
                case Done:
                    return DoActionRet.Done;
                case Redraw:
                    ret = DoActionRet.Redraw;
                    break;
            }
        }
        return ret;
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
            case OpenTrash:
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
