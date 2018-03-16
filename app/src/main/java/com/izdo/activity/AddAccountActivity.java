package com.izdo.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.izdo.R;
import com.izdo.dataBase.DatabaseHelper;
import com.izdo.util.InitData;
import com.izdo.util.MyDialog;

public class AddAccountActivity extends AppCompatActivity {

    private LinearLayout addAccount;
    private Button addAccountSave;
    private EditText addAccountEdit;
    private MyDialog myDialog;

    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        init();
    }

    private void init() {
        mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();

        addAccount = (LinearLayout) findViewById(R.id.add_account);
        addAccountSave = (Button) findViewById(R.id.add_account_save);
        addAccountEdit = (EditText) findViewById(R.id.add_account_edit);

        myDialog = new MyDialog(AddAccountActivity.this, R.style.dialog_style);
        myDialog.setCancelable(false);

        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addAccountSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                final String accountName = addAccountEdit.getText().toString();

                if (TextUtils.isEmpty(accountName) || accountName.equals("\n") || accountName.equals(" ")) {
                    myDialog.initConfirmDialog("账户名不能为空!");
                    setDialogOnClickListener();
                    myDialog.show();
                } else {
                    values.put("account", accountName);

                    // 查询账号名是否已存在
                    if (DatabaseHelper.getInstance(AddAccountActivity.this).query("Account", null, "account=?", new String[]{accountName}, null, null, null).getCount() > 0) {
                        myDialog.initConfirmDialog("此账户已存在!");
                        setDialogOnClickListener();
                        myDialog.show();
                    } else {
                        DatabaseHelper.getInstance(AddAccountActivity.this).insert("Account", null, values);
                    }
                }

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
            }
        });
    }

    private void setDialogOnClickListener() {
        myDialog.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
    }
}
