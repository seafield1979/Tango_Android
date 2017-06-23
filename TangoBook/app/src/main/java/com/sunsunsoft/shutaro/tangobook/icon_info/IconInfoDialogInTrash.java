package com.sunsunsoft.shutaro.tangobook.icon_info;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.Size;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.icon.IconType;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonImage;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.icon.UIcon;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindowCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/12/01.
 *
 * ゴミ箱の中のアイコンをクリックした際に表示されるダイアログ
 */

public class IconInfoDialogInTrash extends IconInfoDialog {
    /**
     * Enums
     */


    /**
     * Consts
     */
    private static final String TAG = "IconInfoDialogBook";
    private static final int BG_COLOR = Color.LTGRAY;
    private static final int DLG_MARGIN = 35;
    private static final int TOP_ITEM_Y = 35;
    private static final int TEXT_VIEW_H = 35;
    private static final int ICON_W = 40;
    private static final int ICON_MARGIN_H = 10;
    private static final int MARGIN_V = 13;
    private static final int MARGIN_H = 13;
    private static final int TEXT_SIZE = 17;
    private static final int ICON_TEXT_SIZE = 10;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    /**
     * Member Variables
     */
    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された
    private UTextView textTitle;
    private UTextView textWord;
    private UTextView textCount;
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public IconInfoDialogInTrash(View parentView,
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
    public static IconInfoDialogInTrash createInstance(
            View parentView,
            IconInfoDialogCallbacks iconInfoDialogCallbacks,
            UWindowCallbacks windowCallbacks,
            UIcon icon,
            float x, float y)
    {
        IconInfoDialogInTrash instance = new IconInfoDialogInTrash( parentView,
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
    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        if (isUpdate) {
            isUpdate = false;
            updateLayout(canvas);

            // 閉じるボタンの再配置
            updateCloseIconPos();
        }

        // BG
        UDraw.drawRoundRectFill(canvas, paint, new RectF(getRect()), UDpi.toPixel(7),
                bgColor, UDpi.toPixel(FRAME_WIDTH), FRAME_COLOR);

        textTitle.draw(canvas, paint, pos);
        if (textCount != null) {
            textCount.draw(canvas, paint, pos);
        }

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
        int y = UDpi.toPixel(TOP_ITEM_Y);
        int iconW = UDpi.toPixel(ICON_W);
        int iconMargin = UDpi.toPixel(ICON_MARGIN_H);
        int marginH = UDpi.toPixel(MARGIN_H);
        int marginV = UDpi.toPixel(MARGIN_V);
        int dlgMargin = UDpi.toPixel(DLG_MARGIN);
        int textSize = UDpi.toPixel(TEXT_SIZE);

        List<ActionIcons> icons = ActionIcons.getInTrashIcons();

        int width = iconW * icons.size() +
                iconMargin * (icons.size() + 1);

        // Action buttons
        int x = iconMargin;
        for (ActionIcons icon : icons) {
            Bitmap image = UResourceManager.getBitmapWithColor(icon.getImageId(), UColor
                    .DarkOrange);
            UButtonImage imageButton = UButtonImage.createButton( this,
                    icon.ordinal(), 0,
                    x, y,
                    iconW, iconW, image, null);

            // アイコンの下に表示するテキストを設定
            imageButton.setTitle(icon.getTitle(mParentView.getContext()), UDpi.toPixel(ICON_TEXT_SIZE), Color.BLACK);

            imageButtons.add(imageButton);
            ULog.showRect(imageButton.getRect());

            x += iconW + iconMargin;
        }
        y += iconW + UDpi.toPixel(MARGIN_V + 17);

        // Title
        textTitle = UTextView.createInstance( mIcon.getTitle(), textSize, 0,
                UAlignment.None, canvas.getWidth(), false, true,
                marginH, y, width - marginH * 2, TEXT_COLOR, TEXT_BG_COLOR);

        y += UDpi.toPixel(TEXT_VIEW_H) + marginV;

        // テキストの幅に合わせてダイアログのサイズ更新
        Size size = UDraw.getOneLineTextSize(new Paint(), mIcon.getTitle(), textSize);
        if (size.width + marginH * 4 > width) {
            width = size.width + marginH * 4;
        }

        // Count(Bookの場合のみ)
        if (mIcon.getType() == IconType.Book) {
            long count = RealmManager.getItemPosDao().countInParentType(
                    TangoParentType.Book, mIcon.getTangoItem().getId()
            );
            textCount = UTextView.createInstance( UResourceManager.getStringById(R.string.book_count) + ":" + count, textSize, 0,
                    UAlignment.None, canvas.getWidth(), false, true,
                    marginH, y, width - marginH * 2, TEXT_COLOR, TEXT_BG_COLOR);

            // テキストの幅に合わせてダイアログのサイズ更新
            size = UDraw.getOneLineTextSize(new Paint(), textCount.getText(), textSize);
            if (size.width + marginH * 4 > width) {
                width = size.width + marginH * 4;
            }

            y += UDpi.toPixel(TEXT_VIEW_H) + marginV;
        }

        setSize(width, y);

        // Correct position
        if ( pos.x + size.width > mParentView.getWidth() - dlgMargin) {
            pos.x = mParentView.getWidth() - size.width - dlgMargin;
        }
        if (pos.y + size.height > mParentView.getHeight() - dlgMargin) {
            pos.y = mParentView.getHeight() - size.height - dlgMargin;
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

        return false;
    }

    public DoActionRet doAction() {
        DoActionRet ret = DoActionRet.None;
        for (UButtonImage button : imageButtons) {
            DoActionRet _ret = button.doAction();
            switch(_ret){
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
            case Return:
                mIconInfoCallbacks.IconInfoReturnIcon(mIcon);
                break;
            case Delete:
                mIconInfoCallbacks.IconInfoDeleteIcon(mIcon);
                break;
        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
    }

}
