package com.izdo.Util;

import com.izdo.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by iZdo on 2017/5/1.
 */

public class TypeMap {

    private Map<Integer, String> typeMap = new HashMap<Integer, String>();
    private Map<String, Integer> typeImgMap = new HashMap<String, Integer>();
    private String mBehavior;

    public TypeMap() {
        initTypeMap();
        initTypeImgMap();
        //mBehavior = behavior;
    }

    // 初始化type哈希表
    private void initTypeMap() {
        //        typeMap.put(R.id.breakfast, "breakfast");
        //        typeMap.put(R.id.lunch, "lunch");
        //        typeMap.put(R.id.dinner, "dinner");
        //        typeMap.put(R.id.beverage, "beverage");
        //        typeMap.put(R.id.snacks, "snacks");
        //        typeMap.put(R.id.traffic, "traffic");
        //        typeMap.put(R.id.grocery, "grocery");
        //        typeMap.put(R.id.entertainment, "entertainment");
        //        typeMap.put(R.id.social, "social");
        //        typeMap.put(R.id.clothes, "clothes");
        //        typeMap.put(R.id.shopping, "shopping");
        //        typeMap.put(R.id.rent, "rent");
        //        typeMap.put(R.id.gifts, "gifts");
        //        typeMap.put(R.id.cash_gift, "cash_gift");
        //        typeMap.put(R.id.medical, "medical");
        //        typeMap.put(R.id.mobile_bill, "mobile_bill");
        //        typeMap.put(R.id.outcome_investment, "outcome_investment");
        //        typeMap.put(R.id.credit_card, "credit_card");
        //        typeMap.put(R.id.transfer, "transfer");
        //        typeMap.put(R.id.outcome_other, "outcome_other");
        typeMap.put(R.id.breakfast, "早餐");
        typeMap.put(R.id.lunch, "午餐");
        typeMap.put(R.id.dinner, "晚餐");
        typeMap.put(R.id.beverage, "饮料");
        typeMap.put(R.id.snacks, "零食");

        typeMap.put(R.id.traffic, "交通");
        //            typeMap.put(R.id.grocery, "日常用品");
        typeMap.put(R.id.shopping, "购物");
        typeMap.put(R.id.entertainment, "娱乐");
        typeMap.put(R.id.social, "社交");
        //            typeMap.put(R.id.clothes, "衣物");
        typeMap.put(R.id.ticket, "车/机票");

        typeMap.put(R.id.water_and_electricity, "水电费");
        typeMap.put(R.id.rent, "房租");
        typeMap.put(R.id.gifts, "礼物");
        //            typeMap.put(R.id.cash_gift, "礼金");
        typeMap.put(R.id.transfer, "转账");
        typeMap.put(R.id.medical, "医疗");

        typeMap.put(R.id.mobile_bill, "话费");
        typeMap.put(R.id.loan, "借出");
        typeMap.put(R.id.repayment, "还款");
        typeMap.put(R.id.outcome_investment, "投资支出");
        typeMap.put(R.id.outcome_other, "其它支出");

        typeMap.put(R.id.salary, "薪水");
        typeMap.put(R.id.bonus, "奖金");
        typeMap.put(R.id.subsidy, "补助");
        typeMap.put(R.id.income_investment, "投资收入");
        typeMap.put(R.id.income_other, "其它收入");
    }

    // 初始化typeImg哈希表
    private void initTypeImgMap() {
        typeImgMap.put("早餐", R.drawable.breakfast_white);
        typeImgMap.put("午餐", R.drawable.lunch_white);
        typeImgMap.put("晚餐", R.drawable.dinner_white);
        typeImgMap.put("饮料", R.drawable.beverage_white);
        typeImgMap.put("零食", R.drawable.snacks_white);

        typeImgMap.put("交通", R.drawable.traffic_white);
        //        typeImgMap.put("日常用品", R.drawable.grocery_white);
        typeImgMap.put("购物", R.drawable.shopping_white);
        typeImgMap.put("娱乐", R.drawable.entertainment_white);
        typeImgMap.put("社交", R.drawable.social_white);
        //        typeImgMap.put("衣物", R.drawable.clothes_white);
        typeImgMap.put("车/机票", R.drawable.ticket_white);

        typeImgMap.put("水电费", R.drawable.water_and_electricity_white);
        typeImgMap.put("房租", R.drawable.rent_white);
        typeImgMap.put("礼物", R.drawable.gift_white);
        //        typeImgMap.put("礼金", R.drawable.cash_white);
        typeImgMap.put("转账", R.drawable.transfer_white);
        typeImgMap.put("医疗", R.drawable.medical_white);

        typeImgMap.put("话费", R.drawable.phone_white);
        typeImgMap.put("借出", R.drawable.loan_white);
        typeImgMap.put("还款", R.drawable.repayment_white);
        typeImgMap.put("投资支出", R.drawable.invest_white);
        //        typeImgMap.put("信用卡", R.drawable.credit_white);
        typeImgMap.put("其它支出", R.drawable.other_white);

        typeImgMap.put("薪水", R.drawable.salary_white);
        typeImgMap.put("奖金", R.drawable.bonus_white);
        typeImgMap.put("补助", R.drawable.subsidy_white);
        typeImgMap.put("投资收入", R.drawable.invest_white);
        typeImgMap.put("其它收入", R.drawable.other_white);
    }

    // TypeMap值找键
    public int valueToKey(String value) {
        Set<Map.Entry<Integer, String>> entries = typeMap.entrySet();
        for (Map.Entry entry : entries) {
            if (value.equals(entry.getValue()))
                return (int) entry.getKey();
        }
        return 0;
    }

    // 按id查找类型名
    public String queryType(int id) {
        return typeMap.get(id);
    }

    // 按类型名查找图片
    public int queryTypeImg(String type) {
        return typeImgMap.get(type);
    }
}
