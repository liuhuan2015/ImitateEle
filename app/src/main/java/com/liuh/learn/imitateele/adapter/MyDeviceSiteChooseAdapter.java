package com.liuh.learn.imitateele.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.liuh.learn.imitateele.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2018/3/6 15:19
 * Description:我的设备的位置搜索的viewpager的适配器
 */

public class MyDeviceSiteChooseAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> fragments = new ArrayList<BaseFragment>();

    private List<String> titles = new ArrayList<String>();

    public MyDeviceSiteChooseAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
