package com.sunsunsoft.shutaro.testdb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

public class TCardUpdateActivity extends AppCompatActivity implements OnClickListener{
    int[] buttonIds = new int[]{
            R.id.buttonOK,
            R.id.buttonRandom,
            R.id.buttonWordA,
            R.id.buttonWordB,
            R.id.buttonHintAB,
            R.id.buttonHintBA,
            R.id.buttonComment
    };
    private int mode = 0;

    EditText editWordA;
    EditText editWordB;
    EditText editHintAB;
    EditText editHintBA;
    EditText editComment;

    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcard_update);

        editWordA = (EditText)findViewById(R.id.editWordA);
        editWordB = (EditText)findViewById(R.id.editWordB);
        editHintAB = (EditText)findViewById(R.id.editHintAB);
        editHintBA = (EditText)findViewById(R.id.editHintBA);
        editComment = (EditText)findViewById(R.id.editComment);

        // buttons
        for (int id : buttonIds) {
            Button button = (Button)findViewById(id);
            if (button != null) {
                button.setOnClickListener(this);
            }
        }

        // 引数を取得する
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOK:
            {
                createCard();
            }
                break;
            case R.id.buttonRandom:
                setRandomAll();
                break;
            case R.id.buttonWordA:
                editWordA.setText("wordA_" + rand.nextInt(1000));
                break;
            case R.id.buttonWordB:
                editWordA.setText("wordB_" + rand.nextInt(1000));
                break;
            case R.id.buttonHintAB:
                editWordA.setText("hintAB_" + rand.nextInt(1000));
                break;
            case R.id.buttonHintBA:
                editWordA.setText("hintBA_" + rand.nextInt(1000));
                break;
            case R.id.buttonComment:
                editWordA.setText("comment_" + rand.nextInt(1000));
                break;
        }
    }

    public void createCard() {
        Intent data = new Intent();
        data.putExtra("wordA", editWordA.getText().toString());
        data.putExtra("wordB", editWordB.getText().toString());
        data.putExtra("hintAB", editHintAB.getText().toString());
        data.putExtra("hintBA", editHintBA.getText().toString());
        data.putExtra("comment", editComment.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    public void setRandomAll() {
        int randVal = rand.nextInt(1000);
        editWordA.setText("wordA_" + randVal);
        editWordB.setText("wordB_" + randVal);
        editHintAB.setText("hintAB" + randVal);
        editHintBA.setText("hintBA" + randVal);
        editComment.setText("comment" + randVal);
    }
}
