package com.legendmohe.methoddiff;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private Test mTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("--->", "===================");
        new Thread(new Runnable() {
            @Override
            public void run() {
                run2(2, (char) 3);
                new Test().run(2, (char) 3);
            }
        }, "bg").start();
        new Test().run(2, (char) 3);
        new Test().run(2, (char) 3);
        new Test().run(2, (char) 3);
        mTest = new Test();
        mTest.run(2, (char) 4);
        hashCode();
        Log.e("--->", "===================");
    }

    private static int run2(int c, char d) {
        int a = 1;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a + 2;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + mTest.hashCode();
    }
}
