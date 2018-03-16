package com.izdo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chaychan.viewlib.PowerfulEditText;
import com.izdo.R;
import com.izdo.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * iZdo
 * 2017/10/27
 */
public class ValidateActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mCompositeSubscription;

    private LinearLayout validate;
    private PowerfulEditText phoneNumberEdit;
    private PowerfulEditText validateCodeEdit;
    private Button getValidateCode;
    private Button next;
    private EventHandler eventHandler;

    private String phoneNumber;

    // 定时器
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            getValidateCode.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            getValidateCode.setEnabled(true);
            getValidateCode.setBackgroundColor(Color.parseColor("#9ac93e"));
            getValidateCode.setText("获取验证码");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);

        init();

        // 注册监听器
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                switch (event) {
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ValidateActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent= new Intent(ValidateActivity.this, RegisterActivity.class);
                                    intent.putExtra("phoneNumber", phoneNumberEdit.getText().toString().trim());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ValidateActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                }
            }
        });
    }

    private void init() {
        validate = (LinearLayout) findViewById(R.id.validate);
        phoneNumberEdit = (PowerfulEditText) findViewById(R.id.phone_number);
        validateCodeEdit = (PowerfulEditText) findViewById(R.id.validate_code);
        getValidateCode = (Button) findViewById(R.id.get_validate_code);
        next = (Button) findViewById(R.id.next);

        validate.setOnClickListener(this);
        getValidateCode.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.validate:
                finish();
                break;
            case R.id.get_validate_code:
                // 验证手机号码是否可注册
                phoneNumber = phoneNumberEdit.getText().toString().trim();

                // 如果手机号码不符合常规号码规则
                if (phoneNumber.length() != 11) {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 验证手机号是否已被注册
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("mobilePhoneNumber", phoneNumber);
                addSubscription(query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> object, BmobException e) {
                        if (object.size() != 0) {
                            Toast.makeText(ValidateActivity.this, "该手机号已被注册", Toast.LENGTH_SHORT).show();
                        } else {
                            getValidateCode.setEnabled(false);
                            getValidateCode.setBackgroundColor(Color.parseColor("#696969"));
                            //发送短信验证码到手机号
                            SMSSDK.getVerificationCode("86", phoneNumber);
                            // 启动定时器
                            timer.start();
                        }
                    }
                }));
                break;
            case R.id.next:
                String code = validateCodeEdit.getText().toString().trim();
                phoneNumber = phoneNumberEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    break;
                }
                //提交验证码  在eventHandler里面查看验证结果
                SMSSDK.submitVerificationCode("86", phoneNumber, code);
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

        //防止使用短信验证 产生内存溢出问题
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
