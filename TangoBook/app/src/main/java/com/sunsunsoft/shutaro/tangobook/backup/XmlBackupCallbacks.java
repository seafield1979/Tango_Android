package com.sunsunsoft.shutaro.tangobook.backup;

/**
 * Created by shutaro on 2017/06/23.
 *
 * XmlManagerのスレッド処理完了時のコールバックメソッド
 */

public interface XmlBackupCallbacks {

    /**
     * バックアップ処理完了
     * @return
     */
    void finishBackup(BackupFileInfo fileInfo);

    /**
     * 復元処理完了
     */

}
