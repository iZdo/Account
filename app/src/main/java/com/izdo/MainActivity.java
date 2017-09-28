package com.izdo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.izdo.Adapter.MyFragmentPagerAdapter;
import com.izdo.DataBase.MyDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * iZdo
 * 2017/3/19
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar main_toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    // 判断滑动菜单是否打开
    private Boolean isOpen = false;
    // Toolbar按钮
    private MenuItem mCalendarItem;
    private MenuItem mAdd;

    // 日期选择器弹出窗口
    private View mDatePickerView;
    private PopupWindow mPopupWindow;
    private Button confirm;
    private Button cancel;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();

    // 日期选择器选择的日期
    private int newYear = calendar.get(Calendar.YEAR);
    private int newMonth = calendar.get(Calendar.MONTH);
    private int newDay = calendar.get(Calendar.DAY_OF_MONTH);
    private String date = simpleDateFormat.format(calendar.getTime());

    private ViewPager mViewPager;
    private ArrayList<MainFragment> mViewList;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    // 记录当前日期
    private String nowDate = date;
    private DatePicker mDatePicker;

    // 预算控件
    private RelativeLayout budgetSetting;
    private TextView totalBudget;
    private MyDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    // 当前滑动菜单item
    public static String behavior = "outcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        budgetSetting = (RelativeLayout) findViewById(R.id.main_budget_setting);
        totalBudget = (TextView) findViewById(R.id.total_budget);

        mDatabaseHelper = new MyDatabaseHelper(this, "Account.db", null, 1);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();

        main_toolbar.setTitle(date);
        setSupportActionBar(main_toolbar);
        budgetSetting.setOnClickListener(this);
        drawerMenu();

        String mMonth = main_toolbar.getTitle().toString().substring(0, main_toolbar.getTitle().toString().length() - 3);
        Cursor cursor = mSQLiteDatabase.query("Budget", null, "date = ? ", new String[]{mMonth}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            // 添加数据
            values.put("total", "1000");
            values.put("date", mMonth);
            mSQLiteDatabase.insert("Budget", null, values);
        }

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewList = new ArrayList<>();

        setViewPager();

        //        showBudget();
    }

    // TODO
    // 显示预算
    //    private void showBudget() {
    //        String query = main_toolbar.getTitle().toString();
    //        query = query.substring(0, query.length() - 3);
    //        Cursor cursor = mSQLiteDatabase.query("Budget", null, "date = ? ", new String[]{query}, null, null, null);
    //        while (cursor.moveToNext()) {
    //            totalBudget.setText(cursor.getString(cursor.getColumnIndex("total")));
    //            surplusBudget.setText(cursor.getString(cursor.getColumnIndex("surplus")));
    //        }
    //    }

    // 为ViewPager添加数据并设置适配器和监听器
    private void setViewPager() {
        date = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, -100);

        mViewList.clear();

        for (int i = 0; i < 200; i++) {
            final MainFragment mainFragment = new MainFragment(behavior);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = simpleDateFormat.format(calendar.getTime());
            mainFragment.setDate(date);
            mViewList.add(mainFragment);
        }

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mViewList);
        mViewPager.setAdapter(myFragmentPagerAdapter);

        mViewPager.setCurrentItem(99);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                nowDate = mViewList.get(position).getDate();
                main_toolbar.setTitle(nowDate);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    // 滑动菜单处理逻辑
    private void drawerMenu() {
        // 设置drawerLayout图标
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, main_toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                main_toolbar.setTitle(nowDate);
                isOpen = false;
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                main_toolbar.setTitle("Account");
                isOpen = true;
                invalidateOptionsMenu();
            }
        };

        // 设置同步
        toggle.syncState();
        // 设置监听
        mDrawerLayout.addDrawerListener(toggle);

        // 选中设置默认选中的item
        mNavigationView.setCheckedItem(R.id.outcome_item);
        // item监听
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.outcome_item:
                        calendar = Calendar.getInstance();
                        behavior = "outcome";
                        setViewPager();
                        break;
                    case R.id.income_item:
                        calendar = Calendar.getInstance();
                        behavior = "income";
                        setViewPager();
                        break;
                    case R.id.total_item:
                        break;
                    case R.id.setting_item:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        intent.putExtra("date", nowDate.substring(0, nowDate.length() - 3));
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    // 显示datePicker
    private void showDatePicker() {
        newYear = Integer.parseInt(nowDate.substring(0, 4));
        newMonth = Integer.parseInt(nowDate.substring(5, 7)) - 1;
        newDay = Integer.parseInt(nowDate.substring(8, 10));

        // 初始化popupWindow
        mDatePickerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.datepicker, null);
        mPopupWindow = new PopupWindow(mDatePickerView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mDatePicker = (DatePicker) mDatePickerView.findViewById(R.id.datePicker);

        // 初始化datePicker
        mDatePicker.init(newYear, newMonth, newDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                confirm.setEnabled(true);
                setSelectDay(year, month, day);
            }
        });

        confirm = (Button) mDatePickerView.findViewById(R.id.datePicker_confirm);
        cancel = (Button) mDatePickerView.findViewById(R.id.datePicker_cancel);

        // 设置按钮未拖动不可点击
        confirm.setEnabled(false);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        // popupWindow外部区域不可点击
        mPopupWindow.setOutsideTouchable(false);
        // 不可获取焦点
        mPopupWindow.setFocusable(false);
        // 设置动画
        mPopupWindow.setAnimationStyle(R.style.mypopupwindow_anim_style);
        // 设置显示位置
        mPopupWindow.showAtLocation(MainActivity.this.findViewById(R.id.activity_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        // 显示弹出窗口
        mPopupWindow.showAsDropDown(mDatePickerView);
    }

    // 设置新选择的日期
    private void setSelectDay(int newYear, int newMonth, int newDay) {
        this.newYear = newYear;
        this.newMonth = newMonth;
        this.newDay = newDay;
    }

    // 为了使popupWindow不获取焦点、外部区域不能点击且后方控件不能被相应，需重写此方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) return false;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mCalendarItem = menu.findItem(R.id.calendar);
        mAdd = menu.findItem(R.id.add);
        if(mDrawerLayout.isDrawerOpen(mNavigationView)){
            mCalendarItem.setVisible(false);
            mAdd.setVisible(false);
        }
//        mCalendarItem.setVisible(!isOpen);
//        mAdd.setVisible(!isOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载toolbar menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // 点击菜单按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.calendar:
                showDatePicker();
                break;
            case R.id.add:
                Intent intent = null;
                if (behavior.equals("outcome"))
                    intent = new Intent(this, OutcomeActivity.class);
                else if (behavior.equals("income"))
                    intent = new Intent(this, IncomeActivity.class);
                intent.putExtra("date", main_toolbar.getTitle());
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String string = data.getStringExtra("total_budget_return");
                    // 当长度不足4位时用" "补齐
                    if (string.length() < 4) {
                        for (int i = string.length(); i < 4; i++) {
                            string = " " + string;
                        }
                    }
                    totalBudget.setText(string);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (behavior.equals("outcome"))
            mNavigationView.setCheckedItem(R.id.outcome_item);
        if (behavior.equals("income"))
            mNavigationView.setCheckedItem(R.id.income_item);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.datePicker_confirm:
                calendar.set(newYear, newMonth, newDay);
                // 重新加载数据
                setViewPager();
                // datePicker更新数据
                mDatePicker.updateDate(newYear, newMonth, newDay);
                mPopupWindow.dismiss();
                break;
            case R.id.datePicker_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.main_budget_setting:
                Intent intent = new Intent(this, BudgetSettingActivity.class);
                //                intent.putExtra("total_budget", totalBudget.getText().toString());
                intent.putExtra("date", nowDate.substring(0, nowDate.length() - 3));
                startActivityForResult(intent, 1);
                break;
        }
    }
}
