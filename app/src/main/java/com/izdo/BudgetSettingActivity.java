package com.izdo;

import android.content.ContentValues;
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
import android.widget.Switch;
import android.widget.TextView;

import com.izdo.DataBase.MyDatabaseHelper;

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

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    // 是否check收入算入剩余预算
    public static boolean isAddIncome = false;
    // 是否check显示预算
    public static boolean isShowBudget = true;

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
        mSharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        isAddIncome = mSharedPreferences.getBoolean("isAddIncome", false);
        isShowBudget = mSharedPreferences.getBoolean("isShowBudget", true);

        switchAddIncome.setChecked(isAddIncome);
        switchShowBudget.setChecked(isShowBudget);
    }


    private void init() {
        showBudget = (TextView) findViewById(R.id.show_budget);
        budgetSetting = (LinearLayout) findViewById(R.id.budget_setting);
        budgetSettingSave = (Button) findViewById(R.id.budget_setting_save);

        mCalculatorView = LayoutInflater.from(BudgetSettingActivity.this).inflate(R.layout.budget_calculator, null);
        mPopupWindow = new PopupWindow(mCalculatorView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mSQLiteDatabase = MyDatabaseHelper.getInstance(this);

        Cursor cursor = mSQLiteDatabase.query("Budget", new String[]{"total"}, "date=?",
                new String[]{getIntent().getStringExtra("date")}, null, null, null);
        cursor.moveToNext();
        showBudget.setText("¥" + cursor.getString(cursor.getColumnIndex("total")));

        switchAddIncome = (Switch) findViewById(R.id.switch_addIncome);
        switchShowBudget = (Switch) findViewById(R.id.switch_showBudget);

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
        } else if (string.equals("AC")) {
            newString = newString.substring(0, 1) + "0";
        } else {
            // "0"不能在数字第一位
            if (string.equals("0") && newString.length() == 1) return;
            if (newString.equals("¥0"))
                newString = newString.substring(0, 1);

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
                mEditor.apply();
                isAddIncome = mSharedPreferences.getBoolean("isAddIncome", false);
                isShowBudget = mSharedPreferences.getBoolean("isShowBudget", true);

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
                break;
            case R.id.switch_showBudget:
                mEditor.putBoolean("isShowBudget", b);
                break;
            default:
                break;
        }
    }
}
