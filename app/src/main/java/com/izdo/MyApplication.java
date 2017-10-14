package com.izdo;

import android.app.Application;

import com.izdo.Util.BallListUtil;
import com.izdo.Util.SPInit;

/**
 * Created by iZdo on 2017/10/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BallListUtil.init();
        SPInit.init(this);

    }
}
