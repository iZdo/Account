package com.izdo.activity;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.izdo.R;
import com.izdo.bean.User;
import com.izdo.util.Constant;
import com.izdo.util.InitData;
import com.izdo.util.MyDialog;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.izdo.util.InitData.picPath;

/**
 * iZdo
 * 2017/10/27
 */

public class MineActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mCompositeSubscription;

    private LinearLayout personalInfo;
    private CircleImageView pic;
    private TextView username;
    private LinearLayout changePic;
    private LinearLayout changePassword;
    private LinearLayout logout;

    private Uri uri;
    private String path;
    private BmobFile bmobFile;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();

        personalInfo = (LinearLayout) findViewById(R.id.personal_info);
        pic = (CircleImageView) findViewById(R.id.pic);
        username = (TextView) findViewById(R.id.username);
        changePic = (LinearLayout) findViewById(R.id.change_pic);
        changePassword = (LinearLayout) findViewById(R.id.modify_password);
        logout = (LinearLayout) findViewById(R.id.logout);

        personalInfo.setOnClickListener(this);
        pic.setOnClickListener(this);
        changePic.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        logout.setOnClickListener(this);

        if (BmobUser.getObjectByKey("username") != null)
            username.setText(BmobUser.getObjectByKey("username") + "");

        Bitmap bitmap = null;
        try {
            if (InitData.picPath.equals("")) return;
            FileInputStream fis = new FileInputStream(InitData.picPath);
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
        startActivityForResult(intent, Constant.IMAGE_REQUEST_CODE);
    }

    //    /**
    //     * 裁剪图片
    //     * @param uri
    //     */
    //    private void cropImage(Uri uri) {
    //        Intent intent = new Intent("com.android.camera.action.CROP");
    //        intent.setDataAndType(uri, "image/*");
    //        intent.putExtra("crop", "true");
    //        intent.putExtra("aspectX", 1);
    //        intent.putExtra("aspectY", 1);
    //        intent.putExtra("outputX", 150);
    //        intent.putExtra("outputY", 150);
    //        intent.putExtra("return-data", true);
    //        startActivityForResult(intent, Constant.RESIZE_REQUEST_CODE);
    //    }

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

    private void change() {
        // 如果path为空 说明没有选择图片
        if (path == null) {
            Toast.makeText(this, "请点击上方选择图片后再点我", Toast.LENGTH_SHORT).show();
            return;
        }

        final MyDialog myDialog = new MyDialog(MineActivity.this, R.style.dialog_style);
        myDialog.initSelectDialog("确定要上传头像？");
        myDialog.show();
        myDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();

                Toast.makeText(MineActivity.this, "上传中,请稍候", Toast.LENGTH_SHORT).show();

                // 头像文件
                bmobFile = new BmobFile(new File(path));

                //  流程:上传头像->缓存头像到本地
                addSubscription(bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            // 先缓存头像到本地
                            bmobFile.download(new DownloadFileListener() {
                                @Override
                                public void done(final String path, BmobException e) {
                                    if (e == null) {
                                        Logger.i(path);
                                        modifyUser();
                                        // 删除旧头像文件
                                        deleteOldFile();
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
                path = null;
            }
        });

        myDialog.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
    }

    private void deleteOldFile() {
        File file = new File(InitData.picPath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 修改用户表数据
     */
    private void modifyUser() {
        // 查询用户
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", BmobUser.getObjectByKey("username"));
        addSubscription(query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) return;
                    User user = list.get(0);
                    user.setPic(bmobFile);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(MineActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                                Toast.makeText(MineActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }
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
        if (requestCode == Constant.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            //            cropImage(uri);
            path = getImagePath(uri, null);
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                pic.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.personal_info:
                finish();
                break;
            case R.id.pic:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectImage();
                }
                break;
            case R.id.change_pic:
                change();
                break;
            case R.id.modify_password:
                startActivity(new Intent(this, ModifyPasswordActivity.class));
                break;
            case R.id.logout:
                final MyDialog myDialog = new MyDialog(MineActivity.this, R.style.dialog_style);
                myDialog.initSelectDialog("确定要退出登录？");
                myDialog.show();
                myDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.dismiss();
                        BmobUser.logOut();
                        picPath = "";
                        mEditor.putString("picPath", "");
                        mEditor.commit();
                        InitData.isLogin = false;
                        Toast.makeText(MineActivity.this, "退出登录成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                myDialog.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.dismiss();
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
