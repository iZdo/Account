package com.izdo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.izdo.R;

import java.util.List;

/**
 * Created by iZdo on 2017/10/15.
 */

public class MyAccountAdapter extends MyBaseAdapter<String> {

    private Context mContext;
    private List<String> mList;

    public MyAccountAdapter(Context context, List<String> list) {
        super(context, list);
        mContext = context;
        mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.account_manage_listview_item, null);

        TextView text = (TextView) view.findViewById(R.id.account_text);
        String accountName = mList.get(position);
        text.setText(accountName);

        return view;
    }
}