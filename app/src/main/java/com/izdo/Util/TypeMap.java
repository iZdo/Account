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
    private Map<String, Integer> typeWhiteImgMap = new HashMap<String, Integer>();
    private Map<String, Integer> typeBlackImgMap = new HashMap<String, Integer>();
    private String mBehavior;

    public TypeMap() {
        initTypeMap();
        initTypeWhiteImgMap();
        initTypeBlackImgMap();
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
    private void initTypeWhiteImgMap() {
        typeWhiteImgMap.put("早餐", R.drawable.breakfast_white);
        typeWhiteImgMap.put("午餐", R.drawable.lunch_white);
        typeWhiteImgMap.put("晚餐", R.drawable.dinner_white);
        typeWhiteImgMap.put("饮料", R.drawable.beverage_white);
        typeWhiteImgMap.put("零食", R.drawable.snacks_white);

        typeWhiteImgMap.put("交通", R.drawable.traffic_white);
        //        typeWhiteImgMap.put("日常用品", R.drawable.grocery_white);
        typeWhiteImgMap.put("购物", R.drawable.shopping_white);
        typeWhiteImgMap.put("娱乐", R.drawable.entertainment_white);
        typeWhiteImgMap.put("社交", R.drawable.social_white);
        //        typeWhiteImgMap.put("衣物", R.drawable.clothes_white);
        typeWhiteImgMap.put("车/机票", R.drawable.ticket_white);

        typeWhiteImgMap.put("水电费", R.drawable.water_and_electricity_white);
        typeWhiteImgMap.put("房租", R.drawable.rent_white);
        typeWhiteImgMap.put("礼物", R.drawable.gift_white);
        //        typeWhiteImgMap.put("礼金", R.drawable.cash_white);
        typeWhiteImgMap.put("转账", R.drawable.transfer_white);
        typeWhiteImgMap.put("医疗", R.drawable.medical_white);

        typeWhiteImgMap.put("话费", R.drawable.phone_white);
        typeWhiteImgMap.put("借出", R.drawable.loan_white);
        typeWhiteImgMap.put("还款", R.drawable.repayment_white);
        typeWhiteImgMap.put("投资支出", R.drawable.invest_white);
        //        typeWhiteImgMap.put("信用卡", R.drawable.credit_white);
        typeWhiteImgMap.put("其它支出", R.drawable.other_white);

        typeWhiteImgMap.put("薪水", R.drawable.salary_white);
        typeWhiteImgMap.put("奖金", R.drawable.bonus_white);
        typeWhiteImgMap.put("补助", R.drawable.subsidy_white);
        typeWhiteImgMap.put("投资收入", R.drawable.invest_white);
        typeWhiteImgMap.put("其它收入", R.drawable.other_white);
    }

    private void initTypeBlackImgMap() {
        typeBlackImgMap.put("早餐", R.drawable.breakfast_black);
        typeBlackImgMap.put("午餐", R.drawable.lunch_black);
        typeBlackImgMap.put("晚餐", R.drawable.dinner_black);
        typeBlackImgMap.put("饮料", R.drawable.beverage_black);
        typeBlackImgMap.put("零食", R.drawable.snacks_black);

        typeBlackImgMap.put("交通", R.drawable.traffic_black);
        typeBlackImgMap.put("购物", R.drawable.shopping_black);
        typeBlackImgMap.put("娱乐", R.drawable.entertainment_black);
        typeBlackImgMap.put("社交", R.drawable.social_black);
        typeBlackImgMap.put("车/机票", R.drawable.ticket_black);

        typeBlackImgMap.put("水电费", R.drawable.water_and_electricity_black);
        typeBlackImgMap.put("房租", R.drawable.rent_black);
        typeBlackImgMap.put("礼物", R.drawable.gift_black);
        typeBlackImgMap.put("转账", R.drawable.transfer_black);
        typeBlackImgMap.put("医疗", R.drawable.medical_black);

        typeBlackImgMap.put("话费", R.drawable.phone_black);
        typeBlackImgMap.put("借出", R.drawable.loan_black);
        typeBlackImgMap.put("还款", R.drawable.repayment_black);
        typeBlackImgMap.put("投资支出", R.drawable.invest_black);
        typeBlackImgMap.put("其它支出", R.drawable.other_black);

        typeBlackImgMap.put("薪水", R.drawable.salary_black);
        typeBlackImgMap.put("奖金", R.drawable.bonus_black);
        typeBlackImgMap.put("补助", R.drawable.subsidy_black);
        typeBlackImgMap.put("投资收入", R.drawable.invest_black);
        typeBlackImgMap.put("其它收入", R.drawable.other_black);
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

    // 按类型名查找白色图片
    public int queryTypeWhiteImg(String type) {
        return typeWhiteImgMap.get(type);
    }

    // 按类型名查找黑色图片
    public int queryTypeBlackImg(String type) {
        return typeBlackImgMap.get(type);
    }
}
