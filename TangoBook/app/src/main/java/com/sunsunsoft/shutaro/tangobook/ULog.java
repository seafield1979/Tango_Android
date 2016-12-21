package com.sunsunsoft.shutaro.tangobook;
import android.graphics.Rect;
import android.util.Log;
import java.util.HashMap;

/**
 * 出力を一括スイッチングできるLog
 * タグ毎のON/OFFを設定できる
 */
public class ULog {

    /**
     * Constants
     */
    public static final String TAG = "ULog";
    private static final boolean isCount = true;

    /**
     * Static variables
     */
    // タグ毎のON/OFF情報をMap(Dictionary)で持つ
    private static HashMap<String,Boolean> enables = new HashMap<>();
    private static HashMap<String,Integer> counters = new HashMap<>();
    private static ULogWindow logWindow;

    /**
     * Get/Set
     */
    // タグのON/OFFを設定する
    public static void setEnable(String tag, boolean enable) {
        enables.put(tag, enable);
    }
    public static void setLogWindow(ULogWindow _logWindow) {
        logWindow = _logWindow;
    }

    /**
     * Init
     */
    // 初期化、アプリ起動時に１回だけ呼ぶ
    public static void init() {
        setEnable(ViewTouch.TAG, false);
        setEnable(UDrawManager.TAG, false);
        setEnable(UMenuBar.TAG, false);
        setEnable(UScrollBar.TAG, false);
        setEnable(UIconWindow.TAG, true);
        setEnable(UButton.TAG, true);
        setEnable(UColor.TAG, false);
        setEnable(UXmlParser.TAG, false);
    }

    // ログ出力
    public static void print(String tag, String msg) {
        // 有効無効判定
        Boolean enable = enables.get(tag);
        if (enable != null && !enable) {
            // 出力しない
        } else {
            Log.v(tag, msg);
            if (logWindow != null) {
                logWindow.addLog(msg);
            }
        }
    }

    /**
     * カウントする
     * start - count ... - end
     */
    public static void startCount(String tag) {
        if (!isCount) return;

        counters.put(tag, 0);
    }
    public static void startAllCount() {
        if (!isCount) return;

        for (String tag : counters.keySet()) {
            counters.put(tag, 0);
        }
    }
    public static void count(String tag) {
        if (!isCount) return;

        Integer count = counters.get(tag);
        if (count == null) {
            count = 0;
        }
        count = count + 1;
        counters.put(tag, count);
    }
    public static void showCount(String tag) {
        if (!isCount) return;

        // 有効無効判定
        Boolean enable = enables.get(tag);
        if (enable != null && !enable) {
            // 出力しない
        } else {
            ULog.print(tag, "count:" + counters.get(tag));
        }
    }
    public static void showAllCount() {
        if (!isCount) return;

        for (String tag : counters.keySet()) {
            showCount(tag);
        }
    }


    /**
     * Static Methods
     */
    public static void showRect(Rect rect) {
        ULog.print(TAG, "Rect left:" + rect.left + " top:" + rect.top +
                    " right:" + rect.right + " bottom:" + rect.bottom);
    }

    public static void showRectF(Rect rect) {
        ULog.print(TAG, "Rect left:" + rect.left + " top:" + rect.top +
                " right:" + rect.right + " bottom:" + rect.bottom);
    }

}
