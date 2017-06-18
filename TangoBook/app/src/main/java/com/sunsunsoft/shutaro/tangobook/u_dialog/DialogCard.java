package com.sunsunsoft.shutaro.tangobook.u_dialog;

import android.graphics.Color;

import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.database.TangoCardHistory;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

/**
 * Created by shutaro on 2016/12/22.
 *
 * カードの情報を表示するダイアログ
 * カード編集ページでカードアイコンをクリックした際に表示される
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
                Color.BLACK, UColor.WHITE);

        this.buttonCallbacks = this;
        this.frameColor = Color.BLACK;
        mCard = card;

        // Text views
        // WordA
        if (card.getWordA() != null && card.getWordA().length() > 0) {
            addTextView(UResourceManager.getStringById(R.string.word_a), UAlignment.CenterX, true, false,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, 0);

            addTextView(card.getWordA(), UAlignment.CenterX, true, true,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, Color.LTGRAY);
        }

        // WordB
        if (card.getWordB() != null && card.getWordB().length() > 0) {
            addTextView(UResourceManager.getStringById(R.string.word_b),
                    UAlignment.CenterX, true, false,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, 0);
            addTextView(card.getWordB(), UAlignment.CenterX, true, true,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, Color.LTGRAY);
        }

        // Comment
        if (card.getComment() != null && card.getComment().length() > 0) {
            addTextView(UResourceManager.getStringById(R.string.comment),
                    UAlignment.CenterX, true, false,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, 0);
            addTextView(card.getComment(), UAlignment.CenterX, true, true,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, Color.LTGRAY);
        }
        // 学習履歴
        TangoCardHistory history = RealmManager.getCardHistoryDao().selectByCard(mCard);
        String historyStr;
        if (history != null) {
            historyStr = history.getCorrectFlagsAsString();
            addTextView(UResourceManager.getStringById(R.string.study_history) + " : " + historyStr,
                    UAlignment.CenterX,
                    true, true,
                    UDraw.getFontSize(FontSize.M), Color.BLACK, Color.LTGRAY);
        }

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
