package com.izdo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.izdo.Adapter.MyBaseAdapter;
import com.izdo.Bean.DataBean;
import com.izdo.DataBase.MyDatabaseHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.izdo.MainActivity.behavior;

/**
 * Created by zz on 2017/4/20.
 */

public class MainFragment extends Fragment {

    private View mView;
    private ArrayList<DataBean> mList = new ArrayList();
    private String mDate;
    public static MyBaseAdapter mBaseAdapter;
    private MyDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private TextView totalCostText;
    private TextView surplusBudgeText;
    private TextView totalBudgetText;
    private TextView percentText;
    private ListView mListView;
    private float totalCost = 0;
    private float surplus = 0;
    private int percent = 0;
    private String mBehavior = "";
    private String totalBudget = "1000";
    private String surplusBudget = "1000";
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private Toolbar mToolbar;
    // 当前日期
    private String mToolbarDate;
    private Cursor mCursor;

    public MainFragment(String behavior) {
        mBehavior = behavior;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getDate() {
        return mDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        surplusBudgeText = (TextView) getActivity().findViewById(R.id.surplus_budget);
        totalBudgetText = (TextView) getActivity().findViewById(R.id.total_budget);
        percentText = (TextView) getActivity().findViewById(R.id.percent);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);

        // 根据类型加载布局
        if (mBehavior.equals("outcome"))
            mView = inflater.inflate(R.layout.main_outcome_viewpager, container, false);
        else if (mBehavior.equals("income"))
            mView = inflater.inflate(R.layout.main_income_viewpager, container, false);

        totalCostText = (TextView) mView.findViewById(R.id.total_cost);
        mListView = (ListView) mView.findViewById(R.id.main_listView);

        mDatabaseHelper = new MyDatabaseHelper(getContext(), "Account.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        setListView();

        return mView;
    }

    private void query() {
        mCursor = mSQLiteDatabase.query("Data", null, "date=? and behavior=?", new String[]{mDate, mBehavior}, null, null, null);
        while (mCursor.moveToNext()) {
            DataBean dataBean = new DataBean();
            dataBean.setId(mCursor.getInt(mCursor.getColumnIndex("id")));
            dataBean.setMoney(mCursor.getString(mCursor.getColumnIndex("money")));
            dataBean.setType(mCursor.getString(mCursor.getColumnIndex("type")));
            dataBean.setDescribe(mCursor.getString(mCursor.getColumnIndex("describe")));
            dataBean.setAccount(mCursor.getString(mCursor.getColumnIndex("account")));
            dataBean.setFixed_charge(mCursor.getString(mCursor.getColumnIndex("fixed_charge")));
            dataBean.setDate(mCursor.getString(mCursor.getColumnIndex("date")));
            dataBean.setBehavior(mCursor.getString(mCursor.getColumnIndex("behavior")));
            totalCost += Float.parseFloat(dataBean.getMoney());
            mList.add(dataBean);
        }

        // 显示总支出/收入金额
        String totalCostStr = decimalFormat.format(totalCost);
        if(totalCostStr.endsWith("0"))
            totalCostStr = totalCostStr.substring(0, totalCostStr.length() - 1);
        if (totalCostStr.endsWith(".0"))
            totalCostStr = totalCostStr.substring(0, totalCostStr.length() - 2);
        totalCostText.setText(totalCostStr);
        totalCost = 0;

        mCursor.close();
    }

    private void budget() {
        mToolbarDate = mToolbar.getTitle().toString();
        mToolbarDate = mToolbarDate.substring(0, mToolbarDate.length() - 3);

        mCursor = mSQLiteDatabase.query("Budget", null, "date = ?", new String[]{mToolbarDate}, null, null, null);
        while (mCursor.moveToNext()) {
            totalBudget = mCursor.getString(mCursor.getColumnIndex("total"));
            totalBudgetText.setText(totalBudget);
        }
        surplusBudget = totalBudget;
        surplus = Float.parseFloat(surplusBudget);
        mCursor.close();

        // 查找这个月所有收入或支出
        mCursor = mSQLiteDatabase.query("Data", null, "date like ? ", new String[]{mToolbarDate + "%"}, null, null, null);

        while (mCursor.moveToNext()) {
            DataBean dataBean = new DataBean();
            dataBean.setMoney(mCursor.getString(mCursor.getColumnIndex("money")));
            dataBean.setBehavior(mCursor.getString(mCursor.getColumnIndex("behavior")));
            // 计算剩余预算
            if (dataBean.getBehavior().equals("outcome"))
                surplus -= Float.parseFloat(dataBean.getMoney());
            else if (dataBean.getBehavior().equals("income"))
                surplus += Float.parseFloat(dataBean.getMoney());
        }

        // 舍弃小数点后为0的数字
        String surplusBudgetStr = decimalFormat.format(surplus);
        if(surplusBudgetStr.endsWith("0"))
            surplusBudgetStr = surplusBudgetStr.substring(0, surplusBudgetStr.length() - 1);
        if (surplusBudgetStr.endsWith(".0"))
            surplusBudgetStr = surplusBudgetStr.substring(0, surplusBudgetStr.length() - 2);
//        String surplusBudgetStr = surplus + "";
//        if (surplusBudgetStr.endsWith(".0"))
//            surplusBudgetStr = surplusBudgetStr.substring(0, surplusBudgetStr.length() - 2);

        surplusBudgeText.setText("¥ " + surplusBudgetStr);

        // 计算百分比
        percent = (int) ((surplus / Integer.parseInt(totalBudget.trim())) * 100);
        if (percent < 0)
            percent = 0;
        percentText.setText(percent + "%");

        mCursor.close();
    }

    private void setListView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if (mBehavior.equals("outcome"))
                    intent = new Intent(getContext(), OutcomeDetailsActivity.class);
                else if (mBehavior.equals("income"))
                    intent = new Intent(getContext(), IncomeDetailsActivity.class);
                intent.putExtra("dataBean", mList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 清空集合
        mList.clear();
        // 查询数据库并显示
        query();
        budget();

        mBaseAdapter = new MyBaseAdapter(getContext(), mList);
//         mBaseAdapter.notifyDataSetChanged();
        mListView.setAdapter(mBaseAdapter);
    }
}
