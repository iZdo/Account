package com.izdo;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.izdo.Adapter.MyStatisticsAdapter;
import com.izdo.Bean.DataBean;
import com.izdo.Bean.StatisticsBean;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.Constant;
import com.izdo.Util.MyDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.izdo.R.id.date;

/**
 * Created by iZdo on 2017/11/7.
 */

public class StatisticsFragment extends Fragment implements View.OnClickListener {

    private View view;

    private TextView dateText;
    private TextView thisMonth;
    private TextView halfAYear;
    private TextView oneYear;
    private TextView userDefined;

    private PieChart mChart;
    private List<Float> moneyList = new ArrayList<>();
    private List<Float> percentList = new ArrayList<>();
    private List<String> typeList = new ArrayList<>();
    private List<StatisticsBean> mDataList = new ArrayList<>();
    private List<DataBean> mDataBeanList = new ArrayList<>();
    private float sum = 0;
    private RecyclerView mRecyclerView;

    private int dateType = Constant.THIS_MONTH;
    private FragmentActivity mActivity;
    public String fragmentType;

    private Cursor cursor = null;
    private MyStatisticsAdapter mAdapter;

    // 日期选择器弹出窗口
    private DatePicker mDatePicker;
    private View mDatePickerView;
    private PopupWindow mPopupWindow;
    private Button confirm;
    private Button cancel;
    private MyDialog mDialog;
    private Calendar calendar = Calendar.getInstance();

    // 日期选择器选择的日期
    private int newYear;
    private int newMonth;
    private int newDay;
    private String beginDate;
    private String endDate;

    private String beginOrEnd;
    private Button beginDateButton;
    private Button endDateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            fragmentType = arguments.getString("fragmentType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.statistics_fragment, container, false);
        mChart = (PieChart) view.findViewById(R.id.pie_chart);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.statistics_recycleView);

