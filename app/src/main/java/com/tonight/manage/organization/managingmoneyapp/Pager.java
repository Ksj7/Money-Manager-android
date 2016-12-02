package com.tonight.manage.organization.managingmoneyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by 3 on 2016-11-14.
 */

public class Pager extends FragmentStatePagerAdapter {
    int tabCount;
    String eventName;
    String eventnum;

    public Pager(FragmentManager fm, int tabCount , String eventName, String eventnum) {
        super(fm);
        this.tabCount = tabCount;
        this.eventName = eventName;
        this.eventnum = eventnum;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PaymentFragment paymentFragment = new PaymentFragment();
                Bundle b = new Bundle();
                b.putString("eventName", eventName);
                b.putString("eventnum",eventnum);
                paymentFragment.setArguments(b);
                return paymentFragment;
            case 1:
                UserFragment userFragment = new UserFragment();
                return userFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}