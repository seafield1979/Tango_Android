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
import android.widget.TextView;

import java.util.List;

/**
 * TangoListItemFragmentのListView用のAdapter
 */

public class TangoListItemAdapter extends ArrayAdapter<TangoListItem> {
    private LayoutInflater mLayoutInflater;

    public TangoListItemAdapter(Context context, int textViewResourceId,
                            List<TangoListItem> objects)
    {
        super(context, textViewResourceId, objects);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TangoListItem item = getItem(position);

        if (null == convertView) {
            convertView = mLayoutInflater.inflate(
                    R.layout.tlist_item, null);
            Log.v("myData", String.valueOf(position));
        }

        // ViewにTangoListItemの情報を設定
        TextView textView = (TextView) convertView.findViewById(R.id.textView1);
        textView.setText("pos:" + item.getPos() + " id:" + item.getId() + " type:" + item.getItemType
                ());

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.rgb(200, 100, 0));
        } else {
            convertView.setBackgroundColor(Color.rgb(255,255,255));
        }

        // チェックボックスにイベントリスナを登録
        CheckBox check = (CheckBox)convertView.findViewById(R.id.checkBox);
        final int p = position;
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                item.setChecked(isChecked);
            }
        });
        return convertView;
    }
}
