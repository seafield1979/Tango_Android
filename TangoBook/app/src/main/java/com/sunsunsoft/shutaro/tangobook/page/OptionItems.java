package com.sunsunsoft.shutaro.tangobook.page;

/**
 * Created by shutaro on 2017/06/14.
 */


import android.graphics.Color;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

/**DefaultNameDialogCallbacks
 * Enum
 */
public enum OptionItems {
    TitleEdit(R.string.title_option_edit, true, Color.BLACK, UColor.LightGreen),
    ColorBook(R.string.option_color_book, false, Color.BLACK, Color.WHITE),
    ColorCard(R.string.option_color_card, false, Color.BLACK, Color.WHITE),
    CardTitle(R.string.option_card_title, false, Color.BLACK, Color.WHITE),
    DefaultNameBook(R.string.option_default_name_book, false, Color.BLACK, Color.WHITE),
    DefaultNameCard(R.string.option_default_name_card, false, Color.BLACK, Color.WHITE),
    TitleStudy(R.string.title_option_study, true, Color.BLACK, UColor.LightRed),
    AddNgCard(R.string.option_add_ng_card, false, Color.BLACK, Color.WHITE),
    StudyMode4(R.string.option_mode4_1, false, Color.BLACK, Color.WHITE),
    ;

    public String title;
    public boolean isTitle;
    public int color;
    public int bgColor;

    OptionItems(int titleId, boolean isTitle, int color, int bgColor) {
        this.title = UResourceManager.getStringById(titleId);
        this.isTitle = isTitle;
        this.color = color;
        this.bgColor = bgColor;
    }

    public static OptionItems[] getItems(PageViewOptions.Mode mode) {
        switch(mode) {
            case All:
                return values();
            case Edit:
                return new OptionItems[]{
                        TitleEdit, ColorBook, ColorCard, CardTitle, DefaultNameBook,
                        DefaultNameCard,
                };
            case Study:
                return new OptionItems[]{
                        TitleStudy, AddNgCard, StudyMode4
                };
            default:
                return null;
        }
    }

    public static OptionItems toEnum(int val) {
        if (val >= values().length) {
            return TitleEdit;
        }
        return values()[val];
    }
}