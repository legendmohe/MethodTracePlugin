package com.legendmohe.gradle.methodtrace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    static String[] SKIP_METHOD_CONTAINS = new String[]{".hashCode()", ".onTrace(com.legendmohe.methodtrace.TraceMonitor$TraceNode)", ".onClearBuffer()", "java.lang.", "access$"};

    public String[] targetPackagePath = TARGET_PACKAGE_PATH;
    public String[] skipClasses = SKIP_CLASSES;
    public String[] skipMethodContains = SKIP_METHOD_CONTAINS;

    @Override
    public String toString() {
        return "TraceConfig{" +
                ", targetPackagePath=" + Arrays.toString(targetPackagePath) +
                ", skipClasses=" + Arrays.toString(skipClasses) +
                ", skipMethodContains=" + Arrays.toString(skipMethodContains) +
                '}';
    }

    public void setTargetPackagePath(String[] targetPackagePath) {
        this.targetPackagePath = targetPackagePath;
    }

    public void setSkipClasses(String[] skipClasses) {
        String[] resultClasses = mergeParamsWithDefault(skipClasses, this.skipClasses);
        this.skipClasses = resultClasses;
    }

    public void setSkipMethodContains(String[] skipMethods) {
        String[] resultClasses = mergeParamsWithDefault(skipMethods, this.skipMethodContains);
        this.skipMethodContains = resultClasses;
    }

    private String[] mergeParamsWithDefault(String[] src, String[] target) {
        Set<String> result = new HashSet<>();
        result.addAll(Arrays.asList(target));
        result.addAll(Arrays.asList(src));

        return result.toArray(new String[result.size()]);
    }
}
