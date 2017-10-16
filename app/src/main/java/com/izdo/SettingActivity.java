package com.izdo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.izdo.Util.MyDialog;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout setting;
    private LinearLayout budgetSetting;
    private LinearLayout accountManage;
    private LinearLayout updateAnnouncement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {
        setting = (LinearLayout) findViewById(R.id.setting);
        budgetSetting = (LinearLayout) findViewById(R.id.budget_setting);
        accountManage = (LinearLayout) findViewById(R.id.account_manage);
        updateAnnouncement = (LinearLayout) findViewById(R.id.update_announcement);

        setting.setOnClickListener(this);
        budgetSetting.setOnClickListener(this);
        accountManage.setOnClickListener(this);
        updateAnnouncement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                finish();
                break;
            case R.id.budget_setting:
                Intent intent = new Intent(this, BudgetSettingActivity.class);
                intent.putExtra("date", getIntent().getStringExtra("date"));
                startActivity(intent);
                break;
            case R.id.account_manage:
                startActivity(new Intent(this,AccountManageActivity.class));
                break;
            case R.id.update_announcement:
                MyDialog myDialog = new MyDialog(this, R.style.dialog_style);
                myDialog.initUpdateDialog();
                myDialog.setCancelable(false);
                myDialog.show();
                break;
            default:
                break;
        }
    }
}
