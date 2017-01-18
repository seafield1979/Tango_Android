package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/09.
 *
 * Bitmap画像やstrings以下の文字列等のリソースを管理する
 */
public class UResourceManager {
    /**
     * Constants
     */
    public static final String TAG = "UResourceManager";

    /**
     * Member variables
     */
    private Context mContext;
    private View mView;

    // 通常画像のキャッシュ
    private static HashMap<Integer, Bitmap> imageCache = new HashMap<>();

    // 色を変えた画像のキャッシュ
    private static HashMap<ArrayList, Bitmap> colorImageCache = new HashMap<>();

    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static UResourceManager singleton;

    // Singletonオブジェクトを作成する
    public static UResourceManager createInstance(Context context) {
        if (singleton == null ) {
            singleton = new UResourceManager(context);
        }
//        else if (singleton.mView != view) {
//            // Viewが異なっていたら別のページに遷移したものとみなし再生成する
//            singleton = new UResourceManager(context, view);
//        }
        return singleton;
    }
    public static UResourceManager getInstance() { return singleton; }

    private UResourceManager(Context context) {
        mContext = context;
    }

    public void setView(View view) {
        singleton.mView = view;
    }

    /**
     * Methods
     */
    public static void clear() {
        UResourceManager instance = getInstance();
        imageCache.clear();
        colorImageCache.clear();
    }

    /**
     * stringsのIDで文字列を取得する
     * @param strId
     */
    public static String getStringById(int strId) {
        UResourceManager instance = getInstance();

        return instance.mContext.getString(strId);
    }

    /**
     * Bitmapを取得
     * @param bmpId
     * @return Bitmapオブジェクト / もしBitmapがロードできなかったら null
     */
    public static Bitmap getBitmapById(int bmpId) {
        UResourceManager instance = getInstance();

        // キャッシュがあるならそちらを取得
        Bitmap bmp = imageCache.get(bmpId);
        if (bmp != null) {
            ULog.print(TAG, "cache hit!! bmpId:" + bmpId);
            return bmp;
        }


        // 未ロードならロードしてからオブジェクトを返す
        bmp = BitmapFactory.decodeResource(instance.mView.getResources(), bmpId);
        if (bmp != null) {
            imageCache.put(bmpId, bmp);
            return bmp;
        }
        return null;
    }

    public static Bitmap getBitmapWithColor(int bmpId, int color) {
        // キャッシュがあるならそちらを取得
        ArrayList<Integer> key = new ArrayList<>();
        key.add(bmpId);
        key.add(color);
        Bitmap bmp = colorImageCache.get(key);
        if (bmp != null) {
            // キャッシュを返す
            ULog.print(TAG, "cache hit!! bmpId:" + bmpId + " color:" + UColor.toString(color));
            return bmp;
        }

        // キャッシュがなかったのでBitmapを生成
        bmp = getBitmapById(bmpId);
        if (color != 0) {
            bmp = UUtil.convBitmapColor(bmp, color);
        }
        // キャッシュに追加
        colorImageCache.put(key, bmp);

        return bmp;
    }
}
