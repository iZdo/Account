package com.izdo.Util;

/**
 * Created by iZdo on 2017/10/2.
 * 常量池
 */

public class Constant {

    public static final String TOOLBAR_TITLE = "Account";

    public static final String OUTCOME = "outcome";
    public static final String INCOME = "income";

//    public static final String UPDATE = "1.2.6\n" +
//            "* 新增余量球更换颜色功能\n" +
//            "1.2.5\n" +
//            "* 新增预算余量球功能\n" +
//            "* 修复未根据预算设置页面开关显示预算和剩余预算bug\n" +
//            "* 修复选择账户显示错位bug\n" +
//            "1.2.4\n" +
//            "* 更换支出部分选项和顺序，删除的选项则作为“其它”存储。\n" +
//            "* 新增更新公告显示功能，打开APP时即会弹出，可在设置界面查看更新公告。\n" +
//            "* 新增单击主界面标题栏日期跳回本日支出/收入界面功能。\n" +
//            "* 新增是否将收入算入剩余预算和是否显示预算开关。\n" +
//            "* 数据库版本升级为2.0。\n" +
//            "* 根据便利性调节若干控件。";

    public static String UPDATE ;

    public static void setUPDATE(String UPDATE) {
        Constant.UPDATE = UPDATE;
    }

    private static String dealString(String update) {

        String string = "";

        String[] strings = update.split("。");
        for (String s : strings) {
            string += s + "\n";
        }

        return string;
    }
}
