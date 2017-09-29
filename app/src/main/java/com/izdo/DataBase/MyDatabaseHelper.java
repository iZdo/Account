package com.izdo.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iZdo on 2017/4/14.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

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
    public static final String CREATE_DATA = "create table Data(" +
            "id integer primary key autoincrement," +
            "money text," +
            "type text," +
            "describe text," +
            "account text," +
            "fixed_charge text," +
            "date text," +
            "behavior text)";

    public static final String CREATE_BUDGET = "create table Budget(" +
            "total text," +
            "date text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DATA);
        sqLiteDatabase.execSQL(CREATE_BUDGET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}