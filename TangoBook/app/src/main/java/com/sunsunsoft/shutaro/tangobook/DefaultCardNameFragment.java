package com.sunsunsoft.shutaro.tangobook;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

/**
 * オプションの色設定DialogFragment
 */

public class DefaultCardNameFragment extends DialogFragment {
    /**
     * Constract
     */
    public static final String TAG = "DefaultBookNameFragment";
    public static final int FragmentType = 2;

    // key names
    public static final String KEY_WORD_A = "key_word_a";
    public static final String KEY_WORD_B = "key_word_b";
    public static final String KEY_FRAGMENT_TYPE = "key_fragment_type";

    /**
     * Member variables
     */
    private DefaultNameDialogCallbacks dialogCallbacks;
    private String mWordA, mWordB;

    // 選択された色を表示するView
    private EditText mEditWordA, mEditWordB;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    static DefaultCardNameFragment createInstance(DefaultNameDialogCallbacks callbacks)
    {
        DefaultCardNameFragment dialog = new DefaultCardNameFragment();

        dialog.dialogCallbacks = callbacks;

        // set arguments
        Bundle args = new Bundle();

        args.putString(KEY_WORD_A, MySharedPref.readString(MySharedPref.DefaultCardWordAKey, null));
        args.putString(KEY_WORD_B, MySharedPref.readString(MySharedPref.DefaultCardWordBKey, null));

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            mWordA = args.getString(KEY_WORD_A, "");
            mWordB = args.getString(KEY_WORD_B, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_default_card_name, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // edit
        mEditWordA = (EditText) view.findViewById(R.id.editWordA);
        mEditWordA.setText(mWordA);

        mEditWordB = (EditText) view.findViewById(R.id.editWordB);
        mEditWordB.setText(mWordB);

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

        String wordA, wordB;
        wordA = mEditWordA.getText().toString();
        wordB = mEditWordB.getText().toString();

        if (wordA != null) {
            args.putString(KEY_WORD_A, wordA);
            MySharedPref.writeString(MySharedPref.DefaultCardWordAKey, wordA);
        }
        if (wordB != null) {
            args.putString(KEY_WORD_B, wordB);
            MySharedPref.writeString(MySharedPref.DefaultCardWordBKey, wordB);
        }

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