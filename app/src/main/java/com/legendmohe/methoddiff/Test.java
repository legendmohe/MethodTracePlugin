package com.legendmohe.methoddiff;

/**
 * Created by hexinyu on 2018/7/20.
 */
public class Test {
    public int run(int c, char d){
        int a = 1;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Test2().run(a, (char) c);
        run2(a, (char) c);
        return a + 2;
    }

    protected int run2(int c, char d){
        int a = 1;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a + 2;
    }
}
