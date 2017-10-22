package com.izdo;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.izdo.Bean.DataBean;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.MyDialog;
import com.izdo.Util.TypeMap;

/**
 * Created by zz on 2017/4/27.
 */

public class IncomeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout incomeDetails;
    private Button detailsEdit;

    private ImageView detailsImg;
    private TextView detailsType;
    private TextView detailsMoney;
    private TextView detailsAccount;
    private TextView detailsFixedCharge;
    private TextView detailsDescribe;
    private TextView date;
    private Button detailsDelete;

    private DataBean mDataBean;
    private TypeMap mTypeMap;

    private boolean back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_details);

        init();
        if (!back) {
            mDataBean = getIntent().getParcelableExtra("dataBean");
            data();
            back = true;
        }
    }

    // 初始化控件并设置点击事件
    private void init() {
        incomeDetails = (LinearLayout) findViewById(R.id.income_details);
        detailsEdit = (Button) findViewById(R.id.income_details_edit);
        detailsImg = (ImageView) findViewById(R.id.income_details_img);
        detailsType = (TextView) findViewById(R.id.income_details_type);
        detailsMoney = (TextView) findViewById(R.id.income_details_money);
        detailsAccount = (TextView) findViewById(R.id.income_details_account);
        detailsFixedCharge = (TextView) findViewById(R.id.income_details_fixed_charge);
        detailsDescribe = (TextView) findViewById(R.id.income_details_describe);
        date = (TextView) findViewById(R.id.income_details_date);
        detailsDelete = (Button) findViewById(R.id.income_details_delete);

        mTypeMap = new TypeMap();

        incomeDetails.setOnClickListener(this);
        detailsEdit.setOnClickListener(this);
        detailsDelete.setOnClickListener(this);
    }

    // 获取数据并显示
    private void data() {
        detailsImg.setImageResource(mTypeMap.queryTypeImg(mDataBean.getType()));
        detailsType.setText(mDataBean.getType());
        detailsMoney.setText("¥" + mDataBean.getMoney());
        detailsAccount.setText(mDataBean.getAccount());
        detailsFixedCharge.setText(mDataBean.getFixed_charge());
        detailsDescribe.setText(mDataBean.getDescribe());
        date.setText(mDataBean.getDate());
    }

    // 删除记录
    private void deleteData() {
        MyDatabaseHelper.getInstance(this).delete("Data", "id=? ", new String[]{mDataBean.getId() + ""});
    }

    // 一并删除后面的记录
    private void deleteBehindData() {
        MyDatabaseHelper.getInstance(this).delete("Data", "id >= ? and fixedRecord_id = ?", new String[]{mDataBean.getId() + "", mDataBean.getFixedRecord_id() + ""});

        // 更新以前的记录固定支出为"无"
        ContentValues values = new ContentValues();
        values.put("fixed_charge", "无");
        MyDatabaseHelper.getInstance(this).update("Data", values, "fixed_charge = ?", new String[]{mDataBean.getFixed_charge() + ""});

        MyDatabaseHelper.getInstance(this).delete("FixedRecord", "fixedRecord_id = ?", new String[]{mDataBean.getFixedRecord_id() + ""});
    }

    // 删除所有记录
    private void deleteAllData() {
        MyDatabaseHelper.getInstance(this).delete("Data", "fixedRecord_id = ?", new String[]{mDataBean.getFixedRecord_id() + ""});
        MyDatabaseHelper.getInstance(this).delete("FixedRecord", "fixedRecord_id = ?", new String[]{mDataBean.getFixedRecord_id() + ""});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK)
                    mDataBean = data.getParcelableExtra("dataBean_return");
                data();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.income_details:
                finish();
                break;
            case R.id.income_details_edit:
                Intent intent = new Intent(this, IncomeActivity.class);
                intent.putExtra("dataBean", mDataBean);
                startActivityForResult(intent, 1);
                break;
            case R.id.income_details_delete:
                final MyDialog myDialog = new MyDialog(this, R.style.dialog_style);
                myDialog.setCancelable(false);

                if (mDataBean.getFixed_charge().equals("无")) {
                myDialog.initSelectDialog("确定删除记录？");
                myDialog.show();
                    myDialog.findViewById(R.id.dialog_select_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteData();
                            myDialog.dismiss();
                            finish();
                        }
                    });
                    myDialog.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                        }
                    });
                } else {
                    myDialog.initDeleteDialog();
                    myDialog.show();
                    myDialog.findViewById(R.id.dialog_delete_one).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteData();
                            myDialog.dismiss();
                            finish();
                        }
                    });
                    myDialog.findViewById(R.id.dialog_delete_behind).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteBehindData();
                            myDialog.dismiss();
                            finish();
                        }
                    });
                    myDialog.findViewById(R.id.dialog_delete_all).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteAllData();
                            myDialog.dismiss();
                            finish();
                        }
                    });
                }
                break;

        }
    }
}
