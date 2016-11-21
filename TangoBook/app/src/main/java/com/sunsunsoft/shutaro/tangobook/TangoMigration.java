package com.sunsunsoft.shutaro.tangobook;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by shutaro on 2016/11/21.
 */

public class TangoMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
//        Realm realm = MyRealmManager.getRealm();
        RealmSchema schema = realm.getSchema();

        // Migrate from version 0 to version 1
        if (oldVersion == 0) {
            // マイグレーション処理
            oldVersion = 1;
        }

        // Migrate from version 1 to version 2
        if (oldVersion == RealmManager.Version1) {
        }
    }

}
