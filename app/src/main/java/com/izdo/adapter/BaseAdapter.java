package com.izdo.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by iZdo on 2017/10/15.
 */

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private Context mContext;
    private List<T> mList;

    public BaseAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
