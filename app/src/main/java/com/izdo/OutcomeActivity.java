package com.izdo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.izdo.Adapter.MyPagerAdapter;
import com.izdo.Bean.DataBean;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.Constant;
import com.izdo.Util.InitData;
import com.izdo.Util.MyDialog;
import com.izdo.Util.TypeMap;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class OutcomeActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 主页面
     */
    private ViewPager mViewpager;
    private View outcome_viewpager1, outcome_viewpager2;
    private List<View> mViewList;
    private LayoutInflater mLayoutInflater;
    private MyPagerAdapter mPagerAdapter;

    private TextView showOutcome;
    private LinearLayout newOutcome;
    private Button outcomeSave;

    private RadioButton breakfast;
    private RadioButton lunch;
    private RadioButton dinner;
    private RadioButton beverage;
    private RadioButton snacks;
    private RadioButton traffic;
    private RadioButton shopping;
    //    private RadioButton grocery;
    private RadioButton entertainment;
    private RadioButton social;
    //    private RadioButton clothes;
    private RadioButton ticket;
    private RadioButton water_and_electricity;
    private RadioButton rent;
    private RadioButton gifts;
    //    private RadioButton cash_gift;
    private RadioButton transfer;
    private RadioButton medical;
    private RadioButton mobile_bill;
    private RadioButton loan;
    private RadioButton repayment;
    private RadioButton outcome_investment;
    //    private RadioButton credit_card;
    private RadioButton outcome_other;

    // 记录当前选中的类型
    private int typeId = 0;
    TypeMap mTypeMap = new TypeMap();

    // ViewPager导航点
    private List<ImageView> mDots;
    // 记录当前点的位置
    private int oldPosition;

    private RelativeLayout describeLayout;
    private RelativeLayout accountLayout;
    private RelativeLayout fixed_chargeLayout;
    private TextView describeText;
    private TextView accountText;
    private TextView fixedChargeText;

    // 是否从支出详情页面跳转的标识
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

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // 是否第一次点击计算器
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcome);
        init();
        ifEdit();

        // TODO
       /* Button aaa = (Button) findViewById(R.id.aaa);
        final TextView bbb = (TextView) findViewById(R.id.bbb);
        aaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper mDatabaseHelper = new MyDatabaseHelper(OutcomeActivity.this, "Account.db", null, 1);
                SQLiteDatabase mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
                Cursor cursor = mSQLiteDatabase.query("Data", null, "money=? and type=? and date=?", new String[]{"9", "snacks", "2017-04-18"}, null, null, null);
                cursor.moveToFirst();
                //int id = cursor.getInt(cursor.getColumnIndex("id"));
                Float money = cursor.getFloat(cursor.getColumnIndex("money"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String describe = cursor.getString(cursor.getColumnIndex("describe"));
                String account = cursor.getString(cursor.getColumnIndex("account"));
                String fixed_charge = cursor.getString(cursor.getColumnIndex("fixed_charge"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String all = money + type + describe + account + fixed_charge + date;
                bbb.setText(all);

            }
        });*/
    }

    private void init() {

        // 初始化控件
        mViewpager = (ViewPager) findViewById(R.id.outcome_viewpager);
        showOutcome = (TextView) findViewById(R.id.show_outcome);
        newOutcome = (LinearLayout) findViewById(R.id.new_outcome);
        outcomeSave = (Button) findViewById(R.id.outcome_save);
        describeLayout = (RelativeLayout) findViewById(R.id.outcome_describeLayout);
        accountLayout = (RelativeLayout) findViewById(R.id.outcome_accountLayout);
        fixed_chargeLayout = (RelativeLayout) findViewById(R.id.outcome_fixed_chargeLayout);
        describeText = (TextView) findViewById(R.id.outcome_describe_text);
        accountText = (TextView) findViewById(R.id.outcome_account);
        fixedChargeText = (TextView) findViewById(R.id.outcome_fixed_charge);

        accountText.setText(InitData.preset);

        // ViewPager
        setViewpager();

        // 初始化RadioButton
        breakfast = (RadioButton) outcome_viewpager1.findViewById(R.id.breakfast);
        lunch = (RadioButton) outcome_viewpager1.findViewById(R.id.lunch);
        dinner = (RadioButton) outcome_viewpager1.findViewById(R.id.dinner);
        beverage = (RadioButton) outcome_viewpager1.findViewById(R.id.beverage);
        snacks = (RadioButton) outcome_viewpager1.findViewById(R.id.snacks);

        traffic = (RadioButton) outcome_viewpager1.findViewById(R.id.traffic);
        shopping = (RadioButton) outcome_viewpager1.findViewById(R.id.shopping);
        //        grocery = (RadioButton) outcome_viewpager1.findViewById(R.id.grocery);
        entertainment = (RadioButton) outcome_viewpager1.findViewById(R.id.entertainment);
        social = (RadioButton) outcome_viewpager1.findViewById(R.id.social);
        ticket = (RadioButton) outcome_viewpager1.findViewById(R.id.ticket);
        //        clothes = (RadioButton) outcome_viewpager1.findViewById(R.id.clothes);

        water_and_electricity = (RadioButton) outcome_viewpager2.findViewById(R.id.water_and_electricity);
        rent = (RadioButton) outcome_viewpager2.findViewById(R.id.rent);
        gifts = (RadioButton) outcome_viewpager2.findViewById(R.id.gifts);
        //        cash_gift = (RadioButton) outcome_viewpager2.findViewById(R.id.cash_gift);
        transfer = (RadioButton) outcome_viewpager2.findViewById(R.id.transfer);
        medical = (RadioButton) outcome_viewpager2.findViewById(R.id.medical);

        mobile_bill = (RadioButton) outcome_viewpager2.findViewById(R.id.mobile_bill);
        loan = (RadioButton) outcome_viewpager2.findViewById(R.id.loan);
        repayment = (RadioButton) outcome_viewpager2.findViewById(R.id.repayment);
        outcome_investment = (RadioButton) outcome_viewpager2.findViewById(R.id.outcome_investment);
        //        credit_card = (RadioButton) outcome_viewpager2.findViewById(R.id.credit_card);
        outcome_other = (RadioButton) outcome_viewpager2.findViewById(R.id.outcome_other);

        mSQLiteDatabase = MyDatabaseHelper.getInstance(this);

        myDialog = new MyDialog(this, R.style.dialog_style);
        myDialog.setCancelable(false);

        mCalculatorView = LayoutInflater.from(OutcomeActivity.this).inflate(R.layout.calculator, null);
        mPopupWindow = new PopupWindow(mCalculatorView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

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
        showOutcome.setOnClickListener(this);
        newOutcome.setOnClickListener(this);
        outcomeSave.setOnClickListener(this);
        describeLayout.setOnClickListener(this);
        accountLayout.setOnClickListener(this);
        fixed_chargeLayout.setOnClickListener(this);

        breakfast.setOnClickListener(this);
        lunch.setOnClickListener(this);
        dinner.setOnClickListener(this);
        beverage.setOnClickListener(this);
        snacks.setOnClickListener(this);
        traffic.setOnClickListener(this);
        shopping.setOnClickListener(this);
        //        grocery.setOnClickListener(this);
        entertainment.setOnClickListener(this);
        social.setOnClickListener(this);
        //        clothes.setOnClickListener(this);
        ticket.setOnClickListener(this);
        water_and_electricity.setOnClickListener(this);
        rent.setOnClickListener(this);
        gifts.setOnClickListener(this);
        //        cash_gift.setOnClickListener(this);
        transfer.setOnClickListener(this);
        medical.setOnClickListener(this);
        mobile_bill.setOnClickListener(this);
        loan.setOnClickListener(this);
        repayment.setOnClickListener(this);
        outcome_investment.setOnClickListener(this);
        //        credit_card.setOnClickListener(this);
        outcome_other.setOnClickListener(this);

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

    // 如果是支出详情页面跳转过来
    private void ifEdit() {
        mDataBean = getIntent().getParcelableExtra("dataBean");
        if (mDataBean != null) {
            ifDetails = true;

            fixed_chargeLayout.setClickable(false);
            findViewById(R.id.divider1).setVisibility(View.GONE);
            findViewById(R.id.divider2).setVisibility(View.GONE);

            TextView textView = (TextView) findViewById(R.id.addOutcome);
            textView.setText("编辑支出");
            //            fixed_chargeLayout.setClickable(false);

            typeId = mTypeMap.valueToKey(mDataBean.getType());

            RadioButton radioButton;

            // 判断RadioButton属于哪个ViewPager
            //            if (typeId == R.id.breakfast || typeId == R.id.lunch || typeId == R.id.dinner || typeId == R.id.beverage || typeId == R.id.snacks
            //                    || typeId == R.id.traffic || typeId == R.id.grocery || typeId == R.id.entertainment || typeId == R.id.social || typeId == R.id.clothes)
            if (typeId == R.id.breakfast || typeId == R.id.lunch || typeId == R.id.dinner || typeId == R.id.beverage || typeId == R.id.snacks
                    || typeId == R.id.traffic || typeId == R.id.shopping || typeId == R.id.entertainment || typeId == R.id.social || typeId == R.id.ticket)
                radioButton = (RadioButton) outcome_viewpager1.findViewById(typeId);
            else
                radioButton = (RadioButton) outcome_viewpager2.findViewById(typeId);

            radioButton.setChecked(true);

            showOutcome.setText("¥" + mDataBean.getMoney());
            fixedChargeText.setText(mDataBean.getFixed_charge());
            accountText.setText(mDataBean.getAccount());
            describeText.setText(mDataBean.getDescribe());
        }
    }

    // 设置ViewPager及导航点
    private void setViewpager() {

        mLayoutInflater = getLayoutInflater();
        outcome_viewpager1 = mLayoutInflater.inflate(R.layout.outcome_viewpager1, null);
        outcome_viewpager2 = mLayoutInflater.inflate(R.layout.outcome_viewpager2, null);
        mViewList = new ArrayList<>();
        mViewList.add(outcome_viewpager1);
        mViewList.add(outcome_viewpager2);

        // 初始化点
        mDots = new ArrayList<>();
        ImageView dotFirst = (ImageView) findViewById(R.id.dotFirst);
        ImageView dotSecond = (ImageView) findViewById(R.id.dotSecond);
        mDots.add(dotFirst);
        mDots.add(dotSecond);
        oldPosition = 0;
        mDots.get(oldPosition).setImageResource(R.drawable.viewpager_dot_focused);

        mPagerAdapter = new MyPagerAdapter(mViewList);
        mViewpager.setAdapter(mPagerAdapter);
        // 导航点监听
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mDots.get(oldPosition).setImageResource(R.drawable.viewpager_dot_normal);
                mDots.get(position).setImageResource(R.drawable.viewpager_dot_focused);
                oldPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // 弹出窗口
    private void showPopupWindow() {

        // 是否获取焦点
        mPopupWindow.setFocusable(false);
        // 设置动画
        mPopupWindow.setAnimationStyle(R.style.calc_popupWindow_anim_style);
        // 设置显示位置
        mPopupWindow.showAtLocation(OutcomeActivity.this.findViewById(R.id.activity_outcome), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

        showOutcome.setText(showOutcome.getText().toString().substring(0, 1) + resultStr);
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
            showOutcome.setText(showOutcome.getText().toString().substring(0, 1) + "0");
            isOperatorSelected = false;
        }
        String newString = showOutcome.getText().toString().trim();
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
            isFirst = false;
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

            if (isFirst) {
                newString = newString.substring(0, 1);
                isFirst = false;
            }

            newString += string;
        }
        showOutcome.setText(newString);
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
                number2 = showOutcome.getText().toString().substring(1, showOutcome.getText().toString().length());
                calc(number1, number2);
                return;
            default:
                isOperatorSelected = false;
                break;
        }
        number1 = showOutcome.getText().toString().substring(1, showOutcome.getText().toString().length());
        OK.setText("=");
    }

    // 储存数据
    private void data() {
        ContentValues values = new ContentValues();

        // 获取数据
        String money = showOutcome.getText().toString().substring(1, showOutcome.getText().toString().length());
        String type = mTypeMap.queryType(typeId);
        String describe = describeText.getText().toString();
        String account = accountText.getText().toString();
        String fixed_charge = fixedChargeText.getText().toString();
        String date;
        // 判断是否从详情页面跳转
        if (ifDetails) date = mDataBean.getDate();
        else date = getIntent().getStringExtra("date");

        // 添加数据
        values.put("money", money);
        values.put("type", type);
        values.put("describe", describe);
        values.put("account", account);
        values.put("fixed_charge", fixed_charge);
        values.put("date", date);
        values.put("behavior", "outcome");
        values.put("fixedRecord_id", 0);

        // 如果固定记录不为"无"
        if (!fixed_charge.equals("无")) {
            ContentValues fixed_values = new ContentValues();

            String already_date = date;

            if (fixed_charge.equals("每周")) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(simpleDateFormat.parse(already_date));
                    // 如果是周日 则退一 (国外一周的第一天从星期天开始)
                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                    calendar.set(Calendar.DAY_OF_WEEK, 2);
                    already_date = simpleDateFormat.format(calendar.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (fixed_charge.equals("每月")) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(simpleDateFormat.parse(already_date));
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    already_date = simpleDateFormat.format(calendar.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // 添加数据
            fixed_values.put("money", money);
            fixed_values.put("type", type);
            fixed_values.put("describe", describe);
            fixed_values.put("account", account);
            fixed_values.put("fixed_charge", fixed_charge);
            fixed_values.put("start_date", date);
            fixed_values.put("already_date", already_date);
            fixed_values.put("behavior", "outcome");
            mSQLiteDatabase.insert("FixedRecord", null, fixed_values);

            Cursor cursor = mSQLiteDatabase.query("FixedRecord", null, Constant.QUERY_SQL,
                    new String[]{money, type, describe, account, fixed_charge, date, "outcome"}, null, null, null);
            cursor.moveToNext();
            values.put("fixedRecord_id", cursor.getInt(cursor.getColumnIndex("fixedRecord_id")));
        }

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
            case R.id.show_outcome:
                showPopupWindow();
                break;
            case R.id.new_outcome:
                if (isPop) {
                    mPopupWindow.dismiss();
                    isPop = false;
                }
                finish();
                break;
            case R.id.outcome_save:
                if (Float.parseFloat(showOutcome.getText().toString().substring(1)) <= 0 && typeId == 0) {
                    myDialog.initSaveButtonDialog("金额和类别");
                    myDialog.show();
                    break;
                }
                if (Float.parseFloat(showOutcome.getText().toString().substring(1)) <= 0) {
                    myDialog.initSaveButtonDialog("正确的金额");
                    myDialog.show();
                    break;
                }
                if (typeId == 0) {
                    myDialog.initSaveButtonDialog("类别");
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
            case R.id.outcome_describeLayout:
                Intent intent = new Intent(this, DescribeActivity.class);
                intent.putExtra("describe", describeText.getText().toString());
                startActivityForResult(intent, 1);
                break;
            case R.id.outcome_accountLayout:
                myDialog.initAccountOrFixedChargeDialog("请选择帐号", InitData.accountOption(this));
                myDialog.setSelect(accountText.getText().toString());
                myDialog.show();
                myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        accountText.setText(myDialog.getSelect());
                    }
                });

                break;
            case R.id.outcome_fixed_chargeLayout:
                myDialog.initAccountOrFixedChargeDialog("请选择自动输入的周期", InitData.fixedChargeOption());
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
            case R.id.breakfast:
            case R.id.lunch:
            case R.id.dinner:
            case R.id.beverage:
            case R.id.snacks:
            case R.id.traffic:
            case R.id.shopping:
                //            case R.id.grocery:
            case R.id.entertainment:
            case R.id.social:
                //            case R.id.clothes:
            case R.id.ticket:
            case R.id.water_and_electricity:
            case R.id.rent:
            case R.id.gifts:
                //            case R.id.cash_gift:
            case R.id.transfer:
            case R.id.medical:
            case R.id.mobile_bill:
            case R.id.loan:
            case R.id.repayment:
            case R.id.outcome_investment:
                //            case R.id.credit_card:
            case R.id.outcome_other:
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSQLiteDatabase.close();
    }
}
