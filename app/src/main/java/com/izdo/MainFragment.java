package com.izdo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.izdo.Adapter.MyDataAdapter;
import com.izdo.Bean.DataBean;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.Constant;
import com.izdo.Util.InitData;
import com.izdo.Util.WaveHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.izdo.R.id.main_toolbar;

/**
 * Created by zz on 2017/4/20.
 */

public class MainFragment extends Fragment {

    private View mView;

    private ArrayList<DataBean> mList = new ArrayList();
    private String mDate;
    // 相差天数
    public static int phaseDay = Integer.MAX_VALUE / 2;
    private MyDataAdapter mDataAdapter;
    private RecyclerView mRecyclerView;

    private TextView totalCostText;
    private TextView surplusBudgeText;
    private TextView totalBudgetText;
    private TextView percentText;
    private float totalCost = 0;
    private float surplus = 0;
    private int percent = 0;
    private String mBehavior = "";
    private String totalBudget = "1000";
    private String surplusBudget = "1000";
    // 小数格式
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private Toolbar mToolbar;
    // 当前日期
    private Cursor mCursor;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();

    // 流量球效果控件
    private WaveView waveView;
    private WaveHelper mWaveHelper;
    // 设置边框颜色
    private int mBorderColor = Color.parseColor("#ef8750");
    // 设置边框宽度
    private int mBorderWidth = 0;
    // 设置背景波浪颜色 将背景波浪设为透明效果更佳
    private int behindWaveColor = Color.parseColor("#00000000");
    // 设置前景波浪颜色
    private int frontWaveColor = Color.parseColor("#696969");
    // 选择的波浪颜色
    private String selectedColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBehavior = MainActivity.behavior;

        // 根据类型加载布局
        if (mBehavior.equals(Constant.OUTCOME))
            mView = inflater.inflate(R.layout.main_outcome_viewpager, container, false);
        else if (mBehavior.equals(Constant.INCOME))
            mView = inflater.inflate(R.layout.main_income_viewpager, container, false);

        totalCostText = (TextView) mView.findViewById(R.id.total_cost);

        // 初始化recyclerView
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.main_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mDataAdapter = new MyDataAdapter(getContext());
        setRecyclerView();

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        surplusBudgeText = (TextView) getActivity().findViewById(R.id.surplus_budget);
        totalBudgetText = (TextView) getActivity().findViewById(R.id.total_budget);
        percentText = (TextView) getActivity().findViewById(R.id.percent);
        mToolbar = (Toolbar) getActivity().findViewById(main_toolbar);

        waveView = (WaveView) getActivity().findViewById(R.id.waveView);
        waveView.setBorder(mBorderWidth, mBorderColor);
        waveView.setWaveColor(behindWaveColor, frontWaveColor);

        mWaveHelper = new WaveHelper(waveView);
    }


    @Override
    public void onResume() {
        super.onResume();
        query();
        budget();
        mDataAdapter.setList(mList);
        mRecyclerView.setAdapter(mDataAdapter);
        mDataAdapter.notifyDataSetChanged();

        selectedColor = InitData.ballColor;
        frontWaveColor = Color.parseColor(selectedColor);
        waveView.setWaveColor(behindWaveColor, frontWaveColor);

        // 是否显示预算
        if (BudgetSettingActivity.isShowBudget) {
            getActivity().findViewById(R.id.main_budget_setting).setVisibility(View.VISIBLE);
            // 是否收入算入剩余预算
            if (!BudgetSettingActivity.isAddIncome) {
                if (mBehavior.equals(Constant.INCOME))
                    getActivity().findViewById(R.id.main_budget_setting).setVisibility(View.GONE);
            }
        } else {
            getActivity().findViewById(R.id.main_budget_setting).setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //        mWaveHelper.cancel();
    }

    /**
     * 设置日期
     *
     * @param position 当前fragment所在position
     */
    public void setDate(int position) {
        calendar = Calendar.getInstance();
        // 当前 - 相差天数
        calendar.add(Calendar.DAY_OF_MONTH, position - phaseDay);

        mDate = simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 查询当天所有数据
     */
    private void query() {

        mList.clear();

        mCursor = MyDatabaseHelper.getInstance(getContext()).query("Data", null, "date=? and behavior=?", new String[]{mDate, mBehavior}, null, null, null);
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
        String totalCostStr = formatNumber(decimalFormat.format(totalCost));
        totalCostText.setText(totalCostStr);
        totalCost = 0;

        mCursor.close();
    }

    /**
     * 设置recyclerView
     */
    private void setRecyclerView() {
        mDataAdapter.setOnItemClickListener(new MyDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = null;
                if (mBehavior.equals(Constant.OUTCOME))
                    intent = new Intent(getContext(), OutcomeDetailsActivity.class);
                else if (mBehavior.equals(Constant.INCOME))
                    intent = new Intent(getContext(), IncomeDetailsActivity.class);
                intent.putExtra("dataBean", mList.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * 预算的查询、计算和显示
     */
    private void budget() {

        String nowToolbarDate = mToolbar.getTitle().toString();
        String nowMonth = mDate.substring(0, mDate.length() - 3);

        // 查询预算总额
        mCursor = MyDatabaseHelper.getInstance(getContext()).query("Budget", null, "date = ?", new String[]{nowMonth}, null, null, null);
        while (mCursor.moveToNext()) {
            totalBudget = mCursor.getString(mCursor.getColumnIndex("total"));
            totalBudgetText.setText(totalBudget);
        }
        surplusBudget = totalBudget;
        surplus = Float.parseFloat(surplusBudget);
        mCursor.close();

        // 查找这个月所有收入或支出
        mCursor = MyDatabaseHelper.getInstance(getContext()).query("Data", null, "date like ? ", new String[]{nowMonth + "%"}, null, null, null);

        while (mCursor.moveToNext()) {
            DataBean dataBean = new DataBean();
            dataBean.setMoney(mCursor.getString(mCursor.getColumnIndex("money")));
            dataBean.setBehavior(mCursor.getString(mCursor.getColumnIndex("behavior")));
            // 计算剩余预算
            if (dataBean.getBehavior().equals(Constant.OUTCOME))
                surplus -= Float.parseFloat(dataBean.getMoney());
            if (dataBean.getBehavior().equals(Constant.INCOME)) {
                // 如果设置了将收入算入剩余预算
                if (BudgetSettingActivity.isAddIncome)
                    surplus += Float.parseFloat(dataBean.getMoney());
            }
        }

        // 舍弃小数点后为0的数字
        String surplusBudgetStr = formatNumber(decimalFormat.format(surplus));

        if (nowToolbarDate.substring(0, nowToolbarDate.length() - 3).equals(nowMonth)) {
            surplusBudgeText.setText("¥ " + surplusBudgetStr);

            // 计算百分比
            percent = (int) ((surplus / Integer.parseInt(totalBudget.trim())) * 100);
            if (percent < 0)
                percent = 0;
            percentText.setText(percent + "%");
        }

        // 设置流量球百分比
        mWaveHelper.setPercent(percent / 100f);
        mWaveHelper.start();

        mCursor.close();
    }

    /**
     * 格式化数字 舍弃小数点后为0的数字
     *
     * @param str 需要格式化的字符串
     * @return
     */
    private String formatNumber(String str) {
        if (str.endsWith("0"))
            str = str.substring(0, str.length() - 1);
        if (str.endsWith(".0"))
            str = str.substring(0, str.length() - 2);

        return str;
    }
}
