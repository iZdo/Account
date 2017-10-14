package com.izdo.Util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by iZdo on 2017/10/14.
 */

public class SPInit {

    private static SharedPreferences mSharedPreferences;

    public static String ballColor;

    public static boolean isAddIncome;
    public static boolean isShowBudget;
    public static boolean isNoLongerPrompt;

    public static void init(Context context) {
        mSharedPreferences = context.getSharedPreferences("data", MODE_PRIVATE);

        ballColor = mSharedPreferences.getString("selectedColor", "#696969");

        isAddIncome = mSharedPreferences.getBoolean("isAddIncome", false);
        isShowBudget = mSharedPreferences.getBoolean("isShowBudget", true);
        isNoLongerPrompt = mSharedPreferences.getBoolean("isNoLongerPrompt", false);

    }

}
