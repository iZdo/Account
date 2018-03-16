package com.izdo.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.izdo.R;
import com.izdo.dataBase.DatabaseHelper;
import com.izdo.util.InitData;
import com.izdo.util.MyDialog;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by iZdo on 2017/5/2.
 */

public class BudgetSettingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    /**
     * 主页面
     */
    private TextView showBudget;
    private LinearLayout budgetSetting;
    private Button budgetSettingSave;
    private SQLiteDatabase mSQLiteDatabase;

    private Switch switchAddIncome;
    private Switch switchShowBudget;
    private RelativeLayout ball_color_layout;

    private SharedPreferences.Editor mEditor;

    // 是否check收入算入剩余预算
    public static boolean isAddIncome;
    // 是否check显示预算
    public static boolean isShowBudget;

    // 存储选中的余量球颜色
    String selectedColor;

    /**
     * 弹出窗口页面
     */
    private PopupWindow mPopupWindow;
    private View mCalculatorView;
    private Button one;
    private Button two;
    private Button three;
    private Button four;
    private Button five;
    private Button six;
    private Button seven;
    private Button eight;
    private Button nine;
    private Button zero;
    private Button ac;
    private Button OK;
    private ImageButton back;

    // 是否第一次点击计算器
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_setting);

        init();
        loadData();
    }

    /**
     * 从本地文件中读取数据
     */
    private void loadData() {
        mEditor = getSharedPreferences("data", MODE_PRIVATE).edit();

        isAddIncome = InitData.isAddIncome;
        isShowBudget = InitData.isShowBudget;

        switchAddIncome.setChecked(isAddIncome);
        switchShowBudget.setChecked(isShowBudget);

        selectedColor = InitData.ballColor;
    }


    private void init() {
        showBudget = (TextView) findViewById(R.id.show_budget);
        budgetSetting = (LinearLayout) findViewById(R.id.budget_setting);
        budgetSettingSave = (Button) findViewById(R.id.budget_setting_save);

        mCalculatorView = LayoutInflater.from(BudgetSettingActivity.this).inflate(R.layout.budget_calculator, null);
        mPopupWindow = new PopupWindow(mCalculatorView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mSQLiteDatabase = DatabaseHelper.getInstance(this);

        queryBudget();

        switchAddIncome = (Switch) findViewById(R.id.switch_addIncome);
        switchShowBudget = (Switch) findViewById(R.id.switch_showBudget);
        ball_color_layout = (RelativeLayout) findViewById(R.id.ball_color_layout);

        // 与弹出窗口有关的控件需要在初始化弹出窗口后再初始化
        one = (Button) mCalculatorView.findViewById(R.id.budget_one);
        two = (Button) mCalculatorView.findViewById(R.id.budget_two);
        three = (Button) mCalculatorView.findViewById(R.id.budget_three);
        four = (Button) mCalculatorView.findViewById(R.id.budget_four);
        five = (Button) mCalculatorView.findViewById(R.id.budget_five);
        six = (Button) mCalculatorView.findViewById(R.id.budget_six);
        seven = (Button) mCalculatorView.findViewById(R.id.budget_seven);
        eight = (Button) mCalculatorView.findViewById(R.id.budget_eight);
        nine = (Button) mCalculatorView.findViewById(R.id.budget_nine);
        zero = (Button) mCalculatorView.findViewById(R.id.budget_zero);
        ac = (Button) mCalculatorView.findViewById(R.id.budget_ac);
        OK = (Button) mCalculatorView.findViewById(R.id.budget_OK);
        back = (ImageButton) mCalculatorView.findViewById(R.id.budget_back);

        // 设置点击事件
        /**
         * 主页面
         */
        showBudget.setOnClickListener(this);
        budgetSetting.setOnClickListener(this);
        budgetSettingSave.setOnClickListener(this);

        switchAddIncome.setOnCheckedChangeListener(this);
        switchShowBudget.setOnCheckedChangeListener(this);
        ball_color_layout.setOnClickListener(this);

        /**
         *  弹出窗口页面
         */
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
        ac.setOnClickListener(this);
        OK.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    // 查询本月预算
    private void queryBudget() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String dateStr = getIntent().getStringExtra("date");

        boolean flag = true;
        // 上个月的预算
        String total = "1000";

        // 查询本月是否已添加预算
        Cursor cursor = mSQLiteDatabase.query("Budget", new String[]{"total"}, "date = ?",
                new String[]{dateStr}, null, null, null);

        // 如果本月还未添加预算,则默认设置为上个月的预算
        if (cursor.getCount() == 0) {
            Logger.i("进来了");

            ContentValues values = new ContentValues();
            try {
                // 如果上个月也没有添加预算 则一直往前找
                while (flag) {
                    // 月份减1
                    Date date = simpleDateFormat.parse(dateStr);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MONTH, -1);

                    // 查询上个月的预算
                    cursor = mSQLiteDatabase.query("Budget", new String[]{"total"}, "date = ?",
                            new String[]{simpleDateFormat.format(calendar.getTime())}, null, null, null);
                    if (cursor.moveToNext()) {
                        total = cursor.getString(cursor.getColumnIndex("total"));
                        flag = false;
                    }
                }

                // 添加数据
                values.put("total", total);
                values.put("date", dateStr);
                DatabaseHelper.getInstance(this).insert("Budget", null, values);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // 添加之后再查找一遍
            cursor = mSQLiteDatabase.query("Budget", new String[]{"total"}, "date = ?",
                    new String[]{dateStr}, null, null, null);
        }
        cursor.moveToNext();
        showBudget.setText("¥" + cursor.getString(cursor.getColumnIndex("total")));


        cursor.close();
    }

    // 弹出窗口
    private void showPopupWindow() {

        // 是否获取焦点
        mPopupWindow.setFocusable(false);
        // 设置动画
        mPopupWindow.setAnimationStyle(R.style.calc_popupWindow_anim_style);
        // 设置显示位置
        mPopupWindow.showAtLocation(BudgetSettingActivity.this.findViewById(R.id.activity_budget_setting), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        // 显示弹出窗口
        mPopupWindow.showAsDropDown(mCalculatorView);
    }

    // 显示金额
    private void showOnTextView(String string) {
        String newString = showBudget.getText().toString().trim();
        if (string.equals("back")) {
            if (newString.length() != 1) {
                newString = newString.substring(0, newString.length() - 1);
                // 最后一个数消除时变为"0"
                if (newString.length() == 1) {
                    newString += "0";
                }
            }
            isFirst = false;
        } else if (string.equals("AC")) {
            newString = newString.substring(0, 1) + "0";
        } else {
            // "0"不能在数字第一位
            if (string.equals("0") && newString.length() == 1) return;
            if (newString.equals("¥0"))
                newString = newString.substring(0, 1);

            if (isFirst) {
                newString = newString.substring(0, 1);
                isFirst = false;
            }

            newString += string;
        }
        showBudget.setText(newString);
    }

    @Override
    public void onBackPressed() {
        // 若弹出窗口已显示 则只关闭弹出窗口
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /**
             *  主页面
             */
            case R.id.show_budget:
                showPopupWindow();
                break;
            case R.id.budget_setting:
                if (mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                finish();
                break;
            case R.id.ball_color_layout:
                final MyDialog myDialog = new MyDialog(this, R.style.dialog_style);
                myDialog.initBallColorDialog();
                myDialog.setSelectedColor(selectedColor);
                myDialog.show();

                myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        selectedColor = myDialog.getSelectedColor();
                    }
                });

                break;
            case R.id.budget_setting_save:
                String total = showBudget.getText().toString().substring(1, showBudget.getText().length()).trim();
                ContentValues values = new ContentValues();
                values.put("total", total);
                mSQLiteDatabase.update("Budget", values, "date = ?", new String[]{getIntent().getStringExtra("date")});
                Intent intent = new Intent();
                intent.putExtra("total_budget_return", total);
                setResult(RESULT_OK, intent);
                if (mPopupWindow.isShowing())
                    mPopupWindow.dismiss();

                // 点击储存按钮才写入文件
                mEditor.putString("selectedColor", selectedColor);
                mEditor.apply();

                InitData.ballColor = selectedColor;
                InitData.isAddIncome = isAddIncome;
                InitData.isShowBudget = isShowBudget;

                finish();
                break;
            /**
             *  弹出窗口页面
             */
            case R.id.budget_one:
                showOnTextView("1");
                break;
            case R.id.budget_two:
                showOnTextView("2");
                break;
            case R.id.budget_three:
                showOnTextView("3");
                break;
            case R.id.budget_four:
                showOnTextView("4");
                break;
            case R.id.budget_five:
                showOnTextView("5");
                break;
            case R.id.budget_six:
                showOnTextView("6");
                break;
            case R.id.budget_seven:
                showOnTextView("7");
                break;
            case R.id.budget_eight:
                showOnTextView("8");
                break;
            case R.id.budget_nine:
                showOnTextView("9");
                break;
            case R.id.budget_zero:
                showOnTextView("0");
                break;
            case R.id.budget_ac:
                showOnTextView("AC");
                break;
            case R.id.budget_back:
                showOnTextView("back");
                break;
            case R.id.budget_OK:
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_addIncome:
                mEditor.putBoolean("isAddIncome", b);
                isAddIncome = b;
                break;
            case R.id.switch_showBudget:
                mEditor.putBoolean("isShowBudget", b);
                isShowBudget = b;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSQLiteDatabase.close();
    }
}
