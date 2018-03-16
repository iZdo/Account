package com.izdo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.izdo.R;
import com.izdo.fragment.StatisticsBudgetFragment;
import com.izdo.fragment.StatisticsFragment;
import com.izdo.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iZdo on 2017/11/5.
 */
public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout statistics;

    private ViewPager viewPager;
    private List<Fragment> fragments;
    String[] fragmentType = {Constant.OUTCOME, Constant.INCOME, Constant.OUTCOME};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        init();
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.statistics_viewpager);

        statistics = (LinearLayout) findViewById(R.id.statistics);
        statistics.setOnClickListener(this);

        initViewpager();
    }

    private void initViewpager() {
        fragments = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            StatisticsFragment statisticsFragment = StatisticsFragment.newInstance(fragmentType[i]);
            fragments.add(statisticsFragment);
        }

        StatisticsBudgetFragment statisticsBudgetFragment = new StatisticsBudgetFragment();
        fragments.add(statisticsBudgetFragment);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragments.get(position).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.statistics:
                finish();
                break;
        }
    }
}
