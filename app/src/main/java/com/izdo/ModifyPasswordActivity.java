package com.izdo;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * iZdo
 * 2017/10/30
 */
public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mCompositeSubscription;

    private LinearLayout modifyPassword;
    private EditText oldPasswordEdit;
    private EditText newPasswordEdit;
    private EditText confirmPasswordEdit;
    private CircularProgressButton modifyPasswordButton;

    private CountDownTimer timer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        modifyPassword = (LinearLayout) findViewById(R.id.modify_password);
        oldPasswordEdit = (EditText) findViewById(R.id.old_password);
        newPasswordEdit = (EditText) findViewById(R.id.new_password);
        confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password);
        modifyPasswordButton = (CircularProgressButton) findViewById(R.id.modify_password_button);

        modifyPassword.setOnClickListener(this);
        modifyPasswordButton.setOnClickListener(this);

        // 设置不确定精度
        modifyPasswordButton.setIndeterminateProgressMode(true);
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

    /**
     * 修改密码
     */
    private void setModifyPassword() {
        modifyPasswordButton.setProgress(0);

        final String oldPassword = oldPasswordEdit.getText().toString().trim();
        final String newPassword = newPasswordEdit.getText().toString().trim();
        String confirmPassword = confirmPasswordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "请输入旧密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.equals(oldPassword)) {
            Toast.makeText(this, "请不要使用旧密码作为新密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!confirmPassword.equals(newPassword)) {
            Toast.makeText(this, "两次密码输入不一致,请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        modifyPasswordButton.setProgress(1);

        final BmobUser user = BmobUser.getCurrentUser();

        user.setPassword(newPassword);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            modifyPasswordButton.setProgress(100);
                            timer.start();
                        } else {
                            modifyPasswordButton.setProgress(-1);
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.modify_password:
                finish();
                break;
            case R.id.modify_password_button:
                setModifyPassword();
                break;
        }
    }
}
