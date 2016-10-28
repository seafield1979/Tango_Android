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
 * Created by shutaro on 2016/10/28.
 */

public class TangoCardAdapter extends ArrayAdapter<TangoCard> {
    private LayoutInflater mLayoutInflater;

    public TangoCardAdapter(Context context, int textViewResourceId,
                          List<TangoCard> objects)
    {
        super(context, textViewResourceId, objects);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TangoCard item = getItem(position);

        if (null == convertView) {
            convertView = mLayoutInflater.inflate(
                    R.layout.tcard_list_item, null);
            Log.v("myData", String.valueOf(position));
        }

        // ViewにTangoCardの情報を設定
        TextView textView = (TextView) convertView.findViewById(R.id.textView1);
        textView.setText(item.getWordA());

        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
        textView2.setText(item.getWordB());

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
                Log.d("contactAdapter", "pos:" + p);
                item.setChecked(isChecked);
            }
        });
        return convertView;
    }
}