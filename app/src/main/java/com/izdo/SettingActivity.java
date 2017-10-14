package com.izdo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.izdo.Util.Constant;
import com.izdo.Util.MyDialog;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout setting;
    private LinearLayout budgetSetting;
    private LinearLayout updateAnnouncement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {
        setting = (LinearLayout) findViewById(R.id.setting);
        budgetSetting = (LinearLayout) findViewById(R.id.setting_budget_setting);

        updateAnnouncement = (LinearLayout) findViewById(R.id.update_announcement);

        setting.setOnClickListener(this);
        budgetSetting.setOnClickListener(this);

        updateAnnouncement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                finish();
                break;
            case R.id.setting_budget_setting:
                Intent intent = new Intent(this, BudgetSettingActivity.class);
                intent.putExtra("date", getIntent().getStringExtra("date"));
                startActivity(intent);
                break;
            case R.id.update_announcement:
                MyDialog updateAnnouncementDialog = new MyDialog(this, R.style.dialog_style, "updateAnnouncement");
                updateAnnouncementDialog.setUpdate(Constant.UPDATE);
                updateAnnouncementDialog.setCancelable(false);
                updateAnnouncementDialog.show();
                break;
            default:
                break;
        }
    }
}
