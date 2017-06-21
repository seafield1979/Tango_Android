package com.sunsunsoft.shutaro.tangobook.database;

import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.util.UDebug;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2017/06/16.
 *
 * バックアップファイル情報のDAO
 */

public class BackupFileDao {
    /**
     * Constants
     */
    public static final String TAG = "BackupFileDao";

    public static final int AUTO_BACKUP_ID = 0;
    public static final int MAX_BACKUP_NUM = 10;

    /**
     * Member variables
     */
    private Realm mRealm;

    /**
     * Constructor
     * @param realm
     */
    public BackupFileDao(Realm realm) {
        mRealm = realm;
    }


    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<BackupFile> selectAll() {

        RealmResults<BackupFile> results = mRealm.where(BackupFile.class).findAll();

        if (UDebug.debugDAO) {
            Log.d(TAG, "BackupFile selectAll");
            for (BackupFile backup : results) {
                Log.d(TAG, "id:" + backup.getId() +
                        " enabled:" + backup.isEnabled() +
                        " dateTime:" + backup.getDateTime() +
                        " cardNum:" + backup.getCardNum() +
                        " bookNum:" + backup.getBookNum());
            }
        }
        return results;
    }

    /**
     * アプリ初回起動時にレコードを作成する
     * 実際にバックアップとして使用しているレコードは enabled が trueになる
     */
    public void createInitialRecords() {
        long count = mRealm.where(BackupFile.class).count();

        if (count == 0) {
            mRealm.beginTransaction();

            // 自動バックアップ用
            BackupFile backup = new BackupFile();
            backup.setId(AUTO_BACKUP_ID);
            backup.setEnabled(false);
            mRealm.copyToRealm(backup);

            // マニュアルバックアップ用
            for (int i = 0; i < MAX_BACKUP_NUM; i++) {
                backup = new BackupFile();
                backup.setId(i+1);
                backup.setEnabled(false);

                mRealm.copyToRealm(backup);
            }

            mRealm.commitTransaction();
        }
    }

    /**
     * 要素を追加
     * アプリ初回起動時にレコードだけを追加するためのメソッド
     * @param
     * @param
     */
    public void addOne() {
        int newId = getNextId();

        BackupFile backup = new BackupFile();
        backup.setId(newId);
        backup.setEnabled(false);

        mRealm.beginTransaction();
        mRealm.copyToRealm(backup);
        mRealm.commitTransaction();
    }

    /**
     * １件更新　バックアップが成功した後に呼ぶ
     * @param id
     * @param filePath
     * @param bookNum
     * @param cardNum
     * @return
     */
    public boolean updateOne(int id, String filePath, int bookNum, int cardNum) {
        mRealm.beginTransaction();

        BackupFile backup = mRealm.where(BackupFile.class).equalTo("id", id).findFirst();
        if (backup == null) {
            return false;
        }
        backup.setEnabled(true);
        backup.setBookNum(bookNum);
        backup.setCardNum(cardNum);
        backup.setFilePath(filePath);
        backup.setDateTime(new Date());

        mRealm.commitTransaction();

        return true;
    }

    /**
     * バックアップ情報をクリアする
     * @param id
     */
    public boolean clearOne(int id) {
        mRealm.beginTransaction();

        BackupFile backup = mRealm.where(BackupFile.class).equalTo("id", id).findFirst();
        if (backup == null) {
            return false;
        }
        backup.setEnabled(false);

        mRealm.commitTransaction();

        return true;
    }

    /**
     * かぶらないプライマリIDを取得する
     * @return
     */
    public int getNextId() {
        // 初期化
        int nextId = 1;
        // userIdの最大値を取得
        Number maxId = mRealm.where(BackupFile.class).max("id");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxId != null) {
            nextId = maxId.intValue() + 1;
        }
        return nextId;
    }
}
