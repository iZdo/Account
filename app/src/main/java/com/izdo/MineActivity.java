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
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.izdo.Util.InitData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.izdo.Util.InitData.picPath;

/**
 * iZdo
 * 2017/10/27
 */

public class MineActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mCompositeSubscription;

    private CircleImageView pic;
    private LinearLayout change;
    private LinearLayout change_id;
    private LinearLayout logout;
    private EditText id_edit;

    private Uri uri;
    private String path;
    private BmobFile bmobFile;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();

        pic = (CircleImageView) findViewById(R.id.pic);
        change = (LinearLayout) findViewById(R.id.change);
        change_id = (LinearLayout) findViewById(R.id.change_id);
        logout = (LinearLayout) findViewById(R.id.logout);
        id_edit = (EditText) findViewById(R.id.id_edit);


        pic.setOnClickListener(this);
        change.setOnClickListener(this);
        change_id.setOnClickListener(this);
        logout.setOnClickListener(this);
        id_edit.setOnClickListener(this);

        // /data/user/0/com.izdo/cache/bmob/mmexport1506676177578.jpg
        Bitmap bitmap = null;
        try {
            if (picPath.equals("")) return;
            FileInputStream fis = new FileInputStream(picPath);
            bitmap = BitmapFactory.decodeStream(fis);//把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap != null)
            pic.setImageBitmap(bitmap);
    }

    /**
     * Intent跳转本地图库选择图片
     */
    private void selectImage() {
        Intent intent = new Intent();
        // 开启Pictures画面Type设定为image
        intent.setType("image/*");
        // 使用Intent.ACTION_GET_CONTENT这个Action
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // 取得相片后返回本画面
        startActivityForResult(intent, 0);
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

    private void change() {
        // 头像文件
        if (path != null)
            bmobFile = new BmobFile(new File(path));

        //  流程:上传头像->缓存头像到本地
        addSubscription(bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 先缓存头像到本地
                    bmobFile.download(new DownloadFileListener() {
                        @Override
                        public void done(String path, BmobException e) {
                            if (e == null) {
                                // 头像缓存完成后开始注册用户

                                // 修改相关数据
                                mEditor.putString("picPath", path);
                                mEditor.commit();
                                InitData.setPic();
                            } else {
                                e.printStackTrace();
                            }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pic:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectImage();
                }
                break;
            case R.id.change:
                change();
                break;
            case R.id.change_id:
                //                id_edit.getText().toString()
                break;
            case R.id.logout:
                BmobUser.logOut();
                mEditor.putString("picPath", "");
                InitData.picPath = "";
                mEditor.putString("picPath", "");
                mEditor.commit();
                InitData.isLogin = false;
                Toast.makeText(this, "退出登录成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.id_edit:

                break;

        }
    }
}
