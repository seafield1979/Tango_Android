package com.sunsunsoft.shutaro.tangobook.fragment;

/**
 * Created by shutaro on 2017/06/14.
 */

import android.os.Bundle;

/**
 *  DialogFragmentのコールバック
 */
public interface DefaultNameDialogCallbacks {
    void submitDefaultName(Bundle args);
    void cancelDefaultName();
}
