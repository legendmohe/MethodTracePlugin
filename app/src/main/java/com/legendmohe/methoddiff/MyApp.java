package com.legendmohe.methoddiff;

import android.content.Context;

import com.legendmohe.testmodeule.AbstractApp;

/**
 * Created by hexinyu on 2018/7/23.
 */
public class MyApp extends AbstractApp {
    private static final String TAG = "MyApp";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        doIt();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
