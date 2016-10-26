package com.sunsunsoft.shutaro.testdb;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.Toast;

import java.util.List;

public class MyFragment extends Fragment implements OnClickListener, OnItemClickListener {
    private final static String BACKGROUND_COLOR = "background_color";

    private ListView listView;
    private Button button1, button2, button3;
    private EditText nameEdit, idEdit;
    private TestContactDB testDB;

    public static MyFragment newInstance(@ColorRes int IdRes) {
        MyFragment frag = new MyFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, IdRes);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page1, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_page1_linearlayout);
        linearLayout.setBackgroundResource(getArguments().getInt(BACKGROUND_COLOR));

        testDB = new TestContactDB(getActivity());

        listView = (ListView)view.findViewById(R.id.listView);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        button1 = (Button)view.findViewById(R.id.button);
        button2 = (Button)view.findViewById(R.id.button2);
        button3 = (Button)view.findViewById(R.id.button3);
        nameEdit = (EditText)view.findViewById(R.id.editText);
        idEdit = (EditText)view.findViewById(R.id.editText2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        showList();
        return view;
    }

    /**
     * クリックイベント
     * @param v
     */
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                showList();
                break;
            case R.id.button2:
                addItem();
                break;
            case R.id.button3:
                updateItems();
                break;
            case R.id.button4:
                deleteItems();
                break;
        }
    }



    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ListView lv = (ListView)parent;
//        ContactData data = (ContactData)lv.getItemAtPosition(position);

//        Log.v("myLog", data.getText());
//        makeToast(data.getText(), 0, 0);

        ContactAdapter adapter = (ContactAdapter)listView.getAdapter();
        ContactData data = (ContactData)adapter.getItem(position);
        data.name = "hogehogehoge";
        adapter.notifyDataSetChanged();
//        CheckBox check = (CheckBox)view.findViewById(R.id.checkBox);
//        check.toggle();
//        Log.d("page1", "check:" + check.isChecked());

    }

    private void addItem() {
        String name = nameEdit.getText().toString();
        String ageStr = idEdit.getText().toString();
        if (ageStr == null || ageStr.length() == 0 ||
                name == null || name.length() == 0)
        {
            makeToast("couldn't add", 0, 0);
            return;
        }

        int age = Integer.parseInt(ageStr);
        testDB.addData(name, age);

        List<ContactData> list = testDB.getAllData();
        ContactAdapter adapter = new ContactAdapter(getContext(), 0, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showList() {
        List<ContactData> list = testDB.getAllData();
        for (ContactData data : list) {
            Log.d("page1", data.name + " " + data.age);
        }
        ContactAdapter adapter = new ContactAdapter(getContext(), 0, list);
        listView.setAdapter(adapter);
    }

    public void updateItems() {
        ContactAdapter adapter = (ContactAdapter)listView.getAdapter();
        for (int i=0; i<adapter.getCount(); i++) {
            ContactData data = adapter.getItem(i);

            if (data.isChecked) {
                Log.d("page1", "data:" + data.name);
            }
        }
//        SparseBooleanArray checked = listView.getCheckedItemPositions();
//        for (int i = 0; i < checked.size(); i++) {
//            int at = checked.keyAt(i);
//            if (checked.get(at)) {
//                ContactData data = (ContactData)listView.getItemAtPosition(at);
//                Log.d("page1", "data:" + data.name);
//            }
//        }
//        for (long position : positions) {
//            ContactData data = (ContactData)listView.getItemAtPosition((int)position);
//            Log.d("page1", data.getText());
//        }
    }

    public void deleteItems() {
//        long[] positions = listView.getCheckedItemPositions();
//        for (long position : positions) {
//            ContactData data = (ContactData)listView.getItemAtPosition((int)position);
//            Log.d("page1", data.getText());
//        }
        Log.d("page1", "count:" + listView.getCheckedItemCount());
    }

    // Toast を表示する
    // x,y はデフォルトの表示位置(画面中央)からのオフセット
    private void makeToast(String message, int x, int y){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, x, y);
        toast.show();
    }
}
