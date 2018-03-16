package com.izdo.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by iZdo on 2017/4/14.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mDatabaseHelper;

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

    public static final String CREATE_FIXED_RECORD = "create table if not exists FixedRecord(" +
            "fixedRecord_id integer primary key autoincrement," +
            "money text," +
            "type text," +
            "describe text," +
            "account text," +
            "fixed_charge text," +
            "start_date text," +
            "already_date text," +
            "behavior text)";

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getInstance(Context context) {
        mContext = context;

        if (mDatabaseHelper == null)
            mDatabaseHelper = new DatabaseHelper(context, "Account.db", null, 5);

        return mDatabaseHelper.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DATA);
        sqLiteDatabase.execSQL(CREATE_BUDGET);

        onUpgrade(sqLiteDatabase, sqLiteDatabase.getVersion(), 4);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql;

        /**
         * 2.0
         * 修复文字bug
         * 更改选项
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
         * 创建账户表
         * 插入默认列
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

        /**
         * 4.0
         * 为Data插入fixedRecord_id字段
         * 新建FixedRecord表
         */
        if (oldVersion < 4) {
            // 插入新字段
            sql = "alter table Data add column fixedRecord_id integer";
            sqLiteDatabase.execSQL(sql);

            // 将字段设置为0
            sql = "update Data set fixedRecord_id = 0";
            sqLiteDatabase.execSQL(sql);

            // 插入新表
            sqLiteDatabase.execSQL(CREATE_FIXED_RECORD);
        }

        /**
         * 5.0
         * 修改date字段为date类型数据
         */
        if (oldVersion < 5) {
            // 修改data表
            sql = "alter table Data rename to Data_old";
            sqLiteDatabase.execSQL(sql);

            sql = "create table if not exists Data(" +
                    "id integer primary key autoincrement," +
                    "money text," +
                    "type text," +
                    "describe text," +
                    "account text," +
                    "fixed_charge text," +
                    "date date," +
                    "behavior text," +
                    "fixedRecord_id integer)";
            sqLiteDatabase.execSQL(sql);

            sql = "insert into Data select * from Data_old";
            sqLiteDatabase.execSQL(sql);

            sql="drop table Data_old";
            sqLiteDatabase.execSQL(sql);

            // 修改budget表
            sql = "alter table Budget rename to Budget_old";
            sqLiteDatabase.execSQL(sql);

            sql = "create table if not exists Budget(" +
                    "total text," +
                    "date date)";
            sqLiteDatabase.execSQL(sql);

            sql = "insert into Budget select * from Budget_old";
            sqLiteDatabase.execSQL(sql);

            sql="drop table Budget_old";
            sqLiteDatabase.execSQL(sql);
        }

        Toast.makeText(mContext, "数据库升级成功!", Toast.LENGTH_SHORT).show();
    }
}
