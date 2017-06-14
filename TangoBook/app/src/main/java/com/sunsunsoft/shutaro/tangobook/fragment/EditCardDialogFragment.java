package com.sunsunsoft.shutaro.tangobook.fragment;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import java.util.Random;
import android.view.View.OnClickListener;

import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.ColorView;



/**
 * 複数の入力項目があるダイアログ
 *
 * 全画面表示
 */
public class EditCardDialogFragment extends DialogFragment implements OnClickListener {
    /**
     * Enums
     */
    /**
     * Constract
     */
    // key names
    public static final String KEY_MODE = "key_mode";
    public static final String KEY_WORD_A = "key_word_a";
    public static final String KEY_WORD_B = "key_word_b";
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
    private EditCardDialogCallbacks dialogCallbacks;
    private int mMode;
    private EditText mEditWordA;
    private EditText mEditWordB;
    private EditText mEditComment;
    // 選択された色を表示するView
    private ColorView mColorView;

    private String mWordA;
    private String mWordB;
    private String mComment;
    private int mColor;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public static EditCardDialogFragment createInstance(EditCardDialogCallbacks callbacks) {
        return createInstance(callbacks, null);
    }

    public static EditCardDialogFragment createInstance(EditCardDialogCallbacks callbacks, TangoCard card) {
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
            if (card.getComment() != null) {
                args.putString(KEY_COMMENT, card.getComment());
            }
            args.putInt(KEY_COLOR, card.getColor());

        } else {
            args.putInt(KEY_MODE, EditCardDialogMode.Create.ordinal());
            args.putInt(KEY_COLOR, MySharedPref.readInt(MySharedPref.DefaultColorCardKey, UColor.BLACK));
            // デフォルトのカード名が設定されれていたらそれを使用する
            String defaultWordA = MySharedPref.readString(MySharedPref.DefaultCardWordAKey);
            if (defaultWordA != null) {
                args.putString(KEY_WORD_A, defaultWordA);
            }
            String defaultWordB = MySharedPref.readString(MySharedPref.DefaultCardWordBKey);
            if (defaultWordB != null) {
                args.putString(KEY_WORD_B, defaultWordB);
            }
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
            mComment = args.getString(KEY_COMMENT, "");
            mColor = args.getInt(KEY_COLOR, 0);
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

        View view = inflater.inflate(R.layout.fragment_edit_card_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditWordA = (EditText)view.findViewById(R.id.editWordA);
        mEditWordA.setText(mWordA);

        mEditWordB = (EditText)view.findViewById(R.id.editWordB);
        mEditWordB.setText(mWordB);

        mEditComment = (EditText)view.findViewById(R.id.editComment);
        mEditComment.setText(mComment);

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
        args.putString(KEY_WORD_A, mEditWordA.getText().toString());
        args.putString(KEY_WORD_B, mEditWordB.getText().toString());
        args.putString(KEY_COMMENT, mEditComment.getText().toString());
        args.putInt(KEY_COLOR, mColorView.getColor());

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