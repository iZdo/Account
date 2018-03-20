package com.izdo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

import com.izdo.activity.BudgetSettingActivity;
import com.izdo.activity.IncomeActivity;
import com.izdo.activity.LoginActivity;
import com.izdo.activity.MineActivity;
import com.izdo.activity.OutcomeActivity;
import com.izdo.activity.SettingActivity;
import com.izdo.activity.StatisticsActivity;
import com.izdo.adapter.FragmentPagerAdapter;
import com.izdo.bean.User;
import com.izdo.dataBase.DatabaseHelper;
import com.izdo.fragment.MainFragment;
import com.izdo.util.Constant;
import com.izdo.util.InitData;
import com.izdo.util.MyDialog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.izdo.R.id.username;

/**
 * iZdo
 * 2017/3/19
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeSubscription mCompositeSubscription;

    private Toolbar main_toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private CircleImageView userHeader;
    private TextView usernameText;

    // Toolbar按钮
    private MenuItem mCalendarItem;
    private MenuItem mAdd;

    // 日期选择器弹出窗口
    private DatePicker mDatePicker;
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

    private ViewPager mViewPager;
    private ArrayList<MainFragment> mViewList;
    private FragmentPagerAdapter fragmentPagerAdapter;
    // 记录最后一次的position
    private int lastPosition;

    // 预算控件
    private RelativeLayout budgetSetting;
    private TextView totalBudget;

    // 当前滑动菜单item
    public static String behavior = Constant.OUTCOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showUpdateAnnouncement();

        init();

        // 检查是否需要更新版本
        checkUpdate();
    }

    private void checkUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            BmobUpdateAgent.forceUpdate(this);
        }
    }

    private void showUpdateAnnouncement() {

        SharedPreferences mSharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        if (!mSharedPreferences.getBoolean("isNoLongerPrompt", false)) {
            MyDialog myDialog = new MyDialog(this, R.style.dialog_style);
            myDialog.initUpdateDialog();
            myDialog.setCancelable(false);
            myDialog.show();
        }
    }


    // 初始化控件
    private void init() {
        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // 获取滑动菜单上的控件
        initNavigation();

        budgetSetting = (RelativeLayout) findViewById(R.id.main_budget_setting);
        totalBudget = (TextView) findViewById(R.id.total_budget);

        // 初始化toolbar
        main_toolbar.setTitle(getFormatDate(calendar));
        setSupportActionBar(main_toolbar);
        main_toolbar.setOnClickListener(this);
        budgetSetting.setOnClickListener(this);
        drawerMenu();

        // 初始化月预算
        initBudget();
        setBudgetData();

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewList = new ArrayList<>();

        // 加载数据
        setViewPager();
    }

    private void initNavigation() {
        View headerLayout = mNavigationView.getHeaderView(0);
        // 初始化控件
        userHeader = (CircleImageView) headerLayout.findViewById(R.id.user_header);
        usernameText = (TextView) headerLayout.findViewById(username);
        userHeader.setOnClickListener(this);
        usernameText.setOnClickListener(this);

        loadUserData();
    }

    private void loadUserData() {
        // 获取本地用户信息
        String username = (String) BmobUser.getObjectByKey("username");

        // 设置相关信息
        if (!TextUtils.isEmpty(username)) {
            InitData.isLogin = true;
            usernameText.setText(username);
        } else {
            usernameText.setText("您还没有登录");
        }

        if (InitData.picPath.equals("")) {
            if (InitData.isLogin) {
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("username", username);
                addSubscription(query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            if (list.size() == 0) return;
                            final BmobFile bmobFile = new BmobFile(list.get(0).getPic().getFilename(), "", list.get(0).getPic().getFileUrl());
                            bmobFile.download(new DownloadFileListener() {
                                @Override
                                public void done(String path, BmobException e) {
                                    SharedPreferences.Editor mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    mEditor.putString("picPath", path);
                                    mEditor.commit();
                                    InitData.picPath = path;
                                    setHeader();
                                }

                                @Override
                                public void onProgress(Integer integer, long l) {

                                }
                            });
                        } else {
                            e.printStackTrace();
                        }

                    }
                }));
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                userHeader.setImageBitmap(bitmap);
                return;
            }
        } else
            setHeader();
    }

    private void setHeader() {
        Bitmap bitmap = null;
        try {
            FileInputStream fis = new FileInputStream(InitData.picPath);
            bitmap = BitmapFactory.decodeStream(fis);//把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap != null)
            userHeader.setImageBitmap(bitmap);
    }


    private void setBudgetData() {
        SharedPreferences mSharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        BudgetSettingActivity.isAddIncome = mSharedPreferences.getBoolean("isAddIncome", false);
        BudgetSettingActivity.isShowBudget = mSharedPreferences.getBoolean("isShowBudget", true);
    }

    // 初始化月预算
    private void initBudget() {
        String mMonth = main_toolbar.getTitle().toString().substring(0, main_toolbar.getTitle().toString().length() - 3);
        Cursor cursor = DatabaseHelper.getInstance(this).query("Budget", null, "date = ? ", new String[]{mMonth}, null, null, null);

        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            // 添加数据
            values.put("total", "1000");
            values.put("date", mMonth);
            DatabaseHelper.getInstance(this).insert("Budget", null, values);
        }
        cursor.close();
    }

    // 获取当前日期 yyyy-MM-hh
    private String getFormatDate(Calendar calendar) {
        // 将calendar的时间转换为yyyy-MM-hh格式
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 为ViewPager添加数据并设置适配器和监听器
     */
    private void setViewPager() {

        mViewList.clear();

        // 加载4个fragment 实现无限循环
        for (int i = 0; i < 4; i++) {
            MainFragment mainFragment = new MainFragment();
            mViewList.add(mainFragment);
        }

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mViewList);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        //        mViewPager.setCurrentItem(Integer.MAX_VALUE / 2 / 4 * 4);

        lastPosition = mViewPager.getCurrentItem();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (position > lastPosition) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    main_toolbar.setTitle(getFormatDate(calendar));
                } else if (position < lastPosition) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    main_toolbar.setTitle(getFormatDate(calendar));
                }

                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 滑动菜单逻辑处理
     */
    private void drawerMenu() {
        // 设置drawerLayout图标
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, main_toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                main_toolbar.setTitle(getFormatDate(calendar));
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                main_toolbar.setTitle(Constant.TOOLBAR_TITLE);
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
                    // 支出item
                    case R.id.outcome_item:
                        behavior = Constant.OUTCOME;
                        fragmentPagerAdapter.notifyDataSetChanged();
                        break;
                    // 收入item
                    case R.id.income_item:
                        behavior = Constant.INCOME;
                        fragmentPagerAdapter.notifyDataSetChanged();
                        break;
                    // 统计item
                    case R.id.statistics_item:
                        startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                        break;
                    // 设置item
                    case R.id.setting_item:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        intent.putExtra("date", getFormatDate(calendar).substring(0, getFormatDate(calendar).length() - 3));
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                // 点击之后关闭滑动菜单
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    /**
     * 显示datePicker
     */
    private void showDatePicker() {
        newYear = Integer.parseInt(main_toolbar.getTitle().toString().substring(0, 4));
        newMonth = Integer.parseInt(main_toolbar.getTitle().toString().substring(5, 7)) - 1;
        newDay = Integer.parseInt(main_toolbar.getTitle().toString().substring(8, 10));

        // 初始化popupWindow
        mDatePickerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.datepicker, null);
        mPopupWindow = new PopupWindow(mDatePickerView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mDatePicker = (DatePicker) mDatePickerView.findViewById(R.id.datePicker);

        // 初始化datePicker
        mDatePicker.init(newYear, newMonth, newDay, new DatePicker.OnDateChangedListener() {
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
        mPopupWindow.showAtLocation(MainActivity.this.findViewById(R.id.activity_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    /**
     * 为了使popupWindow不获取焦点、外部区域不能点击且后方控件不能被响应，需重写此方法
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mPopupWindow != null && mPopupWindow.isShowing())
            return false;
        return super.dispatchTouchEvent(ev);
    }

    // 菜单
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mCalendarItem = menu.findItem(R.id.calendar);
        mAdd = menu.findItem(R.id.add);
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mCalendarItem.setVisible(false);
            mAdd.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // 加载菜单文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载toolbar menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    // 菜单按钮点击事件
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
                if (behavior.equals(Constant.OUTCOME))
                    intent = new Intent(this, OutcomeActivity.class);
                else if (behavior.equals(Constant.INCOME))
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

        if (behavior.equals(Constant.OUTCOME))
            mNavigationView.setCheckedItem(R.id.outcome_item);
        if (behavior.equals(Constant.INCOME))
            mNavigationView.setCheckedItem(R.id.income_item);

        loadUserData();

    }

    /**
     * 解决Subscription内存泄露问题
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }

        Log.d("TPush", "onDestroy:         1        ");
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_toolbar:
                calendar = Calendar.getInstance();

                MainFragment.phaseDay = lastPosition;
                main_toolbar.setTitle(getFormatDate(calendar));
                fragmentPagerAdapter.notifyDataSetChanged();

                break;
            case R.id.datePicker_confirm:
                calendar.set(newYear, newMonth, newDay);
                // datePicker更新数据
                mDatePicker.updateDate(newYear, newMonth, newDay);
                mPopupWindow.dismiss();

                // 计算出选择日期与当前系统日期的相差值
                long l = (System.currentTimeMillis() / (1000 * 3600 * 24) - calendar.getTimeInMillis() / (1000 * 3600 * 24));
                int phaseDay = (int) (lastPosition + l);

                MainFragment.phaseDay = phaseDay;
                main_toolbar.setTitle(getFormatDate(calendar));
                fragmentPagerAdapter.notifyDataSetChanged();
                break;
            case R.id.datePicker_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.main_budget_setting:
                Intent intent = new Intent(this, BudgetSettingActivity.class);
                intent.putExtra("date", getFormatDate(calendar).substring(0, getFormatDate(calendar).length() - 3));
                startActivityForResult(intent, 1);
                break;
            case R.id.username:
            case R.id.user_header:
                if (InitData.isLogin) {
                    // 如果已经登录 跳转到个人设置页面
                    startActivity(new Intent(MainActivity.this, MineActivity.class));
                } else {
                    // 如果未登录 跳转到登录注册页面
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
        }
    }
}
