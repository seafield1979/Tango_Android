package com.sunsunsoft.shutaro.tangobook;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


interface EditBookDialogCallbacks {
    void submitEditBook(Bundle args);
    void cancelEditBook();
}

/**
 * 複数の入力項目があるダイアログ
 *
 * 全画面表示
 */
public class EditBookDialogFragment extends DialogFragment {

    /**
     * Constract
     */

    // key names
    public static final String KEY_NAME = "key_name";
    public static final String KEY_COMMENT = "key_comment";
    public static final String KEY_COLOR = "key_color";

    /**
     * Member variables
     */
    private EditBookDialogCallbacks dialogCallbacks;

    private String mName;
    private String mComment;

    private EditText mEditName;
    private EditText mEditComment;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    static EditBookDialogFragment createInstance(EditBookDialogCallbacks callbacks) {
        return createInstance(callbacks, null);
    }

    static EditBookDialogFragment createInstance(EditBookDialogCallbacks callbacks, TangoBook book) {
        EditBookDialogFragment dialog = new EditBookDialogFragment();

        dialog.dialogCallbacks = callbacks;

        // set arguments
        if (book != null) {
            Bundle args = new Bundle();
            if (book.getName() != null) {
                args.putString(KEY_NAME, book.getName());
            }
            if (book.getComment() != null) {
                args.putString(KEY_COMMENT, book.getComment());
            }
            args.putInt(KEY_COLOR, book.getColor());
            dialog.setArguments(args);
        }

        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            mName = args.getString(KEY_NAME, "");
            mComment = args.getString(KEY_COMMENT, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_edit_book_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mEditName = (EditText)view.findViewById(R.id.editName);
        mEditName.setText(mName);

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

        args.putString(KEY_NAME, mEditName.getText().toString());
        args.putString(KEY_COMMENT, mEditName.getText().toString());

        if (dialogCallbacks != null) {
            dialogCallbacks.submitEditBook(args);
        }

        dismiss();
    }

    /**
     * キャンセルしたときの処理
     */
    private void cancel() {
        if (dialogCallbacks != null) {
            dialogCallbacks.cancelEditBook();
        }
        dismiss();
    }

    /**
     * ランダム値を設定
     */
    private void setRandomValue() {
        Random rand = new Random();
        int value = rand.nextInt(1000);

        mEditName.setText("N " + value);
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