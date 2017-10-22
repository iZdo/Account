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
import com.izdo.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by iZdo on 2017/4/25.
 */

public class MyDialog extends Dialog {

    private ImageView dialogClose;
    private ListView mListView;
    private TextView title;
    private TextView update_content;
    private String select;
    private List<String> mList;

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

    // account，fixed_charge对话框
    public void initAccountOrFixedChargeDialog(String text, List<String> list) {
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

        mListView.setAdapter(new MyBaseAdapter<String>(getContext(), mList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_listview_item, null);
                TextView text = (TextView) view.findViewById(R.id.dialog_listView_text);
                text.setText(mList.get(position));
                return view;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setSelect(mList.get(i));
                dismiss();
            }
        });
    }

    // 未输入金额或类型对话框
    public void initSaveButtonDialog(String text) {
        setContentView(R.layout.dialog_save);
        TextView title = (TextView) findViewById(R.id.dialog_save_text);
        title.setText("请输入" + text);

        findViewById(R.id.dialog_confirm_button).setOnClickListener(new View.OnClickListener() {
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
    public void initConfirmDialog(String text){
        setContentView(R.layout.dialog_confirmt);
        ((TextView) findViewById(R.id.dialog_confirm_text)).setText(text);
    }

    // 有固定支出时的删除对话框
    public void initDeleteDialog(){
        setContentView(R.layout.dialog_delete);

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
