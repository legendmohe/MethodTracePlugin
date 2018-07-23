package com.legendmohe.gradle.methodtrace;

import java.util.Arrays;

/**
 * Created by hexinyu on 2018/7/21.
 */
public class TraceConfig {
    static final String ENTER_INDICATOR = "enter";

    static final String EXIT_INDICATOR = "exit";

    public static final String[] TARGET_PACKAGE_PATH = new String[]{"com.legendmohe.methoddiff"};

    /*
    onTrace(boolean dirIn, String threadName, String className, String methodName, long ts, long objHash)
     */
    static String STATEMENT_INSERT_BEFORE = "{" +
            "com.legendmohe.methodtrace.TraceMonitor.getInstance().onTrace(true, Thread.currentThread().getName(), \"%c\", \"%m\", System.currentTimeMillis(), %t);" +
            "}";

    static String STATEMENT_INSERT_AFTER = "{" +
            "com.legendmohe.methodtrace.TraceMonitor.getInstance().onTrace(false, Thread.currentThread().getName(), \"%c\", \"%m\", System.currentTimeMillis(), %t);" +
            "}";

    static String[] SKIP_CLASSES = new String[]{"R$", "R.class", "BuildConfig.class", "com.legendmohe.methodtrace.TraceMonitor"};

    // 不要处理hashCode，避免死循环
    static String[] SKIP_METHOD_SUFFIX = new String[]{".hashCode()", "onTrace(com.legendmohe.methodtrace.TraceMonitor$TraceNode)", "onClearBuffer()"};

    static String[] SKIP_METHOD_PREFIX = new String[]{"java.lang."};

    public String[] targetPackagePath = TARGET_PACKAGE_PATH;
    public String[] skipClasses = SKIP_CLASSES;
    public String[] skipMethodSuffix = SKIP_METHOD_SUFFIX;
    public String[] skipMethodprefix = SKIP_METHOD_PREFIX;

    @Override
    public String toString() {
        return "TraceConfig{" +
                ", targetPackagePath=" + Arrays.toString(targetPackagePath) +
                ", skipClasses=" + Arrays.toString(skipClasses) +
                ", skipMethodSuffix=" + Arrays.toString(skipMethodSuffix) +
                ", skipMethodprefix=" + Arrays.toString(skipMethodprefix) +
                '}';
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
