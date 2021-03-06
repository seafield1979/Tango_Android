package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.view.View.OnClickListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/11/23.
 */

public class TItemPosDialogFragment extends DialogFragment implements OnClickListener {
    interface OnOkClickListener {
        void onOkClicked(Bundle args);
    }

    enum DialogMode {
        AddToHome,
        AddToTrash,
        DeleteFromHome,
        DeleteFromTrash,
        None
        ;
        public static DialogMode toEnum(int value) {
            if (value < values().length) {
                return values()[value];
            }
            return None;
        }
    }

    /**
     * Constant
     */
    public static final String KEY_MODE = "key_mode";
    public static final String KEY_BOX_ID = "key_box_id";
    public static final String KEY_RET = "key_ret";
    public static final int[] buttonIds = {
            R.id.buttonOK,
            R.id.buttonSelectAll,
            R.id.buttonCancel
    };

    /**
     * Member variable
     */
    TItemPosDialogFragment.DialogMode mMode;
    int mBoxId;

    private OnOkClickListener mListener;
    private View mContentView;
    private ListView mListView;
    private boolean allCheckSwitch;

    /**
     * Constructor
     */
    static TItemPosDialogFragment createInstance(TItemPosDialogFragment.DialogMode mode) {
        TItemPosDialogFragment fragment = new TItemPosDialogFragment();

        // set arguments
        Bundle args = new Bundle();
        args.putInt(KEY_MODE, mode.ordinal());

        fragment.setArguments(args);

        return fragment;
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
            DialogMode[] values = DialogMode.values();
            mMode = values[getArguments().getInt(KEY_MODE)];
            mBoxId = getArguments().getInt(KEY_BOX_ID);
        }

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_titem_pos_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContentView = view;
        // Buttons
        for (int id : buttonIds) {
            (view.findViewById(id)).setOnClickListener(this);
        }

        // ListView
        mListView = (ListView)view.findViewById(R.id.listView);
        switch (mMode) {
            case AddToHome:
                showAddables(TangoParentType.Home);
                break;
            case AddToTrash:
                showAddables(TangoParentType.Trash);
                break;
            case DeleteFromHome:
                showItems(TangoParentType.Home);
                break;
            case DeleteFromTrash:
                showItems(TangoParentType.Trash);
                break;
        }
    }

    /**
     * ボックスに未追加のカード番号を表示する
     */
    private void showAddables(TangoParentType parentType) {
        // すでに登録済みの TangoItemPos のリストを取得する
        List<TangoItemPos> itemsPoses = RealmManager.getItemPosDao()
                .selectItemPosesByParentType(parentType);
        if (itemsPoses == null) return;

        List<TangoItem> items = RealmManager.getItemPosDao()
                .selectItemExcludeItemPoses(itemsPoses, true);

        // アイテムのIDからカード、単語帳の情報を取得する
        TangoItemAdapter adapter = null;
        LinkedList<TangoItemInBoxList> itemsInBox = new LinkedList<>();

        for (TangoItem item : items) {
            TangoItemInBoxList itemInBox = new TangoItemInBoxList(
                    item.getItemType(),
                    item.getId(),
                    "type:" + item.getItemType()
                            + " id:" + item.getId()
                            + " pos:" + item.getPos()
            );
            itemsInBox.add(itemInBox);
        }

        adapter = new TangoItemAdapter(getContext(), 0, itemsInBox);

        mListView.setAdapter(adapter);
    }

    /**
     * 指定のボックスに含まれるアイテムを表示する
     */
    private void showItems(TangoParentType parentType) {
        List<TangoItem> items = RealmManager.getItemPosDao()
                .selectItemsByParentType(parentType, false);

        TangoItemAdapter adapter = null;
        LinkedList<TangoItemInBoxList> itemsInBox = new LinkedList<>();

        for (TangoItem item : items) {
            TangoItemInBoxList itemInBox = new TangoItemInBoxList(
                    item.getItemType(),
                    item.getId(),
                    "type:" + item.getItemType()
                            + " id:" + item.getId()
                            + " pos:" + item.getPos()
            );
            itemsInBox.add(itemInBox);
        }

        adapter = new TangoItemAdapter(getContext(), 0, itemsInBox);

        mListView.setAdapter(adapter);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonOK:
                submit();
                break;
            case R.id.buttonSelectAll:
                checkAllItems();
                break;
            case R.id.buttonCancel:
                dismiss();
                break;
        }
    }


    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        switch(mMode) {
            case AddToHome:
                addItems(TangoParentType.Home);
                break;
            case AddToTrash:
                addItems(TangoParentType.Trash);
                break;
            case DeleteFromHome:
                deleteItems(TangoParentType.Home);
                break;
            case DeleteFromTrash:
                deleteItems(TangoParentType.Trash);
                break;
        }
        if (mListener != null) {
            Bundle args = new Bundle();
            args.putInt(KEY_RET, mMode.ordinal());
            mListener.onOkClicked(args);
        }

        dismiss();
    }


    /**
     * 指定のParentTypeにチェックしたアイテムを追加する
     * @param parentType
     */
    private void addItems(TangoParentType parentType) {
        // チェックされた項目のIDを取得する
        List<TangoItemInBoxList> list = getCheckedItems();

        // TangoItemPosに追加するためのリストを作成する
        LinkedList<TangoItemPos> itemPoses = new LinkedList<>();

        for (TangoItemInBoxList item : list) {
            TangoItemPos itemPos = new TangoItemPos();
            itemPos.setParentType(parentType.ordinal());
            itemPos.setItemType(item.getType().ordinal());
            itemPos.setId(item.getItemId());

            itemPoses.add(itemPos);
        }

        // DBに追加
        RealmManager.getItemPosDao().addItemPoses(itemPoses);
    }

    /**
     * 指定のParentTypeからチェックしたアイテムを削除する
     * @param parentType
     */
    private void deleteItems(TangoParentType parentType) {
        // チェックされた項目のIDを取得する
        List<TangoItemInBoxList> list = getCheckedItems();

        // TangoItemPosに追加するためのリストを作成する
        LinkedList<TangoItemPos> itemPoses = new LinkedList<>();

        for (TangoItemInBoxList item : list) {
            TangoItemPos itemPos = new TangoItemPos();
            itemPos.setParentType(parentType.ordinal());
            itemPos.setItemType(item.getType().ordinal());
            itemPos.setId(item.getItemId());

            itemPoses.add(itemPos);
        }

        // DBから削除
        RealmManager.getItemPosDao().deleteItemPoses(itemPoses);
    }

    /**
     * チェックされた項目を取得する
     */
    protected List<TangoItemInBoxList> getCheckedItems() {
        // チェックされた項目のIDを取得する
        LinkedList<TangoItemInBoxList> list = new LinkedList<>();
        TangoItemAdapter adapter = (TangoItemAdapter) mListView.getAdapter();
        if (adapter == null) return null;

        for (int i = 0; i < adapter.getCount(); i++) {
            TangoItemInBoxList item = adapter.getItem(i);
            if (item.isChecked()) {
                list.add(item);
            }
        }

        return list;
    }


    /**
     * 全ての項目をチェックする
     */
    private void checkAllItems() {
        LinkedList<TangoItemInBoxList> list = new LinkedList<>();
        TangoItemAdapter adapter = (TangoItemAdapter) mListView.getAdapter();
        if (adapter == null) return;

        allCheckSwitch = !allCheckSwitch;


        for (int i = 0; i < mListView.getChildCount(); i++) {
            View view = mListView.getChildAt(i);

            CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);
            if (checkBox != null) {
                checkBox.setChecked(allCheckSwitch);
            }
        }
    }
}