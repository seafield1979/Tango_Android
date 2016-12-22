package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;

/**
 * Created by shutaro on 2016/12/22.
 *
 * カードの情報を表示するダイアログ
 */

public class DialogCard extends UDialogWindow {
    /**
     * Constants
     */
    public static final String TAG = "DialogCard";
    public static final int OKButtonId = 10005000;

    /**
     * Member variables
     */
    private TangoCard mCard;

    /**
     * Constructor
     */
    public DialogCard(TangoCard card,
                        boolean isAnimation,
                        int screenW, int screenH)
    {
        super(DialogType.Mordal, null, null,
                ButtonDir.Horizontal, DialogPosType.Center,
                isAnimation, 0, 0, screenW, screenH,
                Color.BLACK, Color.WHITE);

        this.buttonCallbacks = this;
        this.frameColor = Color.BLACK;
        mCard = card;

        // Text views
        // WordA
        if (card.getWordA() != null) {
            addTextView(UResourceManager.getStringById(R.string.word_a), UAlignment.CenterX, true, false,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, 0);

            addTextView(card.getWordA(), UAlignment.CenterX, true, true,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, UColor.LightBlue);
        }

        // WordB
        if (card.getWordB() != null) {
            addTextView(UResourceManager.getStringById(R.string.word_b),
                    UAlignment.CenterX, true, false,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, 0);
            addTextView(card.getWordB(), UAlignment.CenterX, true, true,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, UColor.LightRed);
        }

        // Comment
        if (card.getComment() != null) {
            addTextView(UResourceManager.getStringById(R.string.comment),
                    UAlignment.CenterX, true, false,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, 0);
            addTextView(card.getComment(), UAlignment.CenterX, true, true,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, UColor.LightGreen);
        }
        // 学習履歴
        TangoCardHistory history = RealmManager.getCardHistoryDao().selectByCard(mCard);
        String historyStr;
        if (history != null) {
            historyStr = history.getCorrectFlagsAsString();
        } else {
            historyStr = "---";
        }
        addTextView(UResourceManager.getStringById(R.string.study_history) + " : " + historyStr,
                UAlignment.CenterX,
                true, true,
                UDraw.getFontSize(FontSize.M), Color.BLACK, UColor.LightCyan);

        // Cancel
        addCloseButton(UResourceManager.getStringById(R.string.close));

        setFrameColor(UColor.DarkGray);
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }

        switch(id) {
            case OKButtonId:
                startClosing();
                return true;
        }
        return false;
    }
}
