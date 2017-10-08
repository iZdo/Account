package com.izdo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DescribeActivity extends AppCompatActivity {

    private LinearLayout newDescribe;
    private Button describeSave;
    private EditText describeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe);

        newDescribe = (LinearLayout) findViewById(R.id.new_describe);
        describeSave = (Button) findViewById(R.id.describe_save);
        describeEdit = (EditText) findViewById(R.id.describe_edit);
        String text = getIntent().getStringExtra("describe");
        describeEdit.setText(text);
        describeEdit.setSelection(text.length());

        newDescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        describeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("describe", describeEdit.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
