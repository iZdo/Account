package com.izdo;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.MyDialog;

public class AddAccountActivity extends AppCompatActivity {

    private LinearLayout addAccount;
    private Button addAccountSave;
    private EditText addAccountEdit;
    private MyDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        init();
    }

    private void init() {
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
                String account_name = addAccountEdit.getText().toString();

                if (TextUtils.isEmpty(account_name)) {
                    myDialog.initConfirmDialog("账户名不能为空!");
                    setDialogOnClickListener();
                    myDialog.show();
                } else {
                    values.put("account", account_name);

                    // 查询账户名是否已存在
                    if (MyDatabaseHelper.getInstance(AddAccountActivity.this).query("Account", null, "account=?", new String[]{account_name}, null, null, null).getCount() > 0) {
                        myDialog.initConfirmDialog("此账户已存在!");
                        setDialogOnClickListener();
                        myDialog.show();
                    } else {
                        MyDatabaseHelper.getInstance(AddAccountActivity.this).insert("Account", null, values);
                        finish();
                    }
                }
            }
        });
    }

    private void setDialogOnClickListener(){
        myDialog.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
    }
}
