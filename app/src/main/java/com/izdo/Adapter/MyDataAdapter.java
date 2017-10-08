package com.izdo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.izdo.Bean.DataBean;
import com.izdo.Bean.TypeMap;
import com.izdo.MainActivity;
import com.izdo.R;
import com.izdo.Util.Constant;

import java.util.List;

/**
 * Created by iZdo on 2017/10/3.
 */

public class MyDataAdapter extends RecyclerView.Adapter<MyDataAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;

    private List<DataBean> mList;
    private TypeMap mTypeMap;

    private OnItemClickListener mOnItemClickListener = null;

    public MyDataAdapter(Context mContext) {
        this.mContext = mContext;
        mTypeMap = new TypeMap();
    }

    public void setList(List<DataBean> list) {
        mList = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mainItem;
        ImageView mainImg;
        TextView mainTypeText;
        TextView mainMoneyText;

        public ViewHolder(View itemView) {
            super(itemView);
            mainItem= (LinearLayout) itemView.findViewById(R.id.main_listView_item);
            mainImg = (ImageView) itemView.findViewById(R.id.main_listView_type_img);
            mainTypeText = (TextView) itemView.findViewById(R.id.main_listView_type_text);
            mainMoneyText = (TextView) itemView.findViewById(R.id.main_listView_money_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (MainActivity.behavior.equals(Constant.OUTCOME))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_outcome_listview_item, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_income_listview_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataBean dataBean = mList.get(position);
        holder.mainImg.setImageResource(mTypeMap.queryTypeImg(dataBean.getType()));
        holder.mainTypeText.setText(dataBean.getType());
        holder.mainMoneyText.setText("¥" + dataBean.getMoney());
        holder.mainItem.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View view) {
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
}


