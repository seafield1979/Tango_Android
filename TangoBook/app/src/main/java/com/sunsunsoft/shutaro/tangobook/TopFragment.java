package com.sunsunsoft.shutaro.tangobook;


import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


/**
 * エディットのコールバック
 */
interface EditTextCallbacks {
    /**
     * 編集前イベント
     */
    void beforeTextChanged(String str, int start, int count, int after);
    /**
     * 編集後イベント
     */
    void onTextChanged(String str, int start, int before, int count);
    /**
     * 編集確定後イベント
     */
    void afterTextChanged(String str);
}

public class TopFragment extends Fragment implements OnClickListener, OnTouchListener{

    /**
     * Constract
     */
    public final static String TAG = "TopFragment";
    private final static String BACKGROUND_COLOR = "background_color";

    private static TopFragment instance;


    /**
     * Member variables
     */
    private TopView topView;

    // 検索Editを内包するLayout
    // 通常は非表示で検索時に飲み表示される
    private LinearLayout editLayout;

    private EditText mEditText;

    // エディットが変更された時のコールバック通知先
    private EditTextCallbacks mEditTextCallback;

    /**
     * Get/Set
     */
    public static TopFragment getInstance() {
        return instance;
    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setEditTextCallback(EditTextCallbacks callbackObj) {
        mEditTextCallback = callbackObj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, null);

        //UResourceManager.createInstance(getContext(), view);
        UResourceManager.getInstance().setView(view);

        // 検索用のEdit
        editLayout = (LinearLayout)view.findViewById(R.id.edit_container);
        mEditText = (EditText)view.findViewById(R.id.editText);
        mEditText.addTextChangedListener(watchHandler);
        showEditLayout(false);

        // Viewを追加
        topView = new TopView(getContext());
        LinearLayout containerView = (LinearLayout)view.findViewById(R.id.view_container);
        containerView.addView(topView);

        return view;
    }

    /**
     * EditTextの入力情報をリアルタイムで取得する
     */
    private TextWatcher watchHandler = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mEditTextCallback != null) {
                mEditTextCallback.beforeTextChanged(s.toString(), start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditTextCallback != null) {
                mEditTextCallback.onTextChanged(s.toString(), start, count, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEditTextCallback != null) {
                mEditTextCallback.afterTextChanged(s.toString());
            }
        }
    };

    public void onClick(View v) {
    }

    public boolean onTouch(View v, MotionEvent e) {
        return true;
    }

    /**
     * エディットレイアウトの表示切り替え
     * @param show
     */
    public void showEditLayout(boolean show) {
        if (show) {
            mEditText.setText("");
            mEditText.requestFocus();
            editLayout.setVisibility(View.VISIBLE);

            // ソフトウェアキーボードを表示する
            InputMethodManager inputMethodManager
                    = (InputMethodManager)getContext().getSystemService(getContext()
                    .INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(mEditText, 0);
        } else {
            editLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 戻るボタンが押された時の処理
     */
    public boolean onBackKeyDown() {
        if (topView.onBackKeyDown()) {
            topView.invalidate();
            return true;
        }
        return false;
    }
}
