package com.izdo;

import android.app.Application;

import com.izdo.Util.BallListUtil;
import com.izdo.Util.Constant;
import com.izdo.Util.InitData;

/**
 * Created by iZdo on 2017/10/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化余量球集合
        BallListUtil.init();

        // 初始化应用数据
        InitData.init(this);
        // 初始化更新公告数据
        Constant.UPDATE = InitData.readAsset(this);
    }
}
