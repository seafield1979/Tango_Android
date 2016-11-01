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
 * ListViewに表示するためのアダプター
 */
public class TangoBookAdapter extends ArrayAdapter<TangoBook> {
    private LayoutInflater mLayoutInflater;

    public TangoBookAdapter(Context context, int textViewResourceId,
                            List<TangoBook> objects)
    {
        super(context, textViewResourceId, objects);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TangoBook item = getItem(position);

        if (null == convertView) {
            convertView = mLayoutInflater.inflate(
                    R.layout.tbook_list_item, null);
            Log.v("myData", String.valueOf(position));
        }

        // ViewにTangoBookの情報を設定
        TextView textView = (TextView) convertView.findViewById(R.id.textView1);
        textView.setText(item.getName());

        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
        textView2.setText(item.getComment());

        convertView.setBackgroundColor(MyColor.addAlpha(item.getColor(), 255));

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