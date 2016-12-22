package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Realmのオブジェクトを管理する
 */

public class RealmManager {
    public static final String TAG = "RealmManager";
    public static final int Version1 = 1;
    public static final int latestVersion = Version1;
    private static final String DefaultFileName = "default.realm";


    public static Realm getRealm() { return realm; }


    /**
     * Static variables
     */
    public static Realm realm;
    private static Context context;
    private static RealmConfiguration realmConfiguration;

    private static TangoCardDao cardDao;
    private static TangoBookDao bookDao;
    private static TangoItemPosDao itemPosDao;
    private static TangoCardHistoryDao cardHistoryDao;
    private static TangoBookHistoryDao bookHistoryDao;
    private static TangoStudiedCardDao studiedCardDao;
    private static TangoTagDao tagDao;
    private static TangoItemsCheckDao itemsCheckDao;
    private static boolean initFlag;

    /**
     * 初期化処理
     * アプリ起動時、バックアップからリストア時に呼ばれる
     * @param _context
     * @param forceInit
     */
    public static void initRealm(Context _context, boolean forceInit) {
        if ( !forceInit && initFlag) return;

        if (realm != null && !realm.isClosed()) {
            realm.close();
        }

        initFlag = true;
        context = _context;

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(latestVersion)
                .migration(new TangoMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();

        cardDao = new TangoCardDao(realm);
        bookDao = new TangoBookDao(realm);
        itemPosDao = new TangoItemPosDao(realm);
        cardHistoryDao = new TangoCardHistoryDao(realm);
        bookHistoryDao = new TangoBookHistoryDao(realm);
        studiedCardDao = new TangoStudiedCardDao(realm);
        itemsCheckDao = new TangoItemsCheckDao(realm);

        tagDao = new TangoTagDao(realm);

    }

    public static TangoCardDao getCardDao() {
        return cardDao;
    }

    public static TangoBookDao getBookDao() {
        return bookDao;
    }

    public static TangoItemPosDao getItemPosDao() { return itemPosDao; }

    public static TangoCardHistoryDao getCardHistoryDao() {
        return cardHistoryDao;
    }

    public static TangoBookHistoryDao getBookHistoryDao() {
        return bookHistoryDao;
    }

    public static TangoStudiedCardDao getStudiedCardDao() {
        return studiedCardDao;
    }

    public static TangoTagDao getTagDao() {
        return tagDao;
    }

    public static TangoItemsCheckDao getCheckDao() { return itemsCheckDao; }

    public static void closeRealm() {
        realm.close();
    }

    /**
     * Methods
     */
    /**
     * バックアップを作成する
     */
    public static String backup() {

        File exportRealmFile = null;
        File exportRealmPATH = getBackupPath();

        String exportRealmFileName = DefaultFileName;

        Log.d(TAG, "Realm DB Path = " + realm.getPath());

        try {
            // create a backup file
            exportRealmFile = new File(exportRealmPATH, exportRealmFileName);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            realm.writeCopyTo(exportRealmFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        String msg =  "File exported to Path: " + context.getExternalFilesDir(null);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Log.d(TAG, msg);

        return exportRealmFile.toString();
    }

    /**
     * restore Realm from backup file
     */
    public static void restore() {

        realm.close();
        //Restore
        copyBundledRealmFile();

        Log.d(TAG, "Data restore is done");

        initRealm(context, true);
    }

    /**
     *
     * @return
     */
    private static String copyBundledRealmFile() {
        try {
            // バックアップ元ファイル
            File backupFile = new File(getBackupPath(), DefaultFileName);
            FileInputStream inputStream = new FileInputStream(backupFile);

            // バックアップ先(Realmのデフォルトのファイルパス)
            FileOutputStream outputStream = new FileOutputStream(realm.getPath());

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return backupFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File getBackupPath() {
        if (false) {
            return context.getExternalFilesDir(null);
        } else {
            return Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOCUMENTS);
        }
    }

    private String dbPath(){
        return realm.getPath();
    }
}
