package com.izdo.Util;

/**
 * Created by iZdo on 2017/10/2.
 * 常量池
 */

public class Constant {

    public static final String TOOLBAR_TITLE = "Account";

    public static final String OUTCOME = "outcome";
    public static final String INCOME = "income";
    public static final String TOTAL = "total";

    public static final String QUERY_SQL = "money = ? and type = ? and describe = ? and account = ? " +
            "and fixed_charge = ? and start_date = ? and behavior = ?";

    public static String DATABASE_PATH = "/data/data/com.izdo/databases/Account.db";

    public static final int BACKUP = 1;
    public static final int RESTORE = 2;

    public static final int IMAGE_REQUEST_CODE = 0;
    //    public static final int RESIZE_REQUEST_CODE = 1;

    //    public static int AUTOUPDATE= 1;
    //    public static int BACKUPANDRESTORE= 2;

    public static final int THIS_MONTH = 0;
    public static final int HALF_A_YEAR = 1;
    public static final int ONE_YEAR = 2;
    public static final int USER_DEFINED = 3;

    public static final int ACCOUNT_AND_FIXED_CHARGED = 0;
    public static final int STATISTICS = 1;

    public static final String BEGIN = "begin";
    public static final String END = "end";

    public static String UPDATE;

    //    private static String dealString(String update) {
    //
    //        String string = "";
    //
    //        String[] strings = update.split("。");
    //        for (String s : strings) {
    //            string += s + "\n";
    //        }
    //
    //        return string;
    //    }
}
