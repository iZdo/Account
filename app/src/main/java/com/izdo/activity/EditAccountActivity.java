package com.izdo.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.izdo.R;
import com.izdo.dataBase.DatabaseHelper;
import com.izdo.util.InitData;
import com.izdo.util.MyDialog;

/**
 * Created by iZdo on 2017/10/15.
 */


public class EditAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout editAccount;
    private Button accountSave;
    private EditText accountEditText;
    private Button accountDelete;
    private MyDialog myDialog;
    private boolean isExist = false;
    private String accountName;
    // 是否是预设账户
    private boolean isPreset = false;

    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        init();
    }

    private void init() {
        mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();

        editAccount = (LinearLayout) findViewById(R.id.edit_account);
        accountSave = (Button) findViewById(R.id.account_save);
        accountEditText = (EditText) findViewById(R.id.account_edittext);
        accountDelete = (Button) findViewById(R.id.account_delete);

        accountName = getIntent().getStringExtra("account_name");
        if (accountName.endsWith("(预设)")) {
            accountName = accountName.substring(0, accountName.length() - 4);
            isPreset = true;
        }
        accountEditText.setText(accountName);

        accountEditText.setSelection(accountEditText.getText().toString().length());

        myDialog = new MyDialog(EditAccountActivity.this, R.style.dialog_style);
        myDialog.setCancelable(false);

        editAccount.setOnClickListener(this);
        accountSave.setOnClickListener(this);
        accountDelete.setOnClickListener(this);
    }

    // 删除记录
    private void deleteData() {
        DatabaseHelper.getInstance(this).delete("Account", "account=?", new String[]{accountEditText.getText().toString()});
    }

    // 更新记录
    private void updateData() {
        ContentValues values = new ContentValues();

        values.put("account", accountEditText.getText().toString());
        DatabaseHelper.getInstance(this).update("Account", values, "account=?", new String[]{getIntent().getStringExtra("account_name")});
    }

    private void queryData() {
        // 查询账号名是否已存在
        if (DatabaseHelper.getInstance(EditAccountActivity.this).query("Account", null, "account=?", new String[]{accountEditText.getText().toString()}, null, null, null).getCount() > 0) {
            isExist = true;
            myDialog.initConfirmDialog("此账户已存在!");
            myDialog.show();
            myDialog.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });
        }

        isExist = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_account:
                if (!accountEditText.getText().toString().equals(getIntent().getStringExtra("account_name"))) {
                    myDialog.initSelectDialog("数据未保存,确定退出？");
                    myDialog.show();
                    myDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                            finish();
                        }
                    });
                    myDialog.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                        }
                    });
                } else
                    finish();
                break;
            case R.id.account_save:
                queryData();

                if (isExist)
                    return;

                updateData();

                if (!isPreset) {
                    myDialog.initSelectDialog("是否设定为预设账户?");
                    myDialog.show();
                    myDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                            mEditor.putString("preset", accountName);
                            mEditor.commit();
                            InitData.preset = accountName;
                            finish();
                        }
                    });
                    myDialog.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                            finish();
                        }
                    });
                } else
                    finish();
                break;
            case R.id.account_delete:
                myDialog.initSelectDialog("确定删除此账户?");
                myDialog.show();
                myDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteData();
                        myDialog.dismiss();
                        finish();
                    }
                });
                myDialog.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }
}

