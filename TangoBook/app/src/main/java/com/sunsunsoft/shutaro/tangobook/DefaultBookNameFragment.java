package com.sunsunsoft.shutaro.tangobook;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

/**
 * オプションの色設定DialogFragment
 */

/**
 *  DialogFragmentのコールバック
 */
interface DefaultNameDialogCallbacks {
    void submitDefaultName(Bundle args);
    void cancelDefaultName();
}

public class DefaultBookNameFragment extends DialogFragment {

    /**
     * Constract
     */
    public static final String TAG = "DefaultBookNameFragment";
    public static final int FragmentType = 1;

    // key names
    public static final String KEY_NAME = "key_name";
    public static final String KEY_FRAGMENT_TYPE = "key_fragment_type";

    /**
     * Member variables
     */
    private DefaultNameDialogCallbacks dialogCallbacks;
    private String mDefaultName;

    // 選択された色を表示するView
    private EditText mNameEdit;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    static DefaultBookNameFragment createInstance(DefaultNameDialogCallbacks callbacks)
    {
        DefaultBookNameFragment dialog = new DefaultBookNameFragment();

        dialog.dialogCallbacks = callbacks;

        // set arguments
        Bundle args = new Bundle();

        args.putString(KEY_NAME, MySharedPref.readString(MySharedPref.DefaultNameBookKey));
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            mDefaultName = args.getString(KEY_NAME, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_default_name, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // title
        String title = UResourceManager.getStringById(
                R.string.default_name_book
        );
        ((TextView)view.findViewById(R.id.textView)).setText(title);

        // edit
        mNameEdit = (EditText) view.findViewById(R.id.editName);
        mNameEdit.setText(mDefaultName);

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
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        Bundle args = new Bundle();

        args.putInt(KEY_FRAGMENT_TYPE, FragmentType);
        args.putString(KEY_NAME, mNameEdit.getText().toString());

        MySharedPref.writeString(MySharedPref.DefaultNameBookKey, mNameEdit.getText().toString());

        if (dialogCallbacks != null) {
            dialogCallbacks.submitDefaultName(args);
        }

        dismiss();
    }

    /**
     * キャンセルしたときの処理
     */
    private void cancel() {
        if (dialogCallbacks != null) {
            dialogCallbacks.cancelDefaultName();
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