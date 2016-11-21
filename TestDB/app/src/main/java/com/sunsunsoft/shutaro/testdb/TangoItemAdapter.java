package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * TangoItemInBoxをListViewに表示するためのAdapter
 */

public class TangoItemAdapter extends ArrayAdapter<TangoItemInBoxList> {
    private LayoutInflater mLayoutInflater;

    public TangoItemAdapter(Context context, int textViewResourceId,
                            List<TangoItemInBoxList> objects)
    {
        super(context, textViewResourceId, objects);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TangoItemInBoxList item = getItem(position);

        if (null == convertView) {
            convertView = mLayoutInflater.inflate(
                    R.layout.tbook_list_item, null);
            Log.v("myData", String.valueOf(position));
        }

        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.list_top);


        // ViewにTangoBookの情報を設定
        TextView textView = (TextView) convertView.findViewById(R.id.textView1);
        textView.setText(item.getName());

        if (item.getType() == TangoItemType.Card) {
            convertView.setBackgroundColor(Color.rgb(255,100,0));
        } else {
            convertView.setBackgroundColor(Color.rgb(0,100,255));
        }

        // チェックボックスにイベントリスナを登録
        CheckBox check = (CheckBox)convertView.findViewById(R.id.checkBox);
        final int p = position;
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Log.d("TangoBookAdapter", "pos:" + p);
                item.setChecked(isChecked);
            }
        });
        return convertView;
    }
}