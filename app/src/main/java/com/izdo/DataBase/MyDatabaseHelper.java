package com.izdo.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by iZdo on 2017/4/14.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper mDatabaseHelper;

    private static Context mContext;

    /**
     * id 自增的唯一标识
     * money 金额
     * type 类型
     * describe 描述
     * account 账户
     * fixed_charge 固定支出
     * date 日期
     * behavior 收入/支出
     */
    public static final String CREATE_DATA = "create table if not exists Data(" +
            "id integer primary key autoincrement," +
            "money text," +
            "type text," +
            "describe text," +
            "account text," +
            "fixed_charge text," +
            "date text," +
            "behavior text)";

    public static final String CREATE_BUDGET = "create table if not exists Budget(" +
            "total text," +
            "date text)";

    public static final String CREATE_ACCOUNT = "create table if not exists Account(" +
            "account_id integer primary key autoincrement," +
            "account text)";

    private MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getInstance(Context context) {
        mContext = context;

        if (mDatabaseHelper == null)
            mDatabaseHelper = new MyDatabaseHelper(context, "Account.db", null, 3);

        return mDatabaseHelper.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DATA);
        sqLiteDatabase.execSQL(CREATE_BUDGET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql;

        /**
         * 2.0
         */
        if (oldVersion < 2) {
            sql = "update Data set type = '话费' where type = '花费'";
            sqLiteDatabase.execSQL(sql);

            sql = "update Data set type = '其它支出' where type = '日常用品' or type = '衣物' or type = '礼金' or type = '信用卡' and behavior = 'outcome'";
            sqLiteDatabase.execSQL(sql);
            sql = "update Data set type = '其它收入' where type = '日常用品' or type = '衣物' or type = '礼金' or type = '信用卡' and behavior = 'income'";
            sqLiteDatabase.execSQL(sql);
        }

        /**
         * 3.0
         */
        if (oldVersion < 3) {
            sqLiteDatabase.execSQL(CREATE_ACCOUNT);

            sql = "insert into account(account) values" +
                    "('微信')," +
                    "('支付宝')," +
                    "('现金')," +
                    "('其他')";
            sqLiteDatabase.execSQL(sql);
        }

        Toast.makeText(mContext, "数据库升级成功!", Toast.LENGTH_SHORT).show();
    }
}
