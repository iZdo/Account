package com.izdo.receiver;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * Created by LinWeiTong on 2018/3/8.
 */

public class XGTestReceiver extends XGPushBaseReceiver {

    // 注册回调
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        Log.e("TPush", "onRegisterResult: " + xgPushRegisterResult.toString());
    }

    // 反注册回调
    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.e("TPush", "onUnregisterResult: " + i + "");
    }

    // 设置tag回调
    @Override
    public void onSetTagResult(Context context, int i, String s) {
        Log.e("TPush", "onSetTagResult: " + i + "  " + s);
        Log.e("TPush", "onSetTagResult:aaa " + XGPushManager.getServiceTag(context));
        // onSetTagResult: 0  abcTag
    }

    // 删除tag回调
    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        Log.e("TPush", "onDeleteTagResult: " + i + "  " + s);
    }

    // 消息透传回调
    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.e("TPush", "onTextMessage: " + xgPushTextMessage);

        // onTextMessage: XGPushShowedResult [title=null, content=应用内消息应用内消息, customContent=null]
    }

    // 通知点击回调
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.e("TPush", "onNotifactionClickedResult: " + xgPushClickedResult.toString());

        // onNotifactionClickedResult: XGPushClickedResult [msgId=3, title=XGPushDemo, customContent=, activityName=xgdemo.izdo.xgpushdemo.MainActivity, actionType=0, notificationActionType1]
    }

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        Log.e("TPush", "onNotifactionShowedResult: " + xgPushShowedResult.toString());

        // onNotifactionShowedResult: XGPushShowedResult [msgId=3, title=XGPushDemo, content=b6808c3b6c6695124eaa12272b8742ee0cfc6de1, customContent=, activity=xgdemo.izdo.xgpushdemo.MainActivity, notificationActionType1]
    }
}
