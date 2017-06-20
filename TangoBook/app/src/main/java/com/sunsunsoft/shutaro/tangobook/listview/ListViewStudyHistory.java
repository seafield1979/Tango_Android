package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Color;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBookHistory;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.UListView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by shutaro on 2016/12/14.
 *
 * 学習履歴ページで表示するリストビュー
 * 学習した単語帳の一覧を表示する
 */

public class ListViewStudyHistory extends UListView {
    /**
     * Enums
     */
    /**
     * Constants
     */

    private static final int LIMIT = 100;
    private static final int TITLE_TEXT_COLOR = UColor.BLACK;
    private static final int TITLE_BG_COLOR = UColor.Green;
    /**
     * Member variables
     */

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public ListViewStudyHistory(UListItemCallbacks listItemCallbacks,
                          int priority, float x, float y, int width, int
                                  height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        List<TangoBookHistory> histories = RealmManager.getBookHistoryDao().selectAllWithLimit
                (true, LIMIT);

        boolean[] dispTitleFlags = new boolean[5];
        int[] titleStringIds = {
                R.string.time_area_1,
                R.string.time_area_2,
                R.string.time_area_3,
                R.string.time_area_4,
                R.string.time_area_5 };

        // add items
        for (TangoBookHistory history : histories) {
            int time = getTimeArea(history.getStudiedDateTime());

            ListItemStudiedBook title = null;

            // Title
            // 各時間の先頭にタイトルを追加
            if (dispTitleFlags[time - 1] == false) {
                dispTitleFlags[time - 1] = true;
                String text = UResourceManager.getStringById(titleStringIds[time - 1]);
                title = ListItemStudiedBook.createTitle( text, size.width, TITLE_TEXT_COLOR, TITLE_BG_COLOR);
                add(title);
            }

            ListItemStudiedBook item = ListItemStudiedBook.createHistory( history,
                width, Color.BLACK, Color.WHITE);
            if (item != null) {
                add(item);
            }
        }

        updateWindow();
    }

    /**
     * Methods
     */
    private int getTimeArea(Date date) {
        Date nowDate = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);

        // 1日前まで
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date beforeDate1 = cal.getTime();

        if (date.after(beforeDate1)) {
            // 今日(~24時間前)
            return 1;
        }

        // 2日前まで
        cal.add(Calendar.DAY_OF_MONTH, -2);
        Date beforeDate2 = cal.getTime();
        if (date.before(beforeDate1) && date.after(beforeDate2)) {
            // 昨日(24時間前 ~ 48時間前)
            return 2;
        }

        // １週間前まで
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date beforeDate3 = cal.getTime();
        if (date.before(beforeDate2) && date.after(beforeDate3)) {
            // 48時間前 ~ １週間前
            return 3;
        }

        // 1ヶ月前まで
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date beforeDate4 = cal.getTime();
        if (date.before(beforeDate3) && date.after(beforeDate4)) {
            // 48時間前 ~ １週間前
            return 4;
        }
        // 1ヶ月以上前
        return 5;
    }



    /**
     * for Debug
     */
    public void addDummyItems(int count) {

        updateWindow();
    }

    /**
     * Callbacks
     */
}
