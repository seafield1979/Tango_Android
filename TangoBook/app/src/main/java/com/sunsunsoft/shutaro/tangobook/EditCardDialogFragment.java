package com.sunsunsoft.shutaro.tangobook;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.Random;


interface EditCardDialogCallbacks {
    void submitEditCard(Bundle args);
    void cancelEditCard();
}

enum EditCardDialogMode {
    Create,     // 新しくアイコンを作成する
    Edit        // 既存のアイコンを編集する
    ;

    public static EditCardDialogMode toEnum(int value) {
        for (EditCardDialogMode id : values()) {
            if (id.ordinal() == value) {
                return id;
            }
        }
        return EditCardDialogMode.Create;
    }
}

/**
 * 複数の入力項目があるダイアログ
 *
 * 全画面表示
 */
public class EditCardDialogFragment extends DialogFragment {
    /**
     * Constract
     */
    // key names
    public static final String KEY_MODE = "key_mode";
    public static final String KEY_WORD_A = "key_word_a";
    public static final String KEY_WORD_B = "key_word_b";
    public static final String KEY_HINT_AB = "key_hint_ab";
    public static final String KEY_HINT_BA = "key_hint_ba";
    public static final String KEY_COMMENT = "key_comment";

    /**
     * Member variables
     */
    private EditCardDialogCallbacks dialogCallbacks;
    private int mMode;
    private EditText mEditWordA;
    private EditText mEditWordB;
    private EditText mEditHintAB;
    private EditText mEditHintBA;
    private EditText mEditComment;

    private String mWordA;
    private String mWordB;
    private String mHintAB;
    private String mHintBA;
    private String mComment;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    static EditCardDialogFragment createInstance(EditCardDialogCallbacks callbacks) {
        return createInstance(callbacks, null);
    }

    static EditCardDialogFragment createInstance(EditCardDialogCallbacks callbacks, TangoCard card) {
        EditCardDialogFragment dialog = new EditCardDialogFragment();

        dialog.dialogCallbacks = callbacks;

        // set arguments
        Bundle args = new Bundle();

        if (card != null) {

            args.putInt(KEY_MODE, EditCardDialogMode.Edit.ordinal());

            if (card.getWordA() != null) {
                args.putString(KEY_WORD_A, card.getWordA());
            }
            if (card.getWordB() != null) {
                args.putString(KEY_WORD_B, card.getWordB());
            }
            if (card.getHintAB() != null) {
                args.putString(KEY_HINT_AB, card.getHintAB());
            }
            if (card.getHintBA() != null) {
                args.putString(KEY_HINT_BA, card.getHintBA());
            }
            if (card.getComment() != null) {
                args.putString(KEY_COMMENT, card.getComment());
            }

        } else {
            args.putInt(KEY_MODE, EditCardDialogMode.Create.ordinal());
        }
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            mMode = args.getInt(KEY_MODE, EditCardDialogMode.Create.ordinal());
            mWordA = args.getString(KEY_WORD_A, "");
            mWordB = args.getString(KEY_WORD_B, "");
            mHintAB = args.getString(KEY_HINT_AB, "");
            mHintBA = args.getString(KEY_HINT_BA, "");
            mComment = args.getString(KEY_COMMENT, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_edit_card_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditWordA = (EditText)view.findViewById(R.id.editWordA);
        mEditWordA.setText(mWordA);

        mEditWordB = (EditText)view.findViewById(R.id.editWordB);
        mEditWordB.setText(mWordB);

        mEditHintAB = (EditText)view.findViewById(R.id.editHintAB);
        mEditHintAB.setText(mHintAB);

        mEditHintBA = (EditText)view.findViewById(R.id.editHintBA);
        mEditHintBA.setText(mHintBA);

        mEditComment = (EditText)view.findViewById(R.id.editComment);
        mEditComment.setText(mComment);

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
        (view.findViewById(R.id.buttonRandom)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setRandomValue();
            }
        });

        setStyle(STYLE_NORMAL, android.R.style.Theme);
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        Bundle args = new Bundle();
        args.putInt(KEY_MODE, mMode);
        args.putString(KEY_WORD_A, mEditWordA.getText().toString());
        args.putString(KEY_WORD_B, mEditWordB.getText().toString());
        args.putString(KEY_HINT_AB, mEditHintAB.getText().toString());
        args.putString(KEY_HINT_BA, mEditHintBA.getText().toString());
        args.putString(KEY_COMMENT, mEditComment.getText().toString());

        if (dialogCallbacks != null) {
            dialogCallbacks.submitEditCard(args);
        }

        dismiss();
    }

    /**
     * キャンセルしたときの処理
     */
    private void cancel() {
        if (dialogCallbacks != null) {
            dialogCallbacks.cancelEditCard();
        }
        dismiss();
    }

    /**
     * ランダム値を設定
     */
    private void setRandomValue() {
        Random rand = new Random();
        int value = rand.nextInt(1000);

        mEditWordA.setText("A " + value);
        mEditWordB.setText("B " + value);
        mEditHintAB.setText("HA " + value);
        mEditHintBA.setText("HB " + value);
        mEditComment.setText("C " + value);
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