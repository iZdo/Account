package com.izdo.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.izdo.DataBase.MyDatabaseHelper;

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

    public static List<String> dataList=new ArrayList<>();

    public static void init(Context context) {
        mSharedPreferences = context.getSharedPreferences("data", MODE_PRIVATE);

        ballColor = mSharedPreferences.getString("selectedColor", "#696969");

        isAddIncome = mSharedPreferences.getBoolean("isAddIncome", false);
        isShowBudget = mSharedPreferences.getBoolean("isShowBudget", true);
        isNoLongerPrompt = mSharedPreferences.getBoolean("isNoLongerPrompt", false);

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
        Cursor cursor = MyDatabaseHelper.getInstance(context).query("Account", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String account = cursor.getString(cursor.getColumnIndex("account"));
            dataList.add(account);
        }

        return dataList;
    }
}
