package com.sunsunsoft.shutaro.testdb;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.Random;

/**
 * ダイアログ用のFragmentサンプル
 * DialogFragmentのサブクラス
 */
public class TCardDialogFragment extends DialogFragment implements OnClickListener{

    private final static String KEY_WORD_A = "key_wordA";
    private final static String KEY_WORD_B = "key_wordB";
    private final static String KEY_HINT_AB = "key_hint_AB";
    private final static String KEY_HINT_BA = "key_hint_BA";
    private final static String KEY_COMMENT = "key_comment";

    public static final String KEY_RET_WORD_A = "key_ret_wordA";
    public static final String KEY_RET_WORD_B = "key_ret_wordB";
    public static final String KEY_RET_HINT_AB = "key_ret_hint_AB";
    public static final String KEY_RET_HINT_BA = "key_ret_hint_BA";
    public static final String KEY_RET_COMMENT = "key_ret_comment";

    private static final int[] buttonIds = new int[]{
            R.id.buttonOK,
            R.id.buttonRandom,
            R.id.buttonWordA,
            R.id.buttonWordB,
            R.id.buttonHintAB,
            R.id.buttonHintBA,
            R.id.buttonComment
    };
    private OnOkClickListener mListener;

    private EditText mEditWordA;
    private EditText mEditWordB;
    private EditText mEditHintAB;
    private EditText mEditHintBA;
    private EditText mEditComment;

    private String mWordA, mWordB, mHintAB, mHintBA, mComment;

    Random rand = new Random();

    /**
     * Create a new instance of TCardDialogFragment, providing "num"
     * as an argument.
     */
    static TCardDialogFragment createInstance(TangoCard card) {
        TCardDialogFragment fragment = new TCardDialogFragment();

        // set arguments
        Bundle args = new Bundle();
        if (card != null) {
            args.putString(KEY_WORD_A, card.getWordA());
            args.putString(KEY_WORD_B, card.getWordB());
            args.putString(KEY_HINT_AB, card.getHintAB());
            args.putString(KEY_HINT_BA, card.getHintBA());
            args.putString(KEY_COMMENT, card.getComment());
        } else {
            // ランダム
            int randVal = new Random().nextInt(1000);
            args.putString(KEY_WORD_A, "WordA " + randVal);
            args.putString(KEY_WORD_B, "WordB " + randVal);
            args.putString(KEY_HINT_AB, "HintAB " + randVal);
            args.putString(KEY_HINT_BA, "HintBA " + randVal);
            args.putString(KEY_COMMENT, "Comment " + randVal);
        }
        fragment.setArguments(args);

        return fragment;
    }

    public interface OnOkClickListener {
        void onOkClicked(Bundle args);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            Fragment targetFragment = this.getTargetFragment();
            if (targetFragment == null) {
                mListener = (OnOkClickListener)context;
            } else {
                mListener = (OnOkClickListener) targetFragment;
            }
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Don't implement OnCustomDialogListener.");
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            mWordA = getArguments().getString(KEY_WORD_A);
            mWordB = getArguments().getString(KEY_WORD_B);
            mHintAB = getArguments().getString(KEY_HINT_AB);
            mHintBA = getArguments().getString(KEY_HINT_BA);
            mComment = getArguments().getString(KEY_COMMENT);
        }

        setMyStyle(3,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tcard_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Viewとメンバ変数紐付け
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

        // Buttons
        for (int id : buttonIds) {
            ((Button)view.findViewById(id)).setOnClickListener(this);
        }
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        Bundle arg = new Bundle();
        arg.putString(KEY_RET_WORD_A, mEditWordA.getText().toString());
        arg.putString(KEY_RET_WORD_B, mEditWordB.getText().toString());
        arg.putString(KEY_RET_HINT_AB, mEditHintAB.getText().toString());
        arg.putString(KEY_RET_HINT_BA, mEditHintBA.getText().toString());
        arg.putString(KEY_RET_COMMENT, mEditComment.getText().toString());

        mListener.onOkClicked(arg);
        dismiss();
    }

    /**
     * DialogFragmentのスタイルを設定する
     *
     * @param _style
     * @param _theme
     */
    private void setMyStyle(int _style, int _theme) {
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch (_style) {
            case 0: style = DialogFragment.STYLE_NO_TITLE; break;
            case 1: style = DialogFragment.STYLE_NO_FRAME; break;
            case 2: style = DialogFragment.STYLE_NO_INPUT; break;
            case 3: style = DialogFragment.STYLE_NORMAL; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NO_TITLE; break;
            case 6: style = DialogFragment.STYLE_NO_FRAME; break;
            case 7: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch (_theme) {
            case 0: theme = android.R.style.Theme_Holo; break;
            case 1: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 2: theme = android.R.style.Theme_Holo_Light; break;
            case 3: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 4: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);
    }

    public void onClick(View v) {

        int randVal = rand.nextInt(1000);
        switch(v.getId()) {
            case R.id.buttonOK:
                submit();
                break;
            case R.id.buttonRandom:
                setRandomAll();
                break;
            case R.id.buttonWordA:
                mEditWordA.setText("WordA " + randVal);
                break;
            case R.id.buttonWordB:
                mEditWordB.setText("WordB " + randVal);
                break;
            case R.id.buttonHintAB:
                mEditHintAB.setText("HintAB " + randVal);
                break;
            case R.id.buttonHintBA:
                mEditHintBA.setText("HintBA " + randVal);
                break;
            case R.id.buttonComment:
                mEditComment.setText("Comment " + randVal);
                break;
        }
    }



    /**
     * 全ての編集項目にランダムな値を設定する
     */
    private void setRandomAll() {
        int randVal = rand.nextInt(1000);

        mEditWordA.setText("WordA " + randVal);
        mEditWordB.setText("WordB " + randVal);
        mEditHintAB.setText("HintAB " + randVal);
        mEditHintBA.setText("HintBA " + randVal);
        mEditComment.setText("Comment " + randVal);
    }
}