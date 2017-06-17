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

public class ListViewBackupDB extends UListView {
    /**
     * Enums
     */
    /**
     * Constants
     */

    private static final int LIMIT = 100;
    /**
     * Member variables
     */

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public ListViewBackupDB(UListItemCallbacks listItemCallbacks,
                                int priority, float x, float y, int width, int
                                        height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        // add items
        List<BackupFile> backupFiles = RealmManager.getBackupFileDao().selectAll();

        for (BackupFile backup : backupFiles) {
            ListItemBackup item = new ListItemBackup(listItemCallbacks, backup, 0, width);
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
