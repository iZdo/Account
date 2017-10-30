package com.izdo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaychan.viewlib.PowerfulEditText;
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
    private Button loginButton;
    private TextView register;

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
        loginButton = (Button) findViewById(R.id.login_button);

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
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                final BmobUser user = new BmobUser();
                user.setUsername(username);
                user.setPassword(password);
                user.loginObservable(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BmobUser bmobUser) {
                        InitData.isLogin = true;
                        Toast.makeText(LoginActivity.this, bmobUser.getUsername() + "登陆成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
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
