package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shutaro on 2017/01/22.
 *
 * ListViewに表示する項目をカスタマイズするためのクラス
 * getViewがカスタマイズされたViewを返す
 */
public class HelpListAdapter extends ArrayAdapter<HelpListItemData> {
    private LayoutInflater mLayoutInflater;

    public HelpListAdapter(Context context, int textViewResourceId,
                         List<HelpListItemData> objects)
    {
        super(context, textViewResourceId, objects);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HelpListItemData item = getItem(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(
                    R.layout.list_item_help, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        textView.setText(item.getmText());
        textView.setTextColor(item.getTextColor());


        convertView.setBackgroundColor(item.getBgColor());


        return convertView;
    }
}
