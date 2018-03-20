package com.izdo;

import android.util.Log;

import com.izdo.util.BallListUtil;
import com.izdo.util.Constant;
import com.izdo.util.InitData;
import com.mob.MobApplication;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by iZdo on 2017/10/14.
 */

public class MyApplication extends MobApplication {

    private boolean isUnInitBmob = true;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化Bmob
        initBmob();
        if (isUnInitBmob) {
            isUnInitBmob = false;
            BmobUpdateAgent.initAppVersion();
        }

        // 初始化Mob
        //        MobSDK.init(this, "21f2ceb54967b", "fc0dae4f94fe7c3f36ed3e86a50bce2b");

        // 初始化余量球集合
        BallListUtil.init();

        // 初始化应用数据
        InitData.init(this);
        // 初始化更新公告数据
        Constant.UPDATE = InitData.readAsset(this);

        // 初始化信鸽推送
        initXGPush();
    }

    public void initXGPush() {
        Log.d("TPush", "注册");
        XGPushConfig.enableDebug(this, false);

        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    public void initBmob() {
        //第一：默认初始化
        Bmob.initialize(this, "a2c4c51ec611b1b212e828f7b343e532");
        // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
        //Bmob.initialize(this, "Your Application ID","bmob");

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
    }
}
