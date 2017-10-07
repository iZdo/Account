package com.izdo.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.izdo.R;

/**
 * Created by iZdo on 2017/4/25.
 */

public class MyDialog extends Dialog {

    private ImageView dialogClose;
    private ListView mListView;
    private String mType;
    private TextView title;
    private String select;
    private String[] account = {"微信", "支付宝", "现金", "其他"};
    private String[] fixed_charge = {"无", "每日", "每周", "每月"};
    String[] items;

    public MyDialog(Context context, int themeResId, String type) {
        super(context, themeResId);
        mType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mType.equals("account") || mType.equals("fixed_charge"))
            init();
        else if (mType.equals("delete")) initDeleteDialog();
        else
            initOutcomeDialog();
    }

    // 未输入金额或类型对话框
    private void initOutcomeDialog() {
        setContentView(R.layout.dialog_save);
        TextView title = (TextView) findViewById(R.id.dialog_save_text);
        title.setText("请输入" + mType);
        Button button = (Button) findViewById(R.id.dialog_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    // delete对话框
    private void initDeleteDialog() {
        setContentView(R.layout.dialog_delete);
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    // account，fixed_charge对话框
    private void init() {
        setContentView(R.layout.dialog);
        dialogClose = (ImageView) findViewById(R.id.dialog_close);
        mListView = (ListView) findViewById(R.id.dialogListView);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#B8B8B8")));
        mListView.setDividerHeight(1);
        title = (TextView) findViewById(R.id.title);

        if (mType.equals("account")) {
            items = account;
            title.setText("请选取帐号");
        } else if (mType.equals("fixed_charge")) {
            items = fixed_charge;
            title.setText("请选取自动输入的周期");
        }

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog.this.dismiss();
            }
        });

        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public Object getItem(int position) {
                return items[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_listview_item, null);
                TextView text = (TextView) view.findViewById(R.id.dialog_listView_text);
                text.setText(items[position]);
                return view;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mType.equals("account")) {
                    setSelect(account[i]);
                } else if (mType.equals("fixed_charge")) {
                    setSelect(fixed_charge[i]);
                }
                dismiss();
            }
        });
    }
}
