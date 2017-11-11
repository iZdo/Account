package com.izdo;

import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chaychan.viewlib.NumberRunningTextView;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.Constant;
import com.izdo.Util.InitData;
import com.izdo.Util.MyDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by iZdo on 2017/11/7.
 */

public class StatisticsBudgetFragment extends Fragment implements View.OnClickListener {

    private HorizontalBarChart mChart;
    private TextView dateText;
    private TextView pre;
    private TextView thisMonth;
    private TextView halfAYear;
    private TextView oneYear;
    private TextView userDefined;
    private TextView allOutcome;
    private TextView allBudget;
    private TextView allIncome;
    private ImageView image;
    private TextView sign;
    private NumberRunningTextView surplusBudget;

    String mString[] = {"收入", "预算", "支出"};
    int colors[] = {Color.parseColor("#9ac93e"), Color.parseColor("#ef8750"), Color.parseColor("#E6534B")};

    private FragmentActivity mActivity;
    private int dateType = Constant.THIS_MONTH;
    private Cursor cursor = null;
    private int count = 0;

    // 日期选择器弹出窗口
    private DatePicker mDatePicker;
    private View mDatePickerView;
    private PopupWindow mPopupWindow;
    private Button confirm;
    private Button cancel;
    private MyDialog mDialog;
    private Calendar calendar = Calendar.getInstance();

    private String beginOrEnd;
    private Button beginDateButton;
    private Button endDateButton;

    // 日期选择器选择的日期
    private int newYear;
    private int newMonth;
    private int newDay;
    private String beginDate;
    private String endDate;

