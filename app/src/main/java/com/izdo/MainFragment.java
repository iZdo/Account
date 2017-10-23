package com.izdo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaychan.viewlib.NumberRunningTextView;
import com.gelitenight.waveview.library.WaveView;
import com.izdo.Adapter.MyDataAdapter;
import com.izdo.Bean.DataBean;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.Constant;
import com.izdo.Util.InitData;
import com.izdo.Util.WaveHelper;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private NumberRunningTextView surplusBudgeText;
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
    private LinearLayoutManager mLayoutManager;

    // 是否位移
    private static boolean isAnimationEnd = false;
    private ObjectAnimator animator;
    // 存储长按的item的imageView和textView
    private static ImageView tmpButton;
    private static TextView tmpTextView;
    // 上一个点击的position
    private static int lastPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBehavior = MainActivity.behavior;

        // 根据类型加载布局
        if (mBehavior.equals(Constant.OUTCOME))
            mView = inflater.inflate(R.layout.main_outcome_viewpager, container, false);
        else if (mBehavior.equals(Constant.INCOME))
            mView = inflater.inflate(R.layout.main_income_viewpager, container, false);

        totalCostText = (TextView) mView.findViewById(R.id.total_cost);

        setRecyclerView();

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        surplusBudgeText = (NumberRunningTextView) getActivity().findViewById(R.id.surplus_budget);
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

        setIsAnimationEnd();
    }

    /**
     * 是否已执行过动画
     */
    private void setIsAnimationEnd() {
        if (isAnimationEnd) {
            ObjectAnimator.ofFloat(tmpButton, "rotationY", 90f, 0f)
                    .setDuration(500).start();
            tmpButton.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(tmpTextView, "translationX", tmpTextView.getTranslationX(), tmpTextView.getTranslationX() + 100f)
                    .setDuration(500).start();
            isAnimationEnd = false;
        }
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

        mDate = getFormatDate(calendar);
    }

    /**
     * 设置recyclerView相关事件
     */
    private void setRecyclerView() {
        // 初始化recyclerView
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.main_recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDataAdapter = new MyDataAdapter(getContext());

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setIsAnimationEnd();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setIsAnimationEnd();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

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

        mDataAdapter.setOnItemLongClickListener(new MyDataAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                View positionItem = mLayoutManager.findViewByPosition(position);
                final TextView text = (TextView) positionItem.findViewById(R.id.main_listView_money_text);
                final ImageView deleteButton = (ImageView) positionItem.findViewById(R.id.item_delete);

                if (!(position == lastPosition)) {
                    setIsAnimationEnd();
                }

                // 判断是否已经位移
                if (!isAnimationEnd) {
                    animator = ObjectAnimator.ofFloat(text, "translationX", text.getTranslationX(), text.getTranslationX() - 100f);
                } else {
                    animator = ObjectAnimator.ofFloat(text, "translationX", text.getTranslationX(), text.getTranslationX() + 100f, text.getTranslationX());
                }
                animator.setDuration(500);
                animator.start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        deleteButton.setVisibility(View.VISIBLE);
                        isAnimationEnd = true;
                        ObjectAnimator.ofFloat(deleteButton, "rotationY", 90f, 0f)
                                .setDuration(500).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 防止多次点击
                        deleteButton.setClickable(false);
                        // 删除记录
                        MyDatabaseHelper.getInstance(getContext()).delete("Data", "id=?", new String[]{mList.get(position).getId() + ""});
                        mList.remove(position);
                        mDataAdapter.notifyItemRemoved(position);
                        mDataAdapter.notifyItemRangeChanged(0, mList.size());
                        isAnimationEnd = false;

                        // 重新计算剩余预算
                        budget();
                    }
                });

                // 保存上一次点击的相关控件
                lastPosition = position;
                tmpButton = deleteButton;
                tmpTextView = text;
            }
        });
    }

    /**
     * 查询当天所有数据
     */
    private void query() {

        mList.clear();

        queryFixedRecord();

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
            dataBean.setFixedRecord_id(mCursor.getInt(mCursor.getColumnIndex("fixedRecord_id")));
            mList.add(dataBean);
        }

        mCursor.close();
    }

    /**
     * 预算的查询、计算和显示
     */
    private void budget() {

        // 计算总支出
        mCursor = MyDatabaseHelper.getInstance(getContext()).query("Data", null, "date=? and behavior=?", new String[]{mDate, mBehavior}, null, null, null);
        while (mCursor.moveToNext())
            totalCost += Float.parseFloat(mCursor.getString(mCursor.getColumnIndex("money")));
        mCursor.close();

        // 显示总支出/收入金额
        String totalCostStr = formatNumber(decimalFormat.format(totalCost));
        totalCostText.setText(totalCostStr);
        totalCost = 0;

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
            TextView sign = (TextView) getActivity().findViewById(R.id.sign);
            if (surplusBudgetStr.startsWith("-")) {
                sign.setText("¥-");
            }else{
                sign.setText("¥");
            }
            surplusBudgeText.setContent(surplusBudgetStr);

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

    // 获取当前日期 yyyy-MM-hh
    private String getFormatDate(Calendar calendar) {
        // 将calendar的时间转换为yyyy-MM-hh格式
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 查询是否有固定记录
     */
    private void queryFixedRecord() {
        // 查找当前behavior是否有固定支出项
        mCursor = MyDatabaseHelper.getInstance(getContext()).query("FixedRecord", null, "behavior=?", new String[]{mBehavior}, null, null, null);
        // 当有固定支出项时,判断今天是否已添加,直接获取already_date的值 用当前系统时间减去差值
        while (mCursor.moveToNext()) {
            try {
                // 获取最后添加的日期
                String already_date = mCursor.getString(mCursor.getColumnIndex("already_date"));
                ContentValues values = new ContentValues();
                Calendar tmp_calendar = Calendar.getInstance();
                tmp_calendar.setTime(new Date(simpleDateFormat.parse(already_date).getTime()));
                // 计算相差天数
                int day = (int) ((Calendar.getInstance().getTimeInMillis() - simpleDateFormat.parse(already_date).getTime()) / (24 * 60 * 60 * 1000));

                // 若day > 0 , 再判断当前是周期是每日,每周还是每月
                if (day > 0) {
                    switch (mCursor.getString(mCursor.getColumnIndex("fixed_charge"))) {
                        case "每日":
                            for (int i = 0; i < day; i++) {
                                // 日期+1
                                tmp_calendar.add(Calendar.DAY_OF_MONTH, 1);
                                already_date = insertFixedRecord(values, tmp_calendar);
                            }
                            updateAlreadyDate(already_date, values);
                            break;
                        case "每周":
                            for (int i = 7; i <= day; i += 7) {
                                // 日期+7
                                tmp_calendar.add(Calendar.DAY_OF_MONTH, 7);
                                already_date = insertFixedRecord(values, tmp_calendar);
                            }
                            // 更新最新已添加数据日期
                            updateAlreadyDate(already_date, values);
                            break;
                        case "每月":
                            for (tmp_calendar.add(Calendar.MONTH, 1);
                                 tmp_calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis();
                                 tmp_calendar.add(Calendar.MONTH, 1)) {
                                already_date = insertFixedRecord(values, tmp_calendar);
                            }
                            // 更新最新已添加数据日期
                            updateAlreadyDate(already_date, values);
                            break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        mCursor.close();
    }

    private String insertFixedRecord(ContentValues values, Calendar tmp_calendar) {
        String already_date;
        already_date = getFormatDate(tmp_calendar);

        values.put("money", mCursor.getInt(mCursor.getColumnIndex("money")));
        values.put("type", mCursor.getString(mCursor.getColumnIndex("type")));
        values.put("describe", mCursor.getString(mCursor.getColumnIndex("describe")));
        values.put("account", mCursor.getString(mCursor.getColumnIndex("account")));
        values.put("fixed_charge", mCursor.getString(mCursor.getColumnIndex("fixed_charge")));
        values.put("date", already_date);
        values.put("behavior", mCursor.getString(mCursor.getColumnIndex("behavior")));
        values.put("fixedRecord_id", mCursor.getInt(mCursor.getColumnIndex("fixedRecord_id")));

        // 插入新数据
        MyDatabaseHelper.getInstance(getContext()).insert("Data", null, values);
        values.clear();
        return already_date;
    }

    private void updateAlreadyDate(String already_date, ContentValues values) {
        // 更新最新已添加数据日期
        values.put("already_date", already_date);
        MyDatabaseHelper.getInstance(getContext()).update("FixedRecord", values,
                "fixedRecord_id = ?", new String[]{mCursor.getInt(mCursor.getColumnIndex("fixedRecord_id")) + ""});
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
