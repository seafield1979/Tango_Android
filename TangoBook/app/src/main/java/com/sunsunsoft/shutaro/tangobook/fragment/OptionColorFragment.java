package com.sunsunsoft.shutaro.tangobook.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.uview.ColorView;


/**
 * オプションの色設定DialogFragment
 */
public class OptionColorFragment extends DialogFragment
        implements View.OnClickListener {

    /**
     * Enums
     */
    public enum ColorMode {
        Book,
        Card
    }
    /**
     * Constract
     */
    public static final String TAG = "OptionColorFragment";

    // key names
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
    private OptionColorDialogCallbacks dialogCallbacks;
    private int mColor;
    private ColorMode mMode;

    // 選択された色を表示するView
    private ColorView mColorView;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public static OptionColorFragment createInstance(OptionColorDialogCallbacks callbacks,
                                              ColorMode mode)
    {
        OptionColorFragment dialog = new OptionColorFragment();

        dialog.dialogCallbacks = callbacks;
        dialog.mMode = mode;

        // set arguments
        Bundle args = new Bundle();

        String keyName = (mode == ColorMode.Book) ? MySharedPref.DefaultColorBookKey : MySharedPref.DefaultColorCardKey;

        args.putInt(KEY_COLOR, MySharedPref.readInt(keyName));
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            mColor = args.getInt(KEY_COLOR, 0xff000000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_option_color_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mColorView = (ColorView)view.findViewById(R.id.current_color);
        mColorView.setColor(mColor);

        // title text
        int messageId;
        if (mMode == ColorMode.Book) {
            messageId = R.string.default_book_color_message;
        } else {
            messageId = R.string.default_card_color_message;
        }

        ((TextView)(view.findViewById(R.id.textView))).setText(UResourceManager.getStringById
                (messageId));

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

        args.putInt(KEY_COLOR, mColorView.getColor());

        MySharedPref.writeInt(
                (mMode == ColorMode.Book) ? MySharedPref.DefaultColorBookKey : MySharedPref.DefaultColorCardKey,
                mColorView.getColor());

        if (dialogCallbacks != null) {
            dialogCallbacks.submitOptionColor(args);
        }

        dismiss();
    }

    /**
     * キャンセルしたときの処理
     */
    private void cancel() {
        if (dialogCallbacks != null) {
            dialogCallbacks.cancelOptionColor();
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