package com.izdo.Util;

/**
 * Created by iZdo on 2017/10/2.
 * 常量池
 */

public class Constant {

    public static final String TOOLBAR_TITLE = "Account";

    public static final String OUTCOME = "outcome";
    public static final String INCOME = "income";

    public static final String QUERY_SQL="money = ? and type = ? and describe = ? and account = ? " +
            "and fixed_charge = ? and start_date = ? and behavior = ?";

    public static String UPDATE ;

    private static String dealString(String update) {

        String string = "";

        String[] strings = update.split("。");
        for (String s : strings) {
            string += s + "\n";
        }

        return string;
    }
}
