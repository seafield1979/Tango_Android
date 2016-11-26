package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.LinkedList;

/**
 * カードアイコンを作成する
 */
public class CardIconInfoDialog extends IconInfoDialog {
    /**
     * Enums
     */
    enum ActionIcons{
        Edit,
        MoveToTrash,
        Copy,
        Favorite,
        None
        ;

        public static int size() {
            return values().length - 1;
        }
        public static ActionIcons toEnum(int value) {
            if (value >= values().length) {
                return None;
            }
            return values()[value];
        }
    }

    /**
     * Consts
     */
    private static final String TAG = "CardIconInfoDialog";
    private static final int BG_COLOR = Color.LTGRAY;
    private static final int DLG_MARGIN = 100;
    private static final int TOP_ITEM_Y = 50;
    private static final int TEXT_VIEW_W = 300;
    private static final int TEXT_VIEW_H = 100;
    private static final int ICON_W = 120;
    private static final int ICON_MARGIN_H = 30;
    private static final int MARGIN_V = 40;
    private static final int TEXT_SIZE = 50;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_BG_COLOR = Color.WHITE;

    /**
     * Member Variables
     */
    private UTextView textWordA;
    private UTextView textWordB;
    private TangoCard mCard;
    private LinkedList<UButtonImage> imageButtons = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public CardIconInfoDialog(View parentView,
                              UButtonCallbacks buttonCallbacks, UDialogCallbacks dialogCallbacks,
                              ButtonDir dir,
                              float x, float y,
                              int screenW, int screenH,
                              int textColor, int dialogColor)
    {
        // width, height はinit内で計算するのでここでは0を設定
        super( buttonCallbacks, dialogCallbacks, dir, false, x, y,
                screenW, screenH, textColor, dialogColor);
        mParentView = parentView;
    }

    public static CardIconInfoDialog createInstance(
            View parentView,
            UButtonCallbacks buttonCallbacks, UDialogCallbacks dialogCallbacks,
            float x, float y,
            int screenW, int screenH,
            TangoCard card)
    {
        CardIconInfoDialog instance = new CardIconInfoDialog( parentView,
                buttonCallbacks, dialogCallbacks, ButtonDir.Horizontal, x, y,
                screenW, screenH, TEXT_COLOR, BG_COLOR);
        instance.mCard = card;

        // 初期化処理
        instance.addCloseIcon(CloseButtonPos.RightTop);
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
        if (isAnimating) {
            // Open/Close animatin
            float ratio;
            if (animationType == AnimationType.Opening) {
                ratio = (float)Math.sin(animeRatio * 90 * RAD);
                ULog.print(TAG, "ratio:" + ratio);
            } else {
                ratio = (float)Math.cos(animeRatio * 90 * RAD);
                ULog.print(TAG, "cos ratio:" + ratio);
            }
            float width = dialogSize.width * ratio;
            float height = dialogSize.height * ratio;
            float x = (size.width - width) / 2;
            float y = (size.height - height) / 2;
            RectF _rect = new RectF(x, y, x + width, y + height);
            UDraw.drawRoundRectFill(canvas, paint, _rect, 20, dialogColor);

        } else {
            // BG
            UDraw.drawRoundRectFill(canvas, paint, getDialogRect(), 20, dialogColor);

            textWordA.draw(canvas, paint, dialogPos);
            textWordB.draw(canvas, paint, dialogPos);
            // Buttons
            for (UButtonImage button : imageButtons) {
                button.draw(canvas, paint, dialogPos);
            }
        }
    }

    /**
     * レイアウト更新
     * @param canvas
     */
    protected void updateLayout(Canvas canvas) {

        int y = TOP_ITEM_Y;

        int width = ICON_W * ActionIcons.size() + ICON_MARGIN_H * (ActionIcons.size() + 1);

        // WordA
        textWordA = UTextView.createInstance( mCard.getWordA(), TEXT_SIZE, 0,
                UDraw.UAlignment.None, canvas.getWidth(), true,
                MARGIN_H, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);

        y += TEXT_VIEW_H + MARGIN_V;

        // WordB
        textWordB = UTextView.createInstance( mCard.getWordB(), TEXT_SIZE, 0,
                UDraw.UAlignment.None, TEXT_VIEW_W, true,
                MARGIN_H, y, width - MARGIN_H * 2, TEXT_COLOR, TEXT_BG_COLOR);

        y += TEXT_VIEW_H + MARGIN_V + 50;

        // アクションボタン
        int x = ICON_MARGIN_H;
        Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), R.drawable.hogeman);
        Bitmap bmp2 = BitmapFactory.decodeResource(mParentView.getResources(), R.drawable.hogeman2);

        for (int i=0; i<ActionIcons.size(); i++) {
            UButtonImage imageButton = UButtonImage.createButton( this,
                            ActionIcons.Edit.ordinal(), 0,
                            x, y,
                            ICON_W, ICON_W, bmp, bmp2);
            imageButtons.add(imageButton);
            buttons.add(imageButton);

            x += ICON_W + ICON_MARGIN_H;
        }
        y += ICON_W + MARGIN_V;

        dialogSize.height = y;
        dialogSize.width = width;

        // 座標補正
        if ( dialogPos.x + dialogSize.width > mParentView.getWidth() - DLG_MARGIN) {
            dialogPos.x = mParentView.getWidth() - dialogSize.width - DLG_MARGIN;
        }
        if (dialogPos.y + dialogSize.height > mParentView.getHeight() - DLG_MARGIN) {
            dialogPos.y = mParentView.getHeight() - dialogSize.height - DLG_MARGIN;
        }
    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public void UButtonClick(int id) {
        switch(ActionIcons.toEnum(id)) {
            case Edit:

                break;
            case MoveToTrash:
                break;
            case Copy:
                break;
            case Favorite:
                break;
        }
    }

    public void UButtonLongClick(int id) {

    }

}
