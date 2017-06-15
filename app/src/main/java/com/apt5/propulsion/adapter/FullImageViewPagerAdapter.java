package com.apt5.propulsion.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.apt5.propulsion.fragment.FullImageFragment;

import java.util.ArrayList;


public class FullImageViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> listUrlPhoto;

    public FullImageViewPagerAdapter(FragmentManager fm, ArrayList<String> listUrlPhoto) {
        super(fm);
        this.listUrlPhoto = listUrlPhoto;
    }

    @Override
    public Fragment getItem(int i) {
        return FullImageFragment.newInstance(listUrlPhoto.get(i));
    }

    @Override
    public int getCount() {
        return listUrlPhoto.size();
    }

    public String getCurrentUrl(int position) {
        return listUrlPhoto.get(position);
    }
}
