package com.sunsunsoft.shutaro.testdb;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import android.content.Context;

/**
 * Realmのオブジェクトを管理する
 */

public class MyRealmManager {
    public static Realm realm;

    public static Realm getRealm() { return realm; }

    public static void initRealm(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(1)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
    }


    public static void closeRealm() {
        realm.close();
    }
}
