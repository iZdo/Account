package com.izdo.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.izdo.MainFragment;

import java.util.ArrayList;

/**
 * Created by iZdo on 2017/4/22.
 */

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<MainFragment> mFragmentList;

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<MainFragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
