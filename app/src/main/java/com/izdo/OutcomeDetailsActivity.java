package com.izdo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.izdo.Bean.DataBean;
import com.izdo.Util.TypeMap;
import com.izdo.DataBase.MyDatabaseHelper;
import com.izdo.Util.MyDialog;

import static com.izdo.R.id.outcome_details;

/**
 * Created by zz on 2017/4/27.
 */

public class OutcomeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout outcomeDetails;
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

    // 是否是从OutcomeActivity返回的
    private boolean back = false;
    private SQLiteDatabase mSQLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcome_details);

        init();
        if (!back) {
            mDataBean = getIntent().getParcelableExtra("dataBean");
            data();
            back = true;
        }
    }

    // 初始化控件并设置点击事件
    private void init() {
        outcomeDetails = (LinearLayout) findViewById(outcome_details);
        detailsEdit = (Button) findViewById(R.id.outcome_details_edit);
        detailsImg = (ImageView) findViewById(R.id.outcome_details_img);
        detailsType = (TextView) findViewById(R.id.outcome_details_type);
        detailsMoney = (TextView) findViewById(R.id.outcome_details_money);
        detailsAccount = (TextView) findViewById(R.id.outcome_details_account);
        detailsFixedCharge = (TextView) findViewById(R.id.outcome_details_fixed_charge);
        detailsDescribe = (TextView) findViewById(R.id.outcome_details_describe);
        date = (TextView) findViewById(R.id.outcome_details_date);
        detailsDelete = (Button) findViewById(R.id.outcome_details_delete);

        mTypeMap = new TypeMap();

        outcomeDetails.setOnClickListener(this);
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
        mSQLiteDatabase = MyDatabaseHelper.getInstance(this);
        mSQLiteDatabase.delete("Data", "id=?", new String[]{mDataBean.getId() + ""});
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
            case outcome_details:
                finish();
                break;
            case R.id.outcome_details_edit:
                Intent intent = new Intent(this, OutcomeActivity.class);
                intent.putExtra("dataBean", mDataBean);
                startActivityForResult(intent, 1);
                break;
            case R.id.outcome_details_delete:
                final MyDialog accountDialog = new MyDialog(this, R.style.dialog_style, "delete");
                accountDialog.show();
                Button confirm = (Button) accountDialog.findViewById(R.id.dialog_delete_confirm);
                Button cancel = (Button) accountDialog.findViewById(R.id.dialog_delete_cancel);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteData();
                        accountDialog.dismiss();
                        finish();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accountDialog.dismiss();
                    }
                });
                break;

        }
    }
}