    private List<Float> sumList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_budget_fragment, container, false);
        mChart = (HorizontalBarChart) view.findViewById(R.id.horizontal_bar_chart);

        dateText = (TextView) view.findViewById(R.id.date);
        pre = (TextView) view.findViewById(R.id.pre);

        thisMonth = (TextView) view.findViewById(R.id.this_month);
        halfAYear = (TextView) view.findViewById(R.id.half_a_year);
        oneYear = (TextView) view.findViewById(R.id.one_year);
        userDefined = (TextView) view.findViewById(R.id.user_defined);
        allOutcome = (TextView) view.findViewById(R.id.all_outcome);
        allBudget = (TextView) view.findViewById(R.id.all_budget);
        allIncome = (TextView) view.findViewById(R.id.all_income);
        image = (ImageView) view.findViewById(R.id.statistics_image);
        sign = (TextView) view.findViewById(R.id.sign);
        surplusBudget = (NumberRunningTextView) view.findViewById(R.id.surplus_budget);

        pre.setOnClickListener(this);
        thisMonth.setOnClickListener(this);
        halfAYear.setOnClickListener(this);
        oneYear.setOnClickListener(this);
        userDefined.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChart != null) {
            mChart.animateY(2500);
        }
    }

    private void init() {
        mActivity = getActivity();

        thisMonth.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainPressed));
        thisMonth.setClickable(false);

        initChart();
    }

    private void initChart() {
        // description不可见
        mChart.getDescription().setEnabled(false);
        // 不可缩放
        mChart.setPinchZoom(false);
        mChart.setScaleEnabled(false);

        mChart.getXAxis().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);

        mChart.getAxisLeft().setAxisMinValue(0f);
        mChart.getAxisRight().setAxisMinValue(0f);

        mChart.setFitBars(true);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(30f);
        l.setYEntrySpace(0f);
        l.setYOffset(10f);
        l.setFormSize(15f);
        l.setTextSize(15f);

        setData();
    }

    private void setData() {

        float spaceForBar = 20f;

        queryData();
        calcSurplusBudget();

        allOutcome.setText(sumList.get(2) + "元");
        allBudget.setText(sumList.get(1) + "元");
        allIncome.setText(sumList.get(0) + "元");

        List<IBarDataSet> dataSets = new ArrayList<>();

        for (int i = 0; i < sumList.size(); i++) {
            ArrayList<BarEntry> valueSet = new ArrayList<>();
            valueSet.add(new BarEntry(i * spaceForBar, sumList.get(i)));
            BarDataSet barDataSet = new BarDataSet(valueSet, mString[i]);
            barDataSet.setColor(colors[i]);
            barDataSet.setDrawValues(false);
            // 点击不高亮
            barDataSet.setHighlightEnabled(false);
            dataSets.add(barDataSet);
        }

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(15f);
        mChart.setData(data);
        mChart.animateY(1500);

    }

    /**
     * 查询数据
     */
    private void queryData() {
        // 清空数据
        sumList.clear();

        final Calendar calendar = Calendar.getInstance();
        String mDate;
        mDate = getFormatDate(calendar);

        List<Cursor> outComeCursorList = new ArrayList<>();
        List<Cursor> budgetCursorList = new ArrayList<>();
        List<Cursor> inComeCursorList = new ArrayList<>();

        if (dateType == Constant.THIS_MONTH) {
            //            mDate = mDate.substring(0, mDate.length() - 3);
            // 查询收入
            cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                    new String[]{mDate + "%", Constant.INCOME}, null, null, null);
            if (cursor.getCount() != 0)
                inComeCursorList.add(cursor);

            // 查询预算
            cursor = MyDatabaseHelper.getInstance(mActivity).query("Budget", null, "date = ?",
                    new String[]{mDate}, null, null, null);
            if (cursor.getCount() != 0)
                budgetCursorList.add(cursor);

            // 查询支出
            cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                    new String[]{mDate + "%", Constant.OUTCOME}, null, null, null);
            if (cursor.getCount() != 0)
                outComeCursorList.add(cursor);

            // 设置日期文本
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            beginDate = getFormatDate(calendar);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDate = getFormatDate(calendar);

        } else if (dateType == Constant.HALF_A_YEAR) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDate = getFormatDate(calendar);

            calendar.add(Calendar.MONTH, -5);
            mDate = getFormatDate(calendar);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            beginDate = getFormatDate(calendar);

            for (int i = 0; i < 6; i++) {
                Calendar newCalendar = calendar;
                //                mDate = mDate.substring(0, mDate.length() - 3);
                // 查询收入
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                        new String[]{mDate + "%", Constant.INCOME}, null, null, null);
                if (cursor.getCount() != 0)
                    inComeCursorList.add(cursor);
                // 查询预算
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Budget", null, "date = ?",
                        new String[]{mDate}, null, null, null);
                if (cursor.getCount() != 0) {
                    budgetCursorList.add(cursor);
                } else
                    count++;

                // 查询支出
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                        new String[]{mDate + "%", Constant.OUTCOME}, null, null, null);
                if (cursor.getCount() != 0)
                    outComeCursorList.add(cursor);

                newCalendar.add(Calendar.MONTH, 1);
                mDate = getFormatDate(calendar);
            }

        } else if (dateType == Constant.ONE_YEAR) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDate = getFormatDate(calendar);

            calendar.add(Calendar.MONTH, -11);
            mDate = getFormatDate(calendar);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            beginDate = getFormatDate(calendar);

            for (int i = 0; i < 12; i++) {
                Calendar newCalendar = calendar;
                //                mDate = mDate.substring(0, mDate.length() - 3);
                // 查询收入
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                        new String[]{mDate + "%", Constant.INCOME}, null, null, null);
                if (cursor.getCount() != 0)
                    inComeCursorList.add(cursor);
                // 查询预算
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Budget", null, "date = ?",
                        new String[]{mDate}, null, null, null);
                if (cursor.getCount() != 0) {
                    budgetCursorList.add(cursor);
                } else
                    count++;

                // 查询支出
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                        new String[]{mDate + "%", Constant.OUTCOME}, null, null, null);
                if (cursor.getCount() != 0)
                    outComeCursorList.add(cursor);

                newCalendar.add(Calendar.MONTH, 1);
                mDate = getFormatDate(calendar);
            }

        } else if (dateType == Constant.USER_DEFINED) {
            // 查询两个日期之间的数据
            // 计算两个日期之间相差多少个月
            int phaseMonth = calcPhaseMonth(beginDate, endDate);

            // 查询收入
            cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date between ? and ? and behavior = ?",
                    new String[]{beginDate + "-01", endDate + "-31", Constant.INCOME}, null, null, null);
            if (cursor.getCount() != 0)
                inComeCursorList.add(cursor);

            // 查询预算
            cursor = MyDatabaseHelper.getInstance(mActivity).query("Budget", null, "date between ? and ? ",
                    new String[]{beginDate, endDate}, null, null, null);
            if (phaseMonth - cursor.getCount() > 0)
                count = phaseMonth - cursor.getCount();
            budgetCursorList.add(cursor);

            // 查询支出
            cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date between ? and ? and behavior = ?",
                    new String[]{beginDate + "-01", endDate + "-31", Constant.OUTCOME}, null, null, null);
            if (cursor.getCount() != 0)
                outComeCursorList.add(cursor);
        }

        dealData(inComeCursorList, Constant.INCOME);
        dealData(budgetCursorList, Constant.TOTAL);
        dealData(outComeCursorList, Constant.OUTCOME);

        dateText.setText(beginDate + "~" + endDate);
    }

    private void dealData(List<Cursor> cursorList, String type) {
        if (cursorList == null) return;

        float sum = 0f;

        if (!type.equals(Constant.TOTAL)) type = "money";

        for (Cursor cursor : cursorList) {
            while (cursor.moveToNext()) {
                Float money = Float.parseFloat(cursor.getString(cursor.getColumnIndex(type)));
                if (type.equals(Constant.TOTAL)) {
                    sum += count * 1000f;
                    count = 0;
                }
                sum += money;
                sum = (float) (Math.round(sum * 100)) / 100;
            }
        }

        sumList.add(sum);
    }

    /**
     * 计算剩余预算
     */
    private void calcSurplusBudget() {

        float surplus;


        // 如果加入收入
        if (InitData.isAddIncome) {
            surplus = (Math.round((sumList.get(1) - sumList.get(2) + sumList.get(0)) * 100)) / 100;
        } else {
            surplus = (Math.round((sumList.get(1) - sumList.get(2) + sumList.get(0)) * 100)) / 100;
        }
        ObjectAnimator.ofFloat(image, "rotationY", 0f, 360f)
                .setDuration(1500).start();
        if (surplus < 0) {
            sign.setVisibility(View.VISIBLE);
            image.setBackgroundResource(R.drawable.unhappy);
        } else if (surplus == 0) {
            sign.setVisibility(View.GONE);
            image.setBackgroundResource(R.drawable.smile);
        } else {
            sign.setVisibility(View.GONE);
            image.setBackgroundResource(R.drawable.laugh);
        }
        surplusBudget.setContent(surplus + "");
    }

    private int calcPhaseMonth(String beginDate, String endDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        try {
            begin.setTime(sdf.parse(beginDate));
            end.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int result = end.get(Calendar.MONTH) - begin.get(Calendar.MONTH);
        int month = (end.get(Calendar.YEAR) - begin.get(Calendar.YEAR)) * 12;

        return result + month;
    }

    // 获取当前日期 yyyy-MM-dd
    private String getFormatDate(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        // 将calendar的时间转换为yyyy-MM-dd格式
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 恢复选项颜色
     */
    private void recoverColor() {
        thisMonth.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainNormal));
        halfAYear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainNormal));
        oneYear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainNormal));
        userDefined.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainNormal));

        thisMonth.setClickable(true);
        halfAYear.setClickable(true);
        oneYear.setClickable(true);
        userDefined.setClickable(true);
    }

    /**
     * 显示datePicker
     */
    private void showDatePicker(String date) {
        int year;
        int month;
        //        int day;

        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(5, 7)) - 1;
        //        day = Integer.parseInt(date.substring(8, 10));

        // 初始化popupWindow
        mDatePickerView = LayoutInflater.from(mActivity).inflate(R.layout.datepicker, null);
        mPopupWindow = new PopupWindow(mDatePickerView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mDatePicker = (DatePicker) mDatePickerView.findViewById(R.id.datePicker);

        // 初始化datePicker
        mDatePicker.init(year, month, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                setSelectDay(year, month, day);
            }
        });

        // 确认取消按钮
        confirm = (Button) mDatePickerView.findViewById(R.id.datePicker_confirm);
        cancel = (Button) mDatePickerView.findViewById(R.id.datePicker_cancel);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        // popupWindow外部区域不可点击
        mPopupWindow.setOutsideTouchable(false);
        // 不可获取焦点
        mPopupWindow.setFocusable(false);
        // 设置动画
        mPopupWindow.setAnimationStyle(R.style.date_popupWindow_anim_style);
        // 设置显示位置
        mPopupWindow.showAtLocation(mDialog.findViewById(R.id.dialog_layout), Gravity.BOTTOM, 0, 0);
        // 显示弹出窗口
        mPopupWindow.showAsDropDown(mDatePickerView);
    }

    /**
     * 设置新选择的日期
     *
     * @param newYear
     * @param newMonth
     * @param newDay
     */
    private void setSelectDay(int newYear, int newMonth, int newDay) {
        this.newYear = newYear;
        this.newMonth = newMonth;
        this.newDay = newDay;
    }

    private void showSelectDateDialog() {
        mDialog = new MyDialog(mActivity, R.style.dialog_style);
        mDialog.initDateDialog();
        mDialog.setCancelable(false);
        beginDateButton = (Button) mDialog.findViewById(R.id.begin_date_button);
        endDateButton = (Button) mDialog.findViewById(R.id.end_date_button);

        String date = dateText.getText().toString();

        beginDate = date.substring(0, 7);
        endDate = date.substring(date.length() - 7, date.length());

        beginDateButton.setText(beginDate);
        endDateButton.setText(endDate);

        beginDateButton.setOnClickListener(this);
        endDateButton.setOnClickListener(this);
        mDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(this);

        mDialog.show();
    }

    /**
     * 比较两个日期前后
     *
     * @return
     */
    private int compareDate() {
        Calendar anotherCalendar = Calendar.getInstance();

        calendar.set(Integer.parseInt(beginDate.substring(0, 4)),
                Integer.parseInt(beginDate.substring(5, 7)) - 1,
                1);


        anotherCalendar.set(Integer.parseInt(endDate.substring(0, 4)),
                Integer.parseInt(endDate.substring(5, 7)) - 1,
                1);

        return calendar.compareTo(anotherCalendar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pre:
                ViewPager viewpager = (ViewPager) mActivity.findViewById(R.id.statistics_viewpager);
                viewpager.setCurrentItem(1);
                viewpager.getAdapter().notifyDataSetChanged();
                break;
            case R.id.this_month:
                recoverColor();
                thisMonth.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainPressed));
                thisMonth.setClickable(false);

                dateType = Constant.THIS_MONTH;
                setData();
                break;
            case R.id.half_a_year:
                recoverColor();
                halfAYear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainPressed));
                halfAYear.setClickable(false);

                dateType = Constant.HALF_A_YEAR;
                setData();
                break;
            case R.id.one_year:
                recoverColor();
                oneYear.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainPressed));
                oneYear.setClickable(false);

                dateType = Constant.ONE_YEAR;
                setData();
                break;
            case R.id.user_defined:
                dateType = Constant.USER_DEFINED;
                showSelectDateDialog();
                break;
            case R.id.begin_date_button:
                beginOrEnd = Constant.BEGIN;
                showDatePicker(beginDateButton.getText().toString());
                break;
            case R.id.end_date_button:
                beginOrEnd = Constant.END;
                showDatePicker(endDateButton.getText().toString());
                break;
            case R.id.dialog_select_confirm:
                recoverColor();
                userDefined.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainPressed));
                setData();
                mDialog.dismiss();
                break;
            case R.id.datePicker_confirm:
                calendar.set(newYear, newMonth, newDay);
                if (beginOrEnd.equals(Constant.BEGIN)) {
                    beginDateButton.setText(getFormatDate(calendar));
                    beginDate = getFormatDate(calendar);
                    // 起始日期不能大于结束日期
                    if (compareDate() == 1) {
                        endDateButton.setText(beginDate);
                        endDate = beginDate;
                    }
                } else if (beginOrEnd.equals(Constant.END)) {
                    endDateButton.setText(getFormatDate(calendar));
                    endDate = getFormatDate(calendar);
                    // 起始日期不能大于结束日期
                    if (compareDate() == 1) {
                        beginDateButton.setText(endDate);
                        beginDate = endDate;
                    }
                }
                mPopupWindow.dismiss();
                break;
            case R.id.datePicker_cancel:
                mPopupWindow.dismiss();
                break;
        }
    }
}
