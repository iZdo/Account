package com.izdo.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.izdo.MainFragment;

import java.util.List;

/**
 * Created by iZdo on 2017/4/22.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<MainFragment> mFragmentList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<MainFragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    public void setFragmentList(List<MainFragment> fragmentList) {
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        //        return mFragmentList.size();
        // 返回最大值 实现无限循环
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
        //        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MainFragment mainFragment = mFragmentList.get(position % mFragmentList.size());
        mainFragment.setDate(position);
        position = position % mFragmentList.size();
        return super.instantiateItem(container, position);
    }
}
