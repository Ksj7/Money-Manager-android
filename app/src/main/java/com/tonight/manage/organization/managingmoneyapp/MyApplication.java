package com.tonight.manage.organization.managingmoneyapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by 10 on 2016-11-23.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }


    public static Context getItemContext() {
        return mContext;
    }
}

