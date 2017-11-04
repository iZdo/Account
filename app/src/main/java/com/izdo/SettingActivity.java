package com.izdo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.izdo.Bean.User;
import com.izdo.Util.Constant;
import com.izdo.Util.InitData;
import com.izdo.Util.MyDialog;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mCompositeSubscription;

    private LinearLayout setting;
    private LinearLayout budgetSetting;
    private LinearLayout accountManage;
    private LinearLayout backupAndRestore;
    private LinearLayout updateAnnouncement;
    private LinearLayout checkUpdate;

    private MyDialog myDialog;
    private BmobFile bmobFile;

    private String flag;

    private CountDownTimer timer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
        }
    };
    private CircularProgressButton backupButton;
    private CircularProgressButton restoreButton;

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
        backupAndRestore = (LinearLayout) findViewById(R.id.backup_and_restore);
        updateAnnouncement = (LinearLayout) findViewById(R.id.update_announcement);
        checkUpdate = (LinearLayout) findViewById(R.id.check_update);

        myDialog = new MyDialog(this, R.style.dialog_style);

        setting.setOnClickListener(this);
        budgetSetting.setOnClickListener(this);
        accountManage.setOnClickListener(this);
        backupAndRestore.setOnClickListener(this);
        updateAnnouncement.setOnClickListener(this);
        checkUpdate.setOnClickListener(this);
    }

    // android6.0动态申请权限 写入sd卡权限
    private void requestPermission() {
        // 检查是否有权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            // 不需要申请权限则调用方法
            if (flag.equals("update"))
                BmobUpdateAgent.forceUpdate(this);
            else if (flag.equals("backupAndRestore"))
                backupAndRestore();
        }
    }

    // 备份和还原
    private void backupAndRestore() {
        myDialog.initBackupAndRestoreDialog();
        myDialog.setCancelable(false);
        myDialog.show();

        backupButton = (CircularProgressButton) myDialog.findViewById(R.id.backup_button);
        restoreButton = (CircularProgressButton) myDialog.findViewById(R.id.restore_button);

        // 设置不确定精度
        backupButton.setIndeterminateProgressMode(true);
        restoreButton.setIndeterminateProgressMode(true);

        bmobFile = new BmobFile(new File(Constant.DATABASE_PATH));

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (backupButton.getText().toString().equals("备份成功")) {
                    Toast.makeText(SettingActivity.this, "已经备份过了,不需要重复备份", Toast.LENGTH_SHORT).show();
                    return;
                } else if (backupButton.getText().toString().equals("备份失败")) {
                    Toast.makeText(SettingActivity.this, "网络状况不太好或者尝试重新登录试试？", Toast.LENGTH_SHORT).show();
                    return;
                }

                backupButton.setProgress(0);
                backupButton.setProgress(1);
                timer.start();
                addSubscription(bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            modifyUser(Constant.BACKUP);
                        } else {
                            e.printStackTrace();
                            backupButton.setProgress(-1);
                        }
                    }
                }));
            }
        });

        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restoreButton.getText().toString().equals("还原成功")) {
                    Toast.makeText(SettingActivity.this, "已经还原过了,不需要重复还原", Toast.LENGTH_SHORT).show();
                    return;
                } else if (restoreButton.getText().toString().equals("还原失败")) {
                    Toast.makeText(SettingActivity.this, "网络状况不太好或者尝试重新登录试试？", Toast.LENGTH_SHORT).show();
                    return;
                }

                restoreButton.setProgress(0);
                restoreButton.setProgress(1);
                timer.start();
                modifyUser(Constant.RESTORE);
            }
        });
    }

    /**
     * 修改用户表数据
     */
    private void modifyUser(final int flag) {
        // 查询用户
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", BmobUser.getObjectByKey("username"));
        addSubscription(query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) return;
                    User user = list.get(0);
                    if (flag == Constant.BACKUP)
                        backup(user);
                    else if (flag == Constant.RESTORE)
                        restore(user);
                } else {
                    if (flag == Constant.BACKUP)
                        backupButton.setProgress(-1);
                    else if (flag == Constant.RESTORE)
                        restoreButton.setProgress(-1);
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * 备份
     *
     * @param user
     */
    private void backup(User user) {
        user.setData(bmobFile);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    backupButton.setProgress(100);
                } else {
                    backupButton.setProgress(-1);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 还原
     *
     * @param user
     */
    private void restore(User user) {
        BmobFile bmobfile = new BmobFile("Account.db", "", user.getData().getFileUrl());
        bmobfile.download(new File(Constant.DATABASE_PATH), new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    restoreButton.setProgress(100);
                    Toast.makeText(SettingActivity.this, "请重启应用完成数据库还原", Toast.LENGTH_SHORT).show();
                } else {
                    restoreButton.setProgress(-1);
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 请求被接受则调用方法
                    if (flag.equals("update"))
                        BmobUpdateAgent.forceUpdate(this);
                    else if (flag.equals("backupAndRestore"))
                        backupAndRestore();
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
            case R.id.backup_and_restore:
                if (!InitData.isLogin) {
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
                flag = "backupAndRestore";
                requestPermission();
                break;
            case R.id.update_announcement:
                myDialog.initUpdateDialog();
                myDialog.setCancelable(false);
                myDialog.show();
                break;
            case R.id.check_update:
                // 请求权限并调用更新方法
                flag = "update";
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
        }
    }

    /**
     * 解决Subscription内存泄露问题
     */

    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
