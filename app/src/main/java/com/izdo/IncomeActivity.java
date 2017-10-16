package com.izdo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.izdo.Adapter.MyPagerAdapter;
import com.izdo.Bean.DataBean;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.InitData;
import com.izdo.Util.MyDialog;
import com.izdo.Util.TypeMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iZdo on 2017/4/13.
 */

public class IncomeActivity extends Activity implements View.OnClickListener {

    private ViewPager mViewpager;
    private View income_viewpager;
    private List<View> mViewList;
    private LayoutInflater mLayoutInflater;
    private MyPagerAdapter mPagerAdapter;

    private TextView showIncome;
    private LinearLayout newIncome;
    private Button incomeSave;

    private RadioButton salary;
    private RadioButton bonus;
    private RadioButton subsidy;
    private RadioButton income_investment;
    private RadioButton income_other;

    // 记录当前选中的类型
    private int typeId = 0;
    TypeMap mTypeMap = new TypeMap();

    private RelativeLayout describeLayout;
    private RelativeLayout accountLayout;
    private RelativeLayout fixed_chargeLayout;
    private TextView describeText;
    private TextView accountText;
    private TextView fixedChargeText;

    // 是否从收入详情页面跳转的标识
    private boolean ifDetails = false;

    private MyDialog myDialog;

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
    private Button point;
    private Button ac;
    private Button plus;
    private Button reduce;
    private Button multiply;
    private Button divide;
    private Button OK;
    private ImageButton back;
    // 判断当前是否有运算符被选中
    private Boolean isOperatorSelected = false;
    // 判断是否已计算
    private Boolean isCount = false;
    // 运算符
    private Character operator = 'N';
    private String number1 = "0", number2 = "0";

