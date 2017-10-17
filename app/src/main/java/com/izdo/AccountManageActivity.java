package com.izdo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.izdo.Adapter.MyAccountAdapter;
import com.izdo.DataBase.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AccountManageActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout accountManage;
    private Button accountAdd;
    private ListView mListView;
    private List<String> accountList;
    private MyAccountAdapter mAccountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);

        init();
    }

    private void init() {
        accountManage = (LinearLayout) findViewById(R.id.account_manage);
        accountAdd = (Button) findViewById(R.id.account_add);
        mListView = (ListView) findViewById(R.id.account_manage_listView);

        accountList = new ArrayList<>();

        accountManage.setOnClickListener(this);
        accountAdd.setOnClickListener(this);


        queryAccount();
        mAccountAdapter = new MyAccountAdapter(this, accountList);
        mListView.setAdapter(mAccountAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AccountManageActivity.this, EditAccountActivity.class);
                intent.putExtra("account_name", accountList.get(i));
                startActivity(intent);
            }
        });
    }

    private void queryAccount() {

        accountList.clear();

        Cursor cursor = MyDatabaseHelper.getInstance(this).query("Account", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String account = cursor.getString(cursor.getColumnIndex("account"));
            accountList.add(account);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryAccount();
        mAccountAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_manage:
                finish();
                break;
            case R.id.account_add:
                startActivity(new Intent(AccountManageActivity.this, AddAccountActivity.class));
                break;
            default:
                break;
        }
    }
}
