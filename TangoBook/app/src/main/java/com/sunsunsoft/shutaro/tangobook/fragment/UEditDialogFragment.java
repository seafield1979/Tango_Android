package com.sunsunsoft.shutaro.tangobook.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.sunsunsoft.shutaro.tangobook.R;

/**
 * ダイアログ用のFragmentサンプル
 * DialogFragmentのサブクラス
 */
public class UEditDialogFragment extends DialogFragment {
    /**
     * Constract
     */
    private final static String KEY_NAME = "key_name";

    // key names
    public static final String KEY_RET = "key_ret";
    public static final String KEY_TEXT1 = "key_text1";

    // ダイアログの名前
    String mName;
    String mText;

    EditText edit1;

    UEditDialogCallbacks editCallbacks;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    static UEditDialogFragment createInstance(UEditDialogCallbacks callbacks, String name, String
            text) {
        UEditDialogFragment dialog = new UEditDialogFragment();

        dialog.editCallbacks = callbacks;

        // set arguments
        Bundle args = new Bundle();
        args.putString(KEY_NAME, name);
        args.putString(KEY_TEXT1, text);
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        mName = getArguments().getString(KEY_NAME);
        mText = getArguments().getString(KEY_TEXT1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.uedit_dialog_contents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = (TextView)view.findViewById(R.id.text);
        textView.setText(mName);

        EditText editText = (EditText)view.findViewById(R.id.editText) ;
        editText.setText(mText);
        editText.setHint("text1");


        // Listener
        (view.findViewById(R.id.buttonOK)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submit();
            }
        });

        (view.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        });

        edit1 = (EditText)view.findViewById(R.id.editText);

        setStyle(STYLE_NORMAL, android.R.style.Theme);
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        Bundle args = new Bundle();
        String str = edit1.getText().toString() + "\n";

        args.putString(KEY_RET, str);

        if (editCallbacks != null) {
            if (args != null) {
                editCallbacks.submit(args);
            }
        }

        dismiss();
    }

    /**
     * キャンセルしたときの処理
     */
    private void cancel() {
        if (editCallbacks != null) {
            editCallbacks.cancel();
        }
        dismiss();
    }

    /**
     *
     * @param dialog
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        cancel();
    }
}
