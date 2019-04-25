package com.example.pi;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment>mFragmentList = new ArrayList<>();
    private final List<String>mTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment,String title){
        mFragmentList.add(fragment);
        mTitleList.add(title);
    }

    public SectionsPageAdapter(FragmentManager fa) {
        super(fa);

    }
    @Override
    public CharSequence getPageTitle(int position){
        return mTitleList.get(position);

    }
    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
