package com.sunsunsoft.shutaro.testdb;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import android.content.Context;

/**
 * Realmのオブジェクトを管理する
 */

public class MyRealmManager {
    public static Realm realm;
    public static final int Version1 = 1;
    public static final int Version2 = 2;   // Add TangoBox
    public static final int Version21 = 21;   // remove TangoBox.studyTime field
    public static final int Version22 = 22;  // add primary key to TangoBox.id
    public static final int Version23 = 23;  // set nullAble

    public static Realm getRealm() { return realm; }

    public static void initRealm(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(Version23)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
    }


    public static void closeRealm() {
        realm.close();
    }
}
