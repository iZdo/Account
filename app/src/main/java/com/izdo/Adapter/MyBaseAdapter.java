package com.izdo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.izdo.Bean.DataBean;
import com.izdo.Bean.TypeMap;
import com.izdo.MainActivity;
import com.izdo.R;

import java.util.List;

/**
 * Created by iZdo on 2017/4/22.
 */

public class MyBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<DataBean> mList;
    private ViewHolder mViewHolder;
    private View mView;
    private TypeMap mTypeMap;

    public MyBaseAdapter(Context context, List<DataBean> list) {
        mContext = context;
        mList = list;
        mTypeMap = new TypeMap();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            if (MainActivity.behavior.equals("outcome"))
                mView = LayoutInflater.from(mContext).inflate(R.layout.main_outcome_listview_item, null);
            else if (MainActivity.behavior.equals("income"))
                mView = LayoutInflater.from(mContext).inflate(R.layout.main_income_listview_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.mainImg = (ImageView) mView.findViewById(R.id.main_listView_type_img);
            mViewHolder.mainTypeText = (TextView) mView.findViewById(R.id.main_listView_type_text);
            mViewHolder.mainMoneyText = (TextView) mView.findViewById(R.id.main_listView_money_text);
            mView.setTag(mViewHolder);
        } else {
            mView = convertView;
            mViewHolder = (ViewHolder) mView.getTag();
        }

        mViewHolder.mainImg.setImageResource(mTypeMap.queryTypeImg(mList.get(i).getType()));
        mViewHolder.mainTypeText.setText(mList.get(i).getType());
        mViewHolder.mainMoneyText.setText("¥" + mList.get(i).getMoney());
        return mView;
    }

    class ViewHolder {
        ImageView mainImg;
        TextView mainTypeText;
        TextView mainMoneyText;
    }
}
