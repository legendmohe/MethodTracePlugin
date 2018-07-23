package com.legendmohe.methoddiff;

/**
 * Created by hexinyu on 2018/7/20.
 */
public class Test2 {
    public int run(int c, char d){
        int a = 1;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        run2(a, (char) a);
        return a + 2;
    }

    private int run2(int c, char d){
        int a = 1;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a + 2;
    }
}
