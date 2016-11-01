package com.sunsunsoft.shutaro.testdb;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * データベースのモデルが変更された時の処理
 *
 * https://github.com/realm/realm-java/blob/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample/model/Migration.java
 */

public class MyMigration implements RealmMigration{
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
        if (oldVersion == MyRealmManager.Version1) {
            // マイグレーション処理

            // Create a new class
            RealmObjectSchema boxSchema = schema.create("TangoBox")
                    .addField("id", Integer.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class, FieldAttribute.REQUIRED)
                    .addField("comment", String.class, FieldAttribute.REQUIRED)
                    .addField("color", Integer.class, FieldAttribute.REQUIRED)
                    .addField("createTime", Date.class, FieldAttribute.REQUIRED)
                    .addField("updateTime", Date.class, FieldAttribute.REQUIRED);

            oldVersion = MyRealmManager.Version2;
        }

        if (oldVersion == MyRealmManager.Version2) {
            // 間違って追加した TangoBox の studyTime カラムを削除
            RealmObjectSchema boxSchema = schema.get("TangoBox")
                    .removeField("studyTime");

            oldVersion = MyRealmManager.Version21;
        }

        if (oldVersion == MyRealmManager.Version21) {
            // TangoBoxのidにプライマリーキーを追加する
            RealmObjectSchema boxSchema = schema.get("TangoBox");
            if(!boxSchema.hasPrimaryKey()) boxSchema.addPrimaryKey("id");

            oldVersion = MyRealmManager.Version22;
        }

        if (oldVersion == MyRealmManager.Version22) {
            // TangoBoxのStringフィールドにnullable属性を追加
            RealmObjectSchema boxSchema = schema.get("TangoBox");

            boxSchema.setNullable("name", true);
            boxSchema.setNullable("comment", true);
            boxSchema.setNullable("createTime", true);
            boxSchema.setNullable("updateTime", true);

            oldVersion = MyRealmManager.Version23;
        }

    }
}