package com.legendmohe.testmodeule;

import android.app.Application;
import android.util.Log;

/**
 * Created by hexinyu on 2018/7/24.
 */
public class AbstractApp extends Application {
    private static final String TAG = "AbstractApp";

    public void doIt() {
        Log.d(TAG, "do it!");
    }
}
