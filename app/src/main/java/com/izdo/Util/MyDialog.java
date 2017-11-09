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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.izdo.Adapter.MyBaseAdapter;
import com.izdo.Bean.Ball;
import com.izdo.Bean.DataBean;
import com.izdo.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by iZdo on 2017/4/25.
 */

public class MyDialog<T> extends Dialog {

    private ImageView dialogClose;
    private ListView mListView;
    private TextView title;
    private TextView update_content;
    private String select;
    private List<T> mList;

    private List<Ball> ballList;
    private String selectedColor;

    private SharedPreferences.Editor mEditor;

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
        mList = new ArrayList<>();
        mEditor = getContext().getSharedPreferences("data", MODE_PRIVATE).edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // account,fixed_charge,statistics对话框
    public void initAccountOrFixedChargeOrStatisticsDialog(String text, List<T> list, int flag) {
        setContentView(R.layout.dialog);
        mList = list;
        dialogClose = (ImageView) findViewById(R.id.dialog_close);
        mListView = (ListView) findViewById(R.id.dialogListView);
        title = (TextView) findViewById(R.id.title);

        title.setText(text);

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (flag == Constant.ACCOUNT_AND_FIXED_CHARGED) {
            mListView.setAdapter(new MyBaseAdapter<T>(getContext(), mList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_listview_item, null);
                    TextView text = (TextView) view.findViewById(R.id.dialog_listView_text);
                    text.setText((String) mList.get(position));
                    return view;
                }
            });
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    setSelect((String) mList.get(i));
                    dismiss();
                }
            });
        } else if (flag == Constant.STATISTICS) {
            mListView.setAdapter(new MyBaseAdapter<T>(getContext(), mList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_statistics_listview_item, null);
                    TextView date = (TextView) view.findViewById(R.id.dialog_listView_date);
                    TextView account = (TextView) view.findViewById(R.id.dialog_listView_account);
                    TextView money = (TextView) view.findViewById(R.id.dialog_listView_money);
                    date.setText(((DataBean) mList.get(position)).getDate());
                    account.setText(((DataBean) mList.get(position)).getAccount());
                    money.setText(((DataBean) mList.get(position)).getMoney());
                    return view;
                }
            });
        }
    }

    // 未输入金额或类型对话框
    public void initSaveButtonDialog(String text) {
        setContentView(R.layout.dialog_confirmt);
        TextView title = (TextView) findViewById(R.id.dialog_confirm_text);
        title.setText("请输入" + text);

        findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    // 自定义title选择对话框
    public void initSelectDialog(String text) {
        setContentView(R.layout.dialog_select);
        ((TextView) findViewById(R.id.dialog_select_text)).setText(text);
    }

    // 更新公告updateAnnouncement对话框
    public void initUpdateDialog() {
        setContentView(R.layout.dialog_update);
        title = (TextView) findViewById(R.id.title);
        final CheckBox updateCheck = (CheckBox) findViewById(R.id.update_check);
        TextView confirm = (TextView) findViewById(R.id.dialog_confirm);

        updateCheck.setChecked(InitData.isNoLongerPrompt);

        update_content = (TextView) findViewById(R.id.update_content);
        update_content.setText(Constant.UPDATE);
        title.setText("更新公告");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.putBoolean("isNoLongerPrompt", updateCheck.isChecked());
                InitData.isNoLongerPrompt = updateCheck.isChecked();
                mEditor.apply();
                dismiss();
            }
        });

    }

    // 余量球颜色选择对话框
    public void initBallColorDialog() {
        setContentView(R.layout.dialog_ball_color);
        mListView = (ListView) findViewById(R.id.ball_color_listView);

        ballList = BallListUtil.getBallList();

        mListView.setAdapter(new MyBaseAdapter<Ball>(getContext(), ballList) {
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

    // 自定义title确认对话框
    public void initConfirmDialog(String text) {
        setContentView(R.layout.dialog_confirmt);
        ((TextView) findViewById(R.id.dialog_confirm_text)).setText(text);
    }

    // 备份还原对话框
    public void initBackupAndRestoreDialog() {
        setContentView(R.layout.dialog_backup_and_restore);
        findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    // 有固定支出时的删除对话框
    public void initDeleteDialog() {
        setContentView(R.layout.dialog_delete);
    }

    // 日期选择对话框
    public void initDateDialog() {
        setContentView(R.layout.dialog_statisctics_date);

        findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }
}
