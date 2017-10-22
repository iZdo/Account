package com.izdo.Adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by iZdo on 2017/10/15.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private Context mContext;
    private List<T> mList;

    public MyBaseAdapter(Context context, List<T> list) {
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
