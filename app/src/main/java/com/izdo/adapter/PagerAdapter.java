package com.izdo.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by iZdo on 2017/4/16.
 */

public class PagerAdapter extends android.support.v4.view.PagerAdapter {

    private List<View> mViewList;

    public PagerAdapter(List<View> mViewList) {
        this.mViewList = mViewList;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup parent = (ViewGroup) mViewList.get(position).getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }
}
