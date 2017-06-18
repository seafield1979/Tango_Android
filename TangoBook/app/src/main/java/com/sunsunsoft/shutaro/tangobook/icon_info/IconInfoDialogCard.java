package com.sunsunsoft.shutaro.tangobook.icon_info;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.uview.FontSize;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.database.TangoCardHistory;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonImage;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.icon.UIcon;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindowCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.icon.IconCard;

import java.util.LinkedList;
import java.util.List;

/**
 * カードアイコンをクリックした際に表示されるダイアログ
 * カードの情報(WordA,WordB)とアクションアイコン(ActionIcons)を表示する
 */
public class IconInfoDialogCard extends IconInfoDialog {
    /**
     * Enums
     */
    enum ButtonId{
        Edit,
        Copy,
        MoveToTrash,
        Favorite
    }

    enum Items {
        WordA,
        WordB,
        Comment,
        History
    }

    /**
     * Consts
     */
    private static final String TAG = "IconInfoDialogCard";
    private static final int DLG_MARGIN = 100;
    private static final int ICON_W = 120;
    private static final int ICON_MARGIN_H = 30;
    private static final int TEXT_SIZE = 50;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    /**
     * Member Variables
     */
    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された
    private UTextView textTitle;
    private IconInfoItem[] mItems = new IconInfoItem[Items.values().length];
    private TangoCard mCard;
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();

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
        if (icon instanceof IconCard) {
            IconCard cardIcon = (IconCard)icon;
            mCard = (TangoCard)cardIcon.getTangoItem();

            if (color == 0) color = Color.LTGRAY;

            bgColor = UColor.setBrightness(color, 220);
            frameColor = UColor.setBrightness(color, 140);
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
                x, y, ((TangoCard)icon.getTangoItem()).getColor());

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

        textTitle.draw(canvas, paint, pos);
        for (IconInfoItem item : mItems) {
            if (item == null) continue;
            item.title.draw(canvas, paint, pos);
            item.body.draw(canvas, paint, pos);
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

        int y = TOP_ITEM_Y;

        List<ActionIcons> icons = ActionIcons.getCardIcons();

        int width = ICON_W * icons.size() + ICON_MARGIN_H * (icons.size() + 1) + 100;
        int fontSize = UDraw.getFontSize(FontSize.M);

        // タイトル(カード)
        textTitle = UTextView.createInstance( mContext.getString(R.string.card),
                TEXT_SIZE, 0,
                UAlignment.None, canvas.getWidth(), false, false,
                MARGIN_H, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);
        y += TEXT_SIZE + MARGIN_V_S;

        String titleStr = null;
        String bodyStr = null;
        int bgColor = UColor.WHITE;

        for (Items item : Items.values()) {
            switch(item) {
                case WordA:
                    titleStr = mContext.getString(R.string.word_a);
                    bodyStr = UUtil.convString(mCard.getWordA(), false, 2, 0);
                    break;

                case WordB:
                    titleStr = mContext.getString(R.string.word_b);
                    bodyStr = UUtil.convString(mCard.getWordB(), false, 2, 0);
                    break;

                case Comment:
                    titleStr = mContext.getString(R.string.comment);
                    bodyStr = UUtil.convString(mCard.getComment(), false, 2, 0);
                    if (bodyStr == null || bodyStr.length() == 0) {
                        continue;
                    }
                    break;

                case History:   // 学習履歴
                {
                    TangoCardHistory history = RealmManager.getCardHistoryDao().selectByCard(mCard);
                    String historyStr;
                    if (history != null) {
                        historyStr = history.getCorrectFlagsAsString();
                    } else {
                        continue;
                    }

                    titleStr = mContext.getString(R.string.study_history);
                    bodyStr = historyStr;
                }
                    break;
            }
            mItems[item.ordinal()] = new IconInfoItem();

            // title
            mItems[item.ordinal()].title = UTextView.createInstance( titleStr ,
                    fontSize, 0,
                    UAlignment.None, canvas.getWidth(), false, false,
                    MARGIN_H, y, size.width - MARGIN_H, TEXT_COLOR, 0);

            y += mItems[item.ordinal()].title.getHeight() + 10;

            // body
            mItems[item.ordinal()].body = UTextView.createInstance( bodyStr,
                    fontSize, 0,
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
            if (item == null) continue;
            item.title.setWidth(width - MARGIN_H * 2);
        }

        // アクションボタン
        int x = (width - (ICON_W * icons.size() + MARGIN_H * (icons.size() - 1))) / 2;
        for (ActionIcons icon : icons) {
            int color = (icon == ActionIcons.Favorite) ? UColor.LightYellow : frameColor;

            UButtonImage imageButton;
            // お気に入りはON/OFF用の２つ画像を登録する
            if (icon == ActionIcons.Favorite) {
                Bitmap image = UResourceManager.getBitmapWithColor(R.drawable.favorites, UColor
                        .OrangeRed);
                Bitmap image2 = UResourceManager.getBitmapWithColor(R.drawable.favorites2, UColor
                        .OrangeRed);

                imageButton = UButtonImage.createButton( this,
                        icon.ordinal(), 0,
                        x, y,
                        ICON_W, ICON_W, image, null);

                imageButton.addState(image2);
                if (mCard.getStar()) {
                    imageButton.setState(mCard.getStar() ? 1 : 0);
                }
            } else {
                Bitmap image = UResourceManager.getBitmapWithColor(icon.getImageId(), color);
                imageButton = UButtonImage.createButton( this,
                        icon.ordinal(), 0,
                        x, y,
                        ICON_W, ICON_W, image, null);
            }

            // アイコンの下に表示するテキストを設定
            imageButton.setTitle(icon.getTitle(mContext), 30, Color.BLACK);

            imageButtons.add(imageButton);
            ULog.showRect(imageButton.getRect());

            x += ICON_W + ICON_MARGIN_H;
        }
        y += ICON_W + MARGIN_V + 50;

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
            switch(_ret){
                case Done:
                    return DoActionRet.Done;
                case Redraw:
                    ret = DoActionRet.Redraw;
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
            case Favorite: {
                TangoCard card = (TangoCard)mIcon.getTangoItem();
                boolean star = RealmManager.getCardDao().toggleStar(card);
                card.setStar(star);

                // 表示アイコンを更新
                imageButtons.get(ButtonId.Favorite.ordinal()).setState(star ? 1 : 0);
            }
                break;
        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
    }

}
