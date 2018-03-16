package com.izdo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.izdo.R;
import com.izdo.adapter.AccountAdapter;
import com.izdo.dataBase.DatabaseHelper;
import com.izdo.util.InitData;

import java.util.ArrayList;
import java.util.List;

public class AccountManageActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout accountManage;
    private Button accountAdd;
    private ListView mListView;
    private List<String> accountList;
    private AccountAdapter mAccountAdapter;

    // 是否有预设账户
    private boolean isPreset;

    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);

        init();
    }

    private void init() {
        mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();

        accountManage = (LinearLayout) findViewById(R.id.account_manage);
        accountAdd = (Button) findViewById(R.id.account_add);
        mListView = (ListView) findViewById(R.id.account_manage_listView);

        accountList = new ArrayList<>();

        accountManage.setOnClickListener(this);
        accountAdd.setOnClickListener(this);


        queryAccount();
        mAccountAdapter = new AccountAdapter(this, accountList);
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
        isPreset = false;

        accountList.clear();

        Cursor cursor = DatabaseHelper.getInstance(this).query("Account", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String accountName = cursor.getString(cursor.getColumnIndex("account"));
            if (accountName.equals(InitData.preset)) {
                accountName += "(预设)";
                isPreset = true;
            }
            accountList.add(accountName);
        }

        if (!isPreset) {
            String firstAccount = accountList.get(0);
            InitData.preset = firstAccount;
            mEditor.putString("preset", firstAccount);
            mEditor.commit();
            firstAccount += "(预设)";
            accountList.set(0, firstAccount);
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
