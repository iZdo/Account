package com.izdo.Util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.izdo.Bean.Ball;
import com.izdo.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by iZdo on 2017/4/25.
 */

public class MyDialog extends Dialog {

    private ImageView dialogClose;
    private ListView mListView;
    private String mType;
    private TextView title;
    private TextView update_content;
    private String select;
    private String[] account = {"微信", "支付宝", "现金", "其他"};
    private String[] fixed_charge = {"无", "每日", "每周", "每月"};

    private String[] items;
    private String update;

    BallListUtil ballListUtil;
    ArrayList<Ball> ballList;
    String selectedColor;

    private SharedPreferences.Editor mEditor;

    public MyDialog(Context context, int themeResId, String type) {
        super(context, themeResId);
        mType = type;
//        ballListUtil = new BallListUtil();
        ballList = BallListUtil.getBallList();


        mEditor = getContext().getSharedPreferences("data", MODE_PRIVATE).edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mType.equals("account") || mType.equals("fixed_charge"))
            initAccountOrFixedChargeDialog();
        else if (mType.equals("delete"))
            initDeleteDialog();
        else if (mType.equals("updateAnnouncement"))
            initUpdateDialog();
        else if (mType.equals("ball_color"))
            initBallColorDialog();
        else
            initSaveButtonDialog();
    }

    // account，fixed_charge对话框
    private void initAccountOrFixedChargeDialog() {
        setContentView(R.layout.dialog);
        dialogClose = (ImageView) findViewById(R.id.dialog_close);
        mListView = (ListView) findViewById(R.id.dialogListView);
        //        mListView.setDivider(new ColorDrawable(Color.parseColor("#B8B8B8")));
        //        mListView.setDividerHeight(1);
        title = (TextView) findViewById(R.id.title);

        if (mType.equals("account")) {
            items = account;
            title.setText("请选择帐号");
        } else if (mType.equals("fixed_charge")) {
            items = fixed_charge;
            title.setText("请选择自动输入的周期");
        }

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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

    // 未输入金额或类型对话框
    private void initSaveButtonDialog() {
        setContentView(R.layout.dialog_save);
        TextView title = (TextView) findViewById(R.id.dialog_save_text);
        title.setText("请输入" + mType);

        findViewById(R.id.dialog_confirm_button).setOnClickListener(new View.OnClickListener() {
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

    // 更新公告updateAnnouncement对话框
    private void initUpdateDialog() {
        setContentView(R.layout.dialog_update);
        title = (TextView) findViewById(R.id.title);
        final CheckBox updateCheck = (CheckBox) findViewById(R.id.update_check);
        TextView confirm = (TextView) findViewById(R.id.dialog_confirm);

        updateCheck.setChecked(SPInit.isNoLongerPrompt);

        update_content = (TextView) findViewById(R.id.update_content);
        update_content.setText(getUpdate());
        title.setText("更新公告");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.putBoolean("isNoLongerPrompt", updateCheck.isChecked());
                SPInit.isNoLongerPrompt = updateCheck.isChecked();
                mEditor.apply();
                dismiss();
            }
        });

    }


    private void initBallColorDialog() {
        setContentView(R.layout.dialog_ball_color);
        mListView = (ListView) findViewById(R.id.ball_color_listView);
        //        mListView.setDivider(new ColorDrawable(Color.parseColor("#B8B8B8")));
        //        mListView.setDividerHeight(1);

        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return ballList.size();
            }

            @Override
            public Object getItem(int position) {
                return ballList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ball_color_listview_item, null);

                ImageView image = (ImageView) view.findViewById(R.id.ball_color_image);
                TextView text = (TextView) view.findViewById(R.id.ball_color_text);
                ImageView checkedImage = (ImageView) view.findViewById(R.id.ball_color_checked_image);

                Ball ball = ballList.get(position);

                image.setImageResource(ball.getColorImage());
                text.setText(ball.getColorText());
                text.setTextColor(Color.parseColor(ball.getColor()));

                // 如果选择的颜色是此Item才设置并显示图片
                if (ball.getColor().equals(selectedColor)) {
                    checkedImage.setImageResource(ball.getColorCheckedImage());
                    checkedImage.setVisibility(View.VISIBLE);
                }

                return view;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedColor = ballList.get(i).getColor();
                dismiss();
            }
        });


    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }
}
