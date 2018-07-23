package com.legendmohe.methoddiff;

import android.app.Application;
import android.content.Context;

import com.legendmohe.methodtrace.TraceMonitor;

/**
 * Created by hexinyu on 2018/7/23.
 */
public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    protected void attachBaseContext(Context base) {
        TraceMonitor.getInstance().setListener(new TraceMonitor.Listener() {
            @Override
            public void onTrace(TraceMonitor.TraceNode node) {
//                Log.d(TAG, "onTrace() called with: node = " + TraceMonitor.printFormattedNodeInfo(node));
            }

            @Override
            public void onClearBuffer() {
            }
        });
        TraceMonitor.getInstance().setEnable(true);
        TraceMonitor.getInstance().setPrintLogcat(true);

        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
