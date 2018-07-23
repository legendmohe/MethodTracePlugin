package com.legendmohe.plugin;

import java.util.Arrays;

/**
 * Created by hexinyu on 2018/7/21.
 */
public class TraceConfig {
    static final String ENTER_INDICATOR = "enter";

    static final String EXIT_INDICATOR = "exit";

    public static final String[] TARGET_PACKAGE_PATH = new String[]{"com.legendmohe.methoddiff"};

    static String STATEMENT_INSERT_BEFORE = "{" +
            "System.out.println(\"<---> " + ENTER_INDICATOR + " \" + Thread.currentThread().getName() + \"|%c|%m()|\" + System.currentTimeMillis() + \"|\" + %t);" +
            "}";

    static String STATEMENT_INSERT_AFTER = "{" +
            "System.out.println(\"<---> " + EXIT_INDICATOR + " \" + Thread.currentThread().getName() + \"|%c|%m()|\" + System.currentTimeMillis() + \"|\" + %t);" +
            "}";

    static String[] SKIP_CLASSES = new String[]{"R$", "R.class", "BuildConfig.class"};

    // 不要处理hashCode，避免死循环
    static String[] SKIP_METHOD_SUFFIX = new String[]{".hashCode()"};

    static String[] SKIP_METHOD_PREFIX = new String[]{"java.lang."};


    public String enterIndicator = ENTER_INDICATOR;
    public String exitIndicator = EXIT_INDICATOR;
    public String[] targetPackagePath = TARGET_PACKAGE_PATH;
    public String[] skipClasses = SKIP_CLASSES;
    public String[] skipMethodSuffix = SKIP_METHOD_SUFFIX;
    public String[] skipMethodprefix = SKIP_METHOD_PREFIX;

    @Override
    public String toString() {
        return "TraceConfig{" +
                "enterIndicator='" + enterIndicator + '\'' +
                ", exitIndicator='" + exitIndicator + '\'' +
                ", targetPackagePath=" + Arrays.toString(targetPackagePath) +
                ", skipClasses=" + Arrays.toString(skipClasses) +
                ", skipMethodSuffix=" + Arrays.toString(skipMethodSuffix) +
                ", skipMethodprefix=" + Arrays.toString(skipMethodprefix) +
                '}';
    }

    public void setEnterIndicator(String enterIndicator) {
        this.enterIndicator = enterIndicator;
    }

    public void setExitIndicator(String exitIndicator) {
        this.exitIndicator = exitIndicator;
    }

    public void setTargetPackagePath(String[] targetPackagePath) {
        this.targetPackagePath = targetPackagePath;
    }

    public void setSkipClasses(String[] skipClasses) {
        this.skipClasses = skipClasses;
    }

    public void setSkipMethodSuffix(String[] skipMethodSuffix) {
        this.skipMethodSuffix = skipMethodSuffix;
    }

    public void setSkipMethodprefix(String[] skipMethodprefix) {
        this.skipMethodprefix = skipMethodprefix;
    }
}
