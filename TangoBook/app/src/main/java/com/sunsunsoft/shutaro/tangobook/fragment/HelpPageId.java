package com.sunsunsoft.shutaro.tangobook.fragment;

import android.graphics.Color;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

/**
 * Created by shutaro on 2017/06/14.
 * ヘルプページのId
 * ヘルプページはページ数がたくさんあるのでこれでページを判別する
 */

public enum HelpPageId {
    TitleInfo(R.string.help_title_basic, true, -1, Color.WHITE, Color.BLUE),
    Info1(R.string.help_title_basic1, false, R.layout.help_page_info1, Color.BLACK, Color.WHITE),
    Info2(R.string.help_title_basic2, false, R.layout.help_page_info2, Color.BLACK, Color.WHITE),

    TitleEdit(R.string.help_title_edit, true, -1, Color.WHITE, Color.BLUE),
    Edit0(R.string.help_title_edit0, false, R.layout.help_edit0, Color.BLACK, Color.WHITE),
    Edit1(R.string.help_title_edit1, false, R.layout.help_edit1, Color.BLACK, Color.WHITE),
    Edit2(R.string.help_title_edit2, false, R.layout.help_edit2, Color.BLACK, Color.WHITE),
    Edit3(R.string.help_title_edit3, false, R.layout.help_edit3, Color.BLACK, Color.WHITE),
    Edit4(R.string.help_title_edit4, false, R.layout.help_edit4, Color.BLACK, Color.WHITE),
    Edit5(R.string.help_title_edit5, false, R.layout.help_edit5, Color.BLACK, Color.WHITE),
    Edit6(R.string.help_title_edit6, false, R.layout.help_edit6, Color.BLACK, Color.WHITE),
    Edit7(R.string.help_title_edit7, false, R.layout.help_edit7, Color.BLACK, Color.WHITE),
    Edit8(R.string.help_title_edit8, false, R.layout.help_edit8, Color.BLACK, Color.WHITE),
    TitleStudy(R.string.help_title_study, true, -1, Color.WHITE, Color.BLUE),
    Study1(R.string.help_title_study1, false, R.layout.help_study1, Color.BLACK, Color.WHITE),
    Study2(R.string.help_title_study2, false, R.layout.help_study2, Color.BLACK, Color.WHITE),
    Study3(R.string.help_title_study3, false, R.layout.help_study3, Color.BLACK, Color.WHITE),
    Study4(R.string.help_title_study4, false, R.layout.help_study4, Color.BLACK, Color.WHITE),
    Study5(R.string.help_title_study5, false, R.layout.help_study5, Color.BLACK, Color.WHITE),
    TitleBackup(R.string.help_title_backup, true, -1, Color.WHITE, Color.BLUE),
    Backup1(R.string.help_title_backup1, false, R.layout.help_backup1, Color.BLACK, Color
            .WHITE),
    Backup2(R.string.help_title_backup2, false, R.layout.help_backup2, Color.BLACK, Color.WHITE),
    Backup3(R.string.help_title_backup3, false, R.layout.help_backup3, Color.BLACK, Color.WHITE),
    Backup4(R.string.help_title_backup4, false, R.layout.help_backup4, Color.BLACK, Color.WHITE),
    ;

    private String text;
    private boolean isTitle;
    private int layoutId;
    private int textColor;
    private int bgColor;

    HelpPageId(int textId, boolean isTitle, int layoutId, int textColor, int bgColor) {
        this.text = UResourceManager.getStringById(textId);
        this.isTitle = isTitle;
        this.layoutId = layoutId;
        this.textColor = textColor;
        this.bgColor = bgColor;
    }
    public String getText() { return text; }
    public boolean isTitle() { return isTitle; }
    public int getLayoutId() { return layoutId; }
    public int getTextColor() { return textColor; }
    public int getBgColor() { return bgColor; }

    // int を enumに変換する
    public static com.sunsunsoft.shutaro.tangobook.fragment.HelpPageId toEnum(int val) {
        if (val >= values().length) return TitleInfo;
        return values()[val];
    }
}
