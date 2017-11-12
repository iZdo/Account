package com.izdo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.izdo.Bean.StatisticsBean;
import com.izdo.R;
import com.izdo.Util.TypeMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iZdo on 2017/11/5.
 */

public class MyStatisticsAdapter extends RecyclerView.Adapter<MyStatisticsAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<StatisticsBean> mDataList = new ArrayList<>();

    private TypeMap mTypeMap;
    private OnItemClickListener mOnItemClickListener = null;
    // 小数格式
    DecimalFormat decimalFormat = new DecimalFormat("0.0");


    public MyStatisticsAdapter(Context context, List dataList) {
        this.mContext = context;
        this.mDataList = dataList;
        mTypeMap = new TypeMap();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout statistics;
        TextView index;
        ImageView typeImage;
        TextView percent;
        TextView type;
        TextView totalMoney;

        public ViewHolder(View itemView) {
            super(itemView);
            statistics = (LinearLayout) itemView.findViewById(R.id.statistics_recyclerView_item);
            index = (TextView) itemView.findViewById(R.id.index);
            typeImage = (ImageView) itemView.findViewById(R.id.type_image);
            percent = (TextView) itemView.findViewById(R.id.percent);
            type = (TextView) itemView.findViewById(R.id.type);
            totalMoney = (TextView) itemView.findViewById(R.id.total_money);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_recyclerview_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.index.setText(position + 1 + "");
        holder.typeImage.setImageResource(mTypeMap.queryTypeBlackImg(mDataList.get(position).getType()));
        holder.percent.setText(decimalFormat.format(mDataList.get(position).getPercent()) + "%");
        holder.type.setText(mDataList.get(position).getType());
        holder.totalMoney.setText(mDataList.get(position).getMoney() + "元");
        holder.statistics.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