        thisMonth = (TextView) view.findViewById(R.id.this_month);
        halfAYear = (TextView) view.findViewById(R.id.half_a_year);
        oneYear = (TextView) view.findViewById(R.id.one_year);
        userDefined = (TextView) view.findViewById(R.id.user_defined);


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
    }

    public static StatisticsFragment newInstance(String fragmentType) {
        Bundle bundle = new Bundle();
        bundle.putString("fragmentType", fragmentType);
        StatisticsFragment fragment = new StatisticsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void init() {
        mActivity = getActivity();
        dateText = (TextView) mActivity.findViewById(date);

        thisMonth.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMainPressed));
        thisMonth.setClickable(false);

        initChart();
        initRecyclerView();
    }

    private void initChart() {
        // 是否使用百分比
        mChart.setUsePercentValues(true);
        // 描述信息
        mChart.getDescription().setEnabled(false);
        // mChart距离屏幕的距离
        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        // 是否空心
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.parseColor("#edead1"));

        mChart.setRotationEnabled(true);
        mChart.setRotationAngle(20);

        Legend l = mChart.getLegend();
        // 位于顶部
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        // 位于右方
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        // 垂直排列
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // 设置标签
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);

        setData();
    }

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyStatisticsAdapter(getContext(), mDataList);
        mAdapter.setOnItemClickListener(new MyStatisticsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MyDialog myDialog = new MyDialog(getContext(), R.style.dialog_style);
                myDialog.initAccountOrFixedChargeOrStatisticsDialog(mDataList.get(position).getType(),
                        dealList(mDataBeanList, mDataList.get(position).getType()), Constant.STATISTICS);
                myDialog.show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 挑出当前点击的item的类型的数据
     *
     * @param dataBeanList
     * @param type
     * @return
     */
    private List<DataBean> dealList(List<DataBean> dataBeanList, String type) {
        List<DataBean> newList = new ArrayList();

        for (DataBean dataBean : dataBeanList)
            if (dataBean.getType().equals(type))
                newList.add(dataBean);

        return newList;
    }

    public void setData() {
        queryData();

        List<PieEntry> data = new ArrayList<>();

        for (int i = 0; i < mDataList.size(); i++) {
            data.add(new PieEntry(mDataList.get(i).getPercent(), mDataList.get(i).getType()));
        }

        PieDataSet dataSet = new PieDataSet(data, "");

        dataSet.setColors(setColors());
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10);
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueFormatter(new PercentFormatter());

        mChart.setCenterText(generateCenterSpannableText());
        mChart.setData(pieData);
        mChart.animateXY(1500, 1500);
        mChart.invalidate();

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private SpannableString generateCenterSpannableText() {
        String flag = "";
        if (fragmentType.equals(Constant.OUTCOME)) flag = "支出";
        else if (fragmentType.equals(Constant.INCOME)) flag = "收入";
        SpannableString s = new SpannableString("总" + flag + "\n" + sum + "元");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 3, 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), 3, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 3, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.8f), 3, s.length(), 0);
        return s;
    }

    /**
     * 添加颜色
     */
    private ArrayList setColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        // 添加颜色
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        return colors;
    }

    /**
     * 清空数据集合
     */
    private void clearList() {
        moneyList.clear();
        percentList.clear();
        typeList.clear();
        mDataList.clear();
        mDataBeanList.clear();
    }

    /**
     * 查询数据
     */
    private void queryData() {
        // 清空数据
        clearList();

        final Calendar calendar = Calendar.getInstance();
        String mDate;
        final List<Cursor> cursorList = new ArrayList<>();
        mDate = getFormatDate(calendar);

        if (dateType == Constant.THIS_MONTH) {
            mDate = mDate.substring(0, mDate.length() - 3);
            cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                    new String[]{mDate + "%", fragmentType}, null, null, null);
            cursorList.add(cursor);

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
                mDate = mDate.substring(0, mDate.length() - 3);
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                        new String[]{mDate + "%", fragmentType}, null, null, null);
                cursorList.add(cursor);

                calendar.add(Calendar.MONTH, 1);
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
                mDate = mDate.substring(0, mDate.length() - 3);
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date like ? and behavior = ?",
                        new String[]{mDate + "%", fragmentType}, null, null, null);
                cursorList.add(cursor);

                calendar.add(Calendar.MONTH, 1);
                mDate = getFormatDate(calendar);
            }
        } else if (dateType == Constant.USER_DEFINED) {
            int year;
            int month;
            int day;

            year = Integer.parseInt(beginDate.substring(0, 4));
            month = Integer.parseInt(beginDate.substring(5, 7)) - 1;
            day = Integer.parseInt(beginDate.substring(8, 10));

            calendar.set(year, month, day);

            while (compareDate(calendar) <= 0) {
                cursor = MyDatabaseHelper.getInstance(mActivity).query("Data", null, "date = ? and behavior = ?",
                        new String[]{getFormatDate(calendar), fragmentType}, null, null, null);
                cursorList.add(cursor);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        dateText.setText(beginDate + "~" + endDate);
        dealData(cursorList);
    }

    private void dealData(List<Cursor> cursorList) {
        if (cursorList == null) return;

        sum = 0f;

        for (Cursor cursor : cursorList) {
            while (cursor.moveToNext()) {
                DataBean dataBean = new DataBean();
                Float money = Float.parseFloat(cursor.getString(cursor.getColumnIndex("money")));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String account = cursor.getString(cursor.getColumnIndex("account"));
                dataBean.setMoney(money + "");
                dataBean.setType(type);
                dataBean.setDate(date);
                dataBean.setAccount(account);
                mDataBeanList.add(dataBean);
                // 如果已有此类型
                if (typeList.contains(type)) {
                    // 找到此类型在typeList中索引
                    int index = typeList.indexOf(type);
                    // 修改moneys数组索引对应的金额
                    moneyList.set(index, moneyList.get(index) + money);
                } else {
                    typeList.add(type);
                    moneyList.add(money);
                }
                sum += money;
                sum = (float) (Math.round(sum * 100)) / 100;
            }
        }

        for (int i = 0; i < moneyList.size(); i++) {
            percentList.add(moneyList.get(i) / sum * 100);
        }

        setDataList();
    }

    private void setDataList() {
        for (int i = 0; i < typeList.size(); i++) {
            StatisticsBean bean = new StatisticsBean();
            bean.setPercent(percentList.get(i));
            bean.setMoney(moneyList.get(i));
            bean.setType(typeList.get(i));
            mDataList.add(bean);
        }

        Collections.sort(mDataList, new Comparator<StatisticsBean>() {
            @Override
            public int compare(StatisticsBean s1, StatisticsBean s2) {
                return s2.getPercent() > s1.getPercent() ? 1 :
                        (s2.getPercent() == s1.getPercent()) ? 0 : -1;
            }
        });
    }

    // 获取当前日期 yyyy-MM-hh
    private String getFormatDate(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 将calendar的时间转换为yyyy-MM-hh格式
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
        int day;

        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(5, 7)) - 1;
        day = Integer.parseInt(date.substring(8, 10));

        // 初始化popupWindow
        mDatePickerView = LayoutInflater.from(mActivity).inflate(R.layout.datepicker, null);
        mPopupWindow = new PopupWindow(mDatePickerView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mDatePicker = (DatePicker) mDatePickerView.findViewById(R.id.datePicker);

        // 初始化datePicker
        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
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

        beginDate = date.substring(0, 10);
        endDate = date.substring(date.length() - 10, date.length());

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
    private int compareDate(Calendar calendar) {
        Calendar anotherCalendar = Calendar.getInstance();

        anotherCalendar.set(Integer.parseInt(endDate.substring(0, 4)),
                Integer.parseInt(endDate.substring(5, 7)) - 1,
                Integer.parseInt(endDate.substring(8, 10)));

        return calendar.compareTo(anotherCalendar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                userDefined.setClickable(false);
                setData();
                mDialog.dismiss();
                break;
            case R.id.datePicker_confirm:
                calendar.set(newYear, newMonth, newDay);
                if (beginOrEnd.equals(Constant.BEGIN)) {
                    beginDateButton.setText(getFormatDate(calendar));
                    beginDate = getFormatDate(calendar);
                    // 起始日期不能大于结束日期
                    if (compareDate(calendar) == 1) {
                        endDateButton.setText(getFormatDate(calendar));
                        endDate = getFormatDate(calendar);
                    }
                } else if (beginOrEnd.equals(Constant.END)) {
                    endDateButton.setText(getFormatDate(calendar));
                    endDate = getFormatDate(calendar);
                    // 起始日期不能大于结束日期
                    if (compareDate(calendar) == -1) {
                        beginDateButton.setText(getFormatDate(calendar));
                        beginDate = getFormatDate(calendar);
                    }
                }
                mPopupWindow.dismiss();
                break;
            case R.id.datePicker_cancel:
                mPopupWindow.dismiss();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
