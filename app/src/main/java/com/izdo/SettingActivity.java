package com.izdo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.izdo.Util.MyDialog;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout setting;
    private LinearLayout budgetSetting;
    private LinearLayout accountManage;
    private LinearLayout updateAnnouncement;
    private LinearLayout checkUpdate;


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
        checkUpdate = (LinearLayout) findViewById(R.id.check_update);

        setting.setOnClickListener(this);
        budgetSetting.setOnClickListener(this);
        accountManage.setOnClickListener(this);
        updateAnnouncement.setOnClickListener(this);
        checkUpdate.setOnClickListener(this);

    }

    // android6.0动态申请权限 写入sd卡权限
    private void requestPermission() {
        // 检查是否有权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 检查是否需要申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        } else {
            // 有权限直接调用更新方法
            BmobUpdateAgent.forceUpdate(this);
            Logger.i("有权限直接调用更新方法");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 请求被被接收则调用更新方法
                    BmobUpdateAgent.forceUpdate(this);
                } else {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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
                startActivity(new Intent(this, AccountManageActivity.class));
                break;
            case R.id.update_announcement:
                MyDialog myDialog = new MyDialog(this, R.style.dialog_style);
                myDialog.initUpdateDialog();
                myDialog.setCancelable(false);
                myDialog.show();
                break;
            case R.id.check_update:
                // 请求权限并调用更新方法
                requestPermission();
                BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                        if (updateStatus == UpdateStatus.No) {
                            Toast.makeText(SettingActivity.this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
