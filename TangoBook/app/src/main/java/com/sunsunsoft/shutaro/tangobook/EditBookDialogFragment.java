package com.sunsunsoft.shutaro.tangobook;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.view.View.OnClickListener;
import java.util.Random;


interface EditBookDialogCallbacks {
    void submitEditBook(Bundle args);
    void cancelEditBook();
}

enum EditBookDialogMode {
    Create,     // 新しくアイコンを作成する
    Edit        // 既存のアイコンを編集する
    ;

    public static EditBookDialogMode toEnum(int value) {
        for (EditBookDialogMode id : values()) {
            if (id.ordinal() == value) {
                return id;
            }
        }
        return EditBookDialogMode.Create;
    }
}

/**
 * 複数の入力項目があるダイアログ
 *
 * 全画面表示
 */
public class EditBookDialogFragment extends DialogFragment implements OnClickListener{
    /**
     * Constract
     */
    public static final String TAG = "EditBookDialogFragment";

    // key names
    public static final String KEY_MODE = "key_mode";
    public static final String KEY_NAME = "key_name";
    public static final String KEY_COMMENT = "key_comment";
    public static final String KEY_COLOR = "key_color";

    private int[] colorViewIds = {
            R.id.current_color,
            R.id.color_view_2,
            R.id.color_view_3,
            R.id.color_view_4,
            R.id.color_view_5,
            R.id.color_view_6,
            R.id.color_view_7,
            R.id.color_view_8,
            R.id.color_view_9,
            R.id.color_view_10,
            R.id.color_view_11,
            R.id.color_view_12,
            R.id.color_view_13,

    };

    /**
     * Member variables
     */
    private EditBookDialogCallbacks dialogCallbacks;
    private int mMode;

    private String mName;
    private String mComment;
    private int mColor;

    private EditText mEditName;
    private EditText mEditComment;

    // 選択された色を表示するView
    private ColorView mColorView;

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
        Bundle args = new Bundle();

        if (book != null) {
            args.putInt(KEY_MODE, EditCardDialogMode.Edit.ordinal());

            if (book.getName() != null) {
                args.putString(KEY_NAME, book.getName());
            }
            if (book.getComment() != null) {
                args.putString(KEY_COMMENT, book.getComment());
            }
            args.putInt(KEY_COLOR, book.getColor());
            dialog.setArguments(args);
        } else {
            args.putInt(KEY_MODE, EditBookDialogMode.Create.ordinal());
        }

        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            mMode = args.getInt(KEY_MODE, EditBookDialogMode.Create.ordinal());
            mName = args.getString(KEY_NAME, "");
            mComment = args.getString(KEY_COMMENT, "");
            mColor = args.getInt(KEY_COLOR, 0xff000000);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        } else {
            setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
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

//        mEditComment = (EditText)view.findViewById(R.id.editComment);
//        mEditComment.setText(mComment);

        mColorView = (ColorView)view.findViewById(R.id.current_color);
        mColorView.setColor(mColor);

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

//        (view.findViewById(R.id.buttonRandom)).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                setRandomValue();
//            }
//        });

        for (int id : colorViewIds) {
            view.findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ColorView) {
            ColorView colorView = (ColorView)v;
            mColorView.setColor(colorView.getColor());
            mColorView.invalidate();
        }
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        Bundle args = new Bundle();

        args.putInt(KEY_MODE, mMode);
        args.putString(KEY_NAME, mEditName.getText().toString());
        args.putString(KEY_COMMENT, mEditComment.getText().toString());
        args.putInt(KEY_COLOR, mColorView.getColor());

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