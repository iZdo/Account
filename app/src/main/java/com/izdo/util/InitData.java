package com.izdo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.izdo.dataBase.DatabaseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by iZdo on 2017/10/14.
 */

public class InitData {

    private static SharedPreferences mSharedPreferences;

    public static String ballColor;

    public static boolean isAddIncome;
    public static boolean isShowBudget;
    public static boolean isNoLongerPrompt;

    public static String preset;

    public static String picPath;

    public static List<String> dataList = new ArrayList<>();

    // 标记是否已经登录
    public static boolean isLogin = false;

    public static void init(Context context) {
        mSharedPreferences = context.getSharedPreferences("data", MODE_PRIVATE);

        ballColor = mSharedPreferences.getString("selectedColor", "#696969");

        isAddIncome = mSharedPreferences.getBoolean("isAddIncome", false);
        isShowBudget = mSharedPreferences.getBoolean("isShowBudget", true);
        isNoLongerPrompt = mSharedPreferences.getBoolean("isNoLongerPrompt", false);

        preset = mSharedPreferences.getString("preset", "现金");

        setPic();
    }

    public static void setPic() {
        picPath = mSharedPreferences.getString("picPath", "");
    }

    /**
     * 获取固定支出集合
     */
    public static List<String> fixedChargeOption() {
        dataList.clear();
        dataList.add("无");
        dataList.add("每日");
        dataList.add("每周");
        dataList.add("每月");
        return dataList;
    }

    /**
     * 获取账号集合
     */
    public static List<String> accountOption(Context context) {
        dataList.clear();
        Cursor cursor = DatabaseHelper.getInstance(context).query("Account", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String account = cursor.getString(cursor.getColumnIndex("account"));
            dataList.add(account);
        }
        return dataList;
    }

    /**
     * 读取更新日志文件
     */
    public static String readAsset(Context context) {
        try {
            InputStream is = context.getAssets().open("update.txt");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