    // 弹出窗口是否存在
    private boolean isPop = false;
    private DataBean mDataBean;
    private SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        init();
        ifEdit();
    }

    private void init() {

        // 初始化控件
        mViewpager = (ViewPager) findViewById(R.id.income_viewpager);
        showIncome = (TextView) findViewById(R.id.show_income);
        newIncome = (LinearLayout) findViewById(R.id.new_income);
        incomeSave = (Button) findViewById(R.id.income_save);
        describeLayout = (RelativeLayout) findViewById(R.id.income_describeLayout);
        accountLayout = (RelativeLayout) findViewById(R.id.income_accountLayout);
        fixed_chargeLayout = (RelativeLayout) findViewById(R.id.income_fixed_chargeLayout);
        describeText = (TextView) findViewById(R.id.income_describe_text);
        accountText = (TextView) findViewById(R.id.income_account);
        fixedChargeText = (TextView) findViewById(R.id.income_fixed_charge);

        // ViewPager
        setViewpager();

        salary = (RadioButton) income_viewpager.findViewById(R.id.salary);
        bonus = (RadioButton) income_viewpager.findViewById(R.id.bonus);
        subsidy = (RadioButton) income_viewpager.findViewById(R.id.subsidy);
        income_investment = (RadioButton) income_viewpager.findViewById(R.id.income_investment);
        income_other = (RadioButton) income_viewpager.findViewById(R.id.income_other);

        mSQLiteDatabase = MyDatabaseHelper.getInstance(this);

        mCalculatorView = LayoutInflater.from(IncomeActivity.this).inflate(R.layout.calculator, null);
        mPopupWindow = new PopupWindow(mCalculatorView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        myDialog = new MyDialog(this, R.style.dialog_style);

        // 与弹出窗口有关的控件需要在初始化弹出窗口后再初始化
        one = (Button) mCalculatorView.findViewById(R.id.one);
        two = (Button) mCalculatorView.findViewById(R.id.two);
        three = (Button) mCalculatorView.findViewById(R.id.three);
        four = (Button) mCalculatorView.findViewById(R.id.four);
        five = (Button) mCalculatorView.findViewById(R.id.five);
        six = (Button) mCalculatorView.findViewById(R.id.six);
        seven = (Button) mCalculatorView.findViewById(R.id.seven);
        eight = (Button) mCalculatorView.findViewById(R.id.eight);
        nine = (Button) mCalculatorView.findViewById(R.id.nine);
        zero = (Button) mCalculatorView.findViewById(R.id.zero);
        point = (Button) mCalculatorView.findViewById(R.id.point);
        ac = (Button) mCalculatorView.findViewById(R.id.ac);
        plus = (Button) mCalculatorView.findViewById(R.id.plus);
        reduce = (Button) mCalculatorView.findViewById(R.id.reduce);
        multiply = (Button) mCalculatorView.findViewById(R.id.multiply);
        divide = (Button) mCalculatorView.findViewById(R.id.divide);
        OK = (Button) mCalculatorView.findViewById(R.id.OK);
        back = (ImageButton) mCalculatorView.findViewById(R.id.back);


        // 设置点击事件
        /**
         *  主页面
         */
        showIncome.setOnClickListener(this);
        newIncome.setOnClickListener(this);
        incomeSave.setOnClickListener(this);
        describeLayout.setOnClickListener(this);
        accountLayout.setOnClickListener(this);
        fixed_chargeLayout.setOnClickListener(this);

        salary.setOnClickListener(this);
        bonus.setOnClickListener(this);
        subsidy.setOnClickListener(this);
        income_investment.setOnClickListener(this);
        income_other.setOnClickListener(this);

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
        point.setOnClickListener(this);
        ac.setOnClickListener(this);
        plus.setOnClickListener(this);
        reduce.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);
        OK.setOnClickListener(this);
        back.setOnClickListener(this);


    }

    // 如果是收入详情页面跳转过来
    private void ifEdit() {
        mDataBean = getIntent().getParcelableExtra("dataBean");
        if (mDataBean != null) {
            ifDetails = true;
            TextView textView = (TextView) findViewById(R.id.addIncome);
            textView.setText("编辑收入");

            typeId = mTypeMap.valueToKey(mDataBean.getType());

            RadioButton radioButton = (RadioButton) income_viewpager.findViewById(typeId);

            radioButton.setChecked(true);

            showIncome.setText("¥" + mDataBean.getMoney());
            fixedChargeText.setText(mDataBean.getFixed_charge());
            accountText.setText(mDataBean.getAccount());
            describeText.setText(mDataBean.getDescribe());
        }
    }

    // 设置ViewPager
    private void setViewpager() {

        mLayoutInflater = getLayoutInflater();
        income_viewpager = mLayoutInflater.inflate(R.layout.income_viewpager, null);
        mViewList = new ArrayList<>();
        mViewList.add(income_viewpager);
        mPagerAdapter = new MyPagerAdapter(mViewList);
        mViewpager.setAdapter(mPagerAdapter);

    }

    // 弹出窗口
    private void showPopupWindow() {

        // 是否获取焦点
        mPopupWindow.setFocusable(false);
        // 设置动画
        mPopupWindow.setAnimationStyle(R.style.calc_popupWindow_anim_style);
        // 设置显示位置
        mPopupWindow.showAtLocation(IncomeActivity.this.findViewById(R.id.activity_income), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        // 显示弹出窗口
        mPopupWindow.showAsDropDown(mCalculatorView);

        isPop = true;

    }

    // 获取当前点击的类型
    private void setViewpagerBackground(int id) {
        RadioButton selected;
        if (typeId != 0) {
            selected = (RadioButton) findViewById(typeId);
            selected.setChecked(false);
        }
        selected = (RadioButton) findViewById(id);
        selected.setChecked(true);
        typeId = id;
    }

    // 计算
    private void calc(String n1, String n2) {
        double count1 = Double.parseDouble(n1);
        double count2 = Double.parseDouble(n2);
        double result = 0;
        String resultStr;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        switch (operator) {
            case '+':
                result = count1 + count2;
                break;
            case '-':
                result = count1 - count2;
                break;
            case '*':
                result = count1 * count2;
                break;
            case '/':
                if (count2 == 0) {
                    result = 0;
                    break;
                }
                result = count1 / count2;
                break;
            default:
                break;
        }

        resultStr = decimalFormat.format(result);
        // 若计算结果是以".00"结尾
        if (resultStr.endsWith(".00")) resultStr = resultStr.substring(0, resultStr.length() - 3);
        // 若计算结果是以".X0"结尾
        if (resultStr.contains(".") && resultStr.endsWith("0"))
            resultStr = resultStr.substring(0, resultStr.length() - 1);

        showIncome.setText(showIncome.getText().toString().substring(0, 1) + resultStr);
        isCount = true;
        OK.setText("OK");
    }

    // 显示金额
    private void showOnTextView(String string) {
        if (string.equals("+") || string.equals("-") || string.equals("*") || string.equals("/")) {
            if (string.equals(operator)) return;
            isOperatorSelected = true;
            return;
        }
        if (isOperatorSelected) {
            showIncome.setText(showIncome.getText().toString().substring(0, 1) + "0");
            isOperatorSelected = false;
        }
        String newString = showIncome.getText().toString().trim();
        if (isCount) {
            newString = newString.substring(0, 1) + "0";
            isCount = false;
        }
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

            if (string.equals(".")) {
                // "."只能存在一个
                if (newString.contains(".")) return;
                // "."不能在第一位
                if (newString.length() == 1)
                    newString += "0";
            }
            // 维持小数点后两位
            if (newString.contains(".") && newString.substring(newString.indexOf("."), newString.length()).length() > 2)
                return;

            newString += string;
        }
        showIncome.setText(newString);
        OK.setEnabled(true);
    }

    // 判断当前选中的是哪个运算符
    private void operatorSelected(Character c) {

        plus.setBackgroundColor(Color.parseColor("#faf9e9"));
        reduce.setBackgroundColor(Color.parseColor("#faf9e9"));
        multiply.setBackgroundColor(Color.parseColor("#faf9e9"));
        divide.setBackgroundColor(Color.parseColor("#faf9e9"));

        switch (c) {
            case '+':
                plus.setBackgroundColor(Color.parseColor("#dedcca"));
                OK.setEnabled(false);
                break;
            case '-':
                reduce.setBackgroundColor(Color.parseColor("#dedcca"));
                OK.setEnabled(false);
                break;
            case '*':
                multiply.setBackgroundColor(Color.parseColor("#dedcca"));
                OK.setEnabled(false);
                break;
            case '/':
                divide.setBackgroundColor(Color.parseColor("#dedcca"));
                OK.setEnabled(false);
                break;
            case '=':
                number2 = showIncome.getText().toString().substring(1, showIncome.getText().toString().length());
                calc(number1, number2);
                return;
            default:
                isOperatorSelected = false;
                break;
        }
        number1 = showIncome.getText().toString().substring(1, showIncome.getText().toString().length());
        OK.setText("=");
    }

    // 储存数据
    private void data() {
        ContentValues values = new ContentValues();

        // 获取数据
        String money = showIncome.getText().toString().substring(1, showIncome.getText().toString().length());
        String type = mTypeMap.queryType(typeId);
        String describe = describeText.getText().toString();
        String account = accountText.getText().toString();
        String fixed_charge = fixedChargeText.getText().toString();
        String date;
        if (ifDetails) date = mDataBean.getDate();
        else date = getIntent().getStringExtra("date");

        // 添加数据
        values.put("money", money);
        values.put("type", type);
        values.put("describe", describe);
        values.put("account", account);
        values.put("fixed_charge", fixed_charge);
        values.put("date", date);
        values.put("behavior", "income");

        if (ifDetails) {
            mDataBean.setMoney(money);
            mDataBean.setType(type);
            mDataBean.setDescribe(describe);
            mDataBean.setAccount(account);

            mSQLiteDatabase.update("Data", values, "id=?", new String[]{mDataBean.getId() + ""});

            return;
        }

        mSQLiteDatabase.insert("Data", null, values);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra("describe");
                    describeText.setText(returnedData);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // 若弹出窗口已显示 则只关闭弹出窗口
        if (isPop) {
            mPopupWindow.dismiss();
            isPop = false;
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
            case R.id.show_income:
                showPopupWindow();
                break;
            case R.id.new_income:
                if (isPop) {
                    mPopupWindow.dismiss();
                    isPop = false;
                }
                finish();
                break;
            case R.id.income_save:
                if (showIncome.getText().toString().substring(1, 2).equals("0") && typeId == 0) {
                    myDialog.initSaveButtonDialog("金额和类别");
                    myDialog.setCancelable(false);
                    myDialog.show();
                    break;
                }
                if (showIncome.getText().toString().substring(1, 2).equals("0")) {
                    myDialog.initSaveButtonDialog("金额");
                    myDialog.setCancelable(false);
                    myDialog.show();
                    break;
                }
                if (typeId == 0) {

                    myDialog.initSaveButtonDialog("类别");
                    myDialog.setCancelable(false);
                    myDialog.show();
                    break;
                }
                data();
                if (ifDetails) {
                    Intent intent = new Intent();
                    intent.putExtra("dataBean_return", mDataBean);
                    setResult(RESULT_OK, intent);
                    ifDetails = false;
                }
                if (isPop) {
                    mPopupWindow.dismiss();
                    isPop = false;
                }
                finish();
                break;
            case R.id.income_describeLayout:
                Intent intent = new Intent(this, DescribeActivity.class);
                intent.putExtra("describe", describeText.getText().toString());
                startActivityForResult(intent, 1);
                break;
            case R.id.income_accountLayout:
                myDialog.initAccountOrFixedChargeDialog("请选择帐号",InitData.accountOption(this));
                myDialog.setSelect(accountText.getText().toString());
                myDialog.show();
                myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        accountText.setText(myDialog.getSelect());
                    }
                });

                break;
            case R.id.income_fixed_chargeLayout:
                myDialog.initAccountOrFixedChargeDialog("请选择自动输入的周期",InitData.fixedChargeOption());
                myDialog.setSelect(fixedChargeText.getText().toString());
                myDialog.show();
                myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        fixedChargeText.setText(myDialog.getSelect());
                    }
                });
                break;

            /**
             * radioButton
             */
            case R.id.salary:
            case R.id.bonus:
            case R.id.subsidy:
            case R.id.income_investment:
            case R.id.income_other:
                showPopupWindow();
                setViewpagerBackground(view.getId());
                break;

            /**
             *  弹出窗口页面
             */
            case R.id.one:
                showOnTextView("1");
                break;
            case R.id.two:
                showOnTextView("2");
                break;
            case R.id.three:
                showOnTextView("3");
                break;
            case R.id.four:
                showOnTextView("4");
                break;
            case R.id.five:
                showOnTextView("5");
                break;
            case R.id.six:
                showOnTextView("6");
                break;
            case R.id.seven:
                showOnTextView("7");
                break;
            case R.id.eight:
                showOnTextView("8");
                break;
            case R.id.nine:
                showOnTextView("9");
                break;
            case R.id.zero:
                showOnTextView("0");
                break;
            case R.id.point:
                showOnTextView(".");
                break;
            case R.id.ac:
                showOnTextView("AC");
                break;
            case R.id.plus:
                showOnTextView("+");
                operator = '+';
                operatorSelected('+');
                break;
            case R.id.reduce:
                showOnTextView("-");
                operator = '-';
                operatorSelected('-');
                break;
            case R.id.multiply:
                showOnTextView("*");
                operator = '*';
                operatorSelected('*');
                break;
            case R.id.divide:
                showOnTextView("/");
                operator = '/';
                operatorSelected('/');
                break;
            case R.id.back:
                showOnTextView("back");
                break;
            case R.id.OK:
                if (!OK.getText().equals("=")) {
                    mPopupWindow.dismiss();
                    isPop = false;
                } else {
                    operatorSelected('=');
                }
                break;
            default:
                break;
        }
    }
}
