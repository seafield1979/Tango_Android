package com.sunsunsoft.shutaro.tangobook;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by shutaro on 2016/12/02.
 *
 * CardやBookにつけるタグ情報（現状は名前だけ）
 */

public class TangoTagInfo extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;        // タグ名


    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}