package com.izdo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaychan.viewlib.PowerfulEditText;
import com.dd.CircularProgressButton;
import com.izdo.Util.InitData;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;

/**
 * iZdo
 * 2017/10/27
 */
public class LoginActivity extends AppCompatActivity {

    private CircleImageView pic;
    private LinearLayout login;
    private PowerfulEditText usernameEdit;
    private PowerfulEditText passwordEdit;
    private CircularProgressButton loginButton;
    private TextView register;

    private CountDownTimer timer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        pic = (CircleImageView) findViewById(R.id.pic);
        login = (LinearLayout) findViewById(R.id.login);
        usernameEdit = (PowerfulEditText) findViewById(R.id.username_edit);
        passwordEdit = (PowerfulEditText) findViewById(R.id.password_edit);
        register = (TextView) findViewById(R.id.register);
        loginButton = (CircularProgressButton) findViewById(R.id.login_button);

        // 设置不确定精度
        loginButton.setIndeterminateProgressMode(true);

        // 返回
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 登录
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setProgress(0);

                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginButton.setProgress(1);

                final BmobUser user = new BmobUser();
                user.setUsername(username);
                user.setPassword(password);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        user.loginObservable(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                loginButton.setProgress(-1);
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(BmobUser bmobUser) {
                                InitData.isLogin = true;
                                loginButton.setProgress(100);
                                timer.start();
                            }
                        });
                    }
                }).start();
            }
        });

        // 注册
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginActivity.this, ValidateActivity.class));
            }
        });
    }


}
