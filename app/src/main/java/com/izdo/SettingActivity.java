package com.izdo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout setting;
    LinearLayout budgetSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {
        setting = (LinearLayout) findViewById(R.id.setting);
        budgetSetting = (LinearLayout) findViewById(R.id.setting_budget_setting);

        setting.setOnClickListener(this);
        budgetSetting.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
