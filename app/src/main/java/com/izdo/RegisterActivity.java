package com.izdo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaychan.viewlib.PowerfulEditText;
import com.izdo.Bean.User;
import com.izdo.Util.InitData;
import com.izdo.Util.MyDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * iZdo
 * 2017/10/27
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mCompositeSubscription;

    private LinearLayout register;
    private PowerfulEditText usernameEdit;
    private PowerfulEditText passwordEdit;
    private Button registerButton;
    private CircleImageView pic;
    private TextView setPic;

    private String username;
    private String password;
    private BmobFile bmobFile;

    // 头像真实路径
    private Uri uri;
    private String path;

    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

    private void init() {
        mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();

        register = (LinearLayout) findViewById(R.id.register);
        usernameEdit = (PowerfulEditText) findViewById(R.id.username_edit);
        passwordEdit = (PowerfulEditText) findViewById(R.id.password_edit);
        registerButton = (Button) findViewById(R.id.register_button);
        pic = (CircleImageView) findViewById(R.id.pic);
        setPic = (TextView) findViewById(R.id.set_pic);

        register.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        setPic.setOnClickListener(this);

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

    private void register() {
        // 头像文件
        if (path != null)
            bmobFile = new BmobFile(new File(path));

        username = usernameEdit.getText().toString().trim();
        password = passwordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "账号名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //        if (username.length() < 4) {
        //            Toast.makeText(RegisterActivity.this, "账号名长度应不小于4位", Toast.LENGTH_SHORT).show();
        //            return;
        //        }

        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "密码长度应不小于6位", Toast.LENGTH_SHORT).show();
            return;
        }

        // 查询用户名是否已经存在
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);

        //  流程:上传头像->缓存头像到本地->提交注册数据
        addSubscription(query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                // 如果查询不到此用户 还未被注册
                if (object.size() == 0) {
                    // 上传头像
                    addSubscription(bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 先缓存头像到本地
                                bmobFile.download(new DownloadFileListener() {
                                    @Override
                                    public void done(String path, BmobException e) {
                                        // 头像缓存完成后开始注册用户

                                        // 修改相关数据
                                        mEditor.putString("picPath", path);
                                        mEditor.commit();
                                        InitData.setPic();

                                        final User myUser = new User();
                                        myUser.setUsername(username);
                                        myUser.setPassword(password);
                                        myUser.setMobilePhoneNumber(getIntent().getStringExtra("phoneNumber"));

                                        if (bmobFile != null)
                                            myUser.setPic(bmobFile);
                                        addSubscription(myUser.signUp(new SaveListener<User>() {
                                            @Override
                                            public void done(User user, BmobException e) {
                                                if (user == null) {
                                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                } else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }));
                                    }

                                    @Override
                                    public void onProgress(Integer integer, long l) {

                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }));
                } else {
                    Toast.makeText(RegisterActivity.this, "此账户已存在", Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    /**
     * Intent跳转本地图库选择图片
     */
    private void selectImage() {
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                finish();
                break;
            case R.id.register_button:
                if (uri == null) {
                    final MyDialog myDialog = new MyDialog(RegisterActivity.this, R.style.dialog_style);
                    myDialog.initSelectDialog("确定使用默认头像？");
                    myDialog.show();
                    myDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                            register();
                        }
                    });
                    myDialog.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                        }
                    });
                } else
                    register();
                break;
            case R.id.set_pic:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectImage();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                } else {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            path = getImagePath(uri, null);
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                pic.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // 获取真实路径
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();

        }
        return path;

    }
}
