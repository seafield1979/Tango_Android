package com.sunsunsoft.shutaro.testdb;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class TBookDialogFragment extends DialogFragment implements View.OnClickListener {
    private final static String KEY_NAME = "key_name";
    private final static String KEY_COLOR = "key_color";
    private final static String KEY_COMMENT = "key_comment";

    public static final String KEY_RET_NAME = "key_ret_name";
    public static final String KEY_RET_COLOR = "key_ret_color";
    public static final String KEY_RET_COMMENT = "key_ret_comment";

    private static final int[] buttonIds = new int[]{
            R.id.buttonOK,
            R.id.buttonRandom,
            R.id.buttonName,
            R.id.buttonColor,
            R.id.buttonComment
    };
    private OnOkClickListener mListener;

    private EditText mEditName;
    private EditText mEditColor;
    private EditText mEditComment;

    private String mName, mComment;
    private int mColor;

    Random rand = new Random();

    /**
     * Create a new instance of TBookDialogFragment, providing "num"
     * as an argument.
     */
    static TBookDialogFragment createInstance(TangoBook book) {
        TBookDialogFragment fragment = new TBookDialogFragment();

        // set arguments
        if (book != null) {
            Bundle args = new Bundle();
            args.putString(KEY_NAME, book.getName());
            args.putInt(KEY_COLOR, book.getColor());
            args.putString(KEY_COMMENT, book.getComment());

            fragment.setArguments(args);
        }

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
            mName = getArguments().getString(KEY_NAME);
            mColor = getArguments().getInt(KEY_COLOR);
            mComment = getArguments().getString(KEY_COMMENT);
        }

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tbook_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Viewとメンバ変数紐付け
        mEditName = (EditText)view.findViewById(R.id.editName);
        mEditName.setText(mName);

        mEditColor = (EditText)view.findViewById(R.id.editColor);
        mEditColor.setText(String.format("#%06x", mColor));

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
        arg.putString(KEY_RET_NAME, mEditName.getText().toString());
        arg.putInt(KEY_RET_COLOR, convStrToInt(mEditColor.getText().toString()));
        arg.putString(KEY_RET_COMMENT, mEditComment.getText().toString());

        mListener.onOkClicked(arg);
        dismiss();
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
            case R.id.buttonName:
                mEditName.setText("Name " + randVal);
                break;
            case R.id.buttonColor:
                mEditColor.setText(getRandomColorStr());
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

        mEditName.setText("Name " + randVal);
        mEditColor.setText(getRandomColorStr());
        mEditComment.setText("Comment " + randVal);
    }

    private int getRandomColor() {
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return (r << 16) | (g << 8) | b;
    }

    private String getRandomColorStr() {
        int color = getRandomColor();
        return String.format("#%06x", color);
    }

    private int convStrToInt(String intStr) {
        // 先頭が # なら取り除く
        int val = 0;
        Log.d("mylog", intStr.substring(0,1));
        if (intStr.substring(0,1).equals("#")) {
            intStr = intStr.substring(1);
        }
        if (intStr.length() < 6) return 0;

        int color = (hex2int(intStr.substring(0, 2)) << 16) |
                (hex2int(intStr.substring(2, 4)) << 8) |
                hex2int(intStr.substring(4, 6));
        Log.d("mylog", "" + color);

        return (hex2int(intStr.substring(0, 2)) << 16) |
                (hex2int(intStr.substring(2, 4)) << 8) |
                hex2int(intStr.substring(4, 6));
    }

    private int hex2int(String s){
        int v=0;
        try {
            v=Integer.parseInt(s,16);
        }catch (Exception e){
            v=0;
        }
        return v;
    }
}