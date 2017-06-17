package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Color;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.BackupFile;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBookHistory;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.UListView;

import java.util.List;

/**
 * Created by shutaro on 2017/06/16.
 */

public class ListViewBackup extends UListView {
    /**
     * Enums
     */
    public enum ListViewType{
        Backup,
        Restore
    }

    /**
     * Constants
     */

    private static final int LIMIT = 100;
    /**
     * Member variables
     */
    private ListViewType mLvType;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public ListViewBackup(UListItemCallbacks listItemCallbacks, ListViewType type,
                          int priority, float x, float y, int width, int
                                        height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        mLvType = type;
        // add items
        List<BackupFile> backupFiles = RealmManager.getBackupFileDao().selectAll();

        for (BackupFile backup : backupFiles) {
            ListItemBackup item = new ListItemBackup(listItemCallbacks, backup, 0, width);
            // バックアップリストでは自動バックアップは表示しない
            // 自動バックアップのスロットに手動でバックアップするのはおかしいので
            if (item.getBackup().isAutoBackup() && type == ListViewType.Backup) {
                continue;
            }
            if (item != null) {
                add(item);
            }
        }

        updateWindow();
    }

    /**
     * Methods
     */


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
