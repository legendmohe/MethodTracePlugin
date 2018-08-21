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

    static final String[] TARGET_PACKAGE_PATH = new String[]{};

    static String[] TARGET_CLASSES = new String[]{};

    static String[] TARGET_PROJECTS = new String[]{};

    /*
    onTrace(boolean dirIn, String threadName, String className, String methodName, long ts, long objHash)
     */
    static String STATEMENT_INSERT_BEFORE = "{" +
            "if(com.legendmohe.tracelib.MethodTrace.isEnabled())System.out.println(\"<---> " + ENTER_INDICATOR + " \" + Thread.currentThread().getName() + \"|%c|%m()|\" + System.currentTimeMillis() + \"|\" + %t);" +
            "android.support.v4.os.TraceCompat.beginSection(\"%c#%m\");" +
            "}";

    static String STATEMENT_INSERT_AFTER = "{" +
            "if(com.legendmohe.tracelib.MethodTrace.isEnabled())System.out.println(\"<---> " + EXIT_INDICATOR + " \" + Thread.currentThread().getName() + \"|%c|%m()|\" + System.currentTimeMillis() + \"|\" + %t);" +
            "android.support.v4.os.TraceCompat.endSection();" +
            "}";

    static String[] SKIP_CLASSES = new String[]{"com.legendmohe.tracelib", ".R$"};

    static String[] SKIP_CLASSES_SUFFIX_INTERNAL = new String[]{".R", ".BuildConfig"};

    static String[] SKIP_PACKAGES = new String[]{"java.lang.", "android."};

    // 不要处理hashCode，避免死循环
    static String[] SKIP_METHOD_CONTAINS = new String[]{".hashCode()", "java.lang.", "access$"};

    public String[] targetPackagePath = TARGET_PACKAGE_PATH;
    public String[] targetProjects = TARGET_PROJECTS;
    public String[] skipClasses = SKIP_CLASSES;
    public String[] targetClasses = TARGET_CLASSES;
    public String[] skipPackages = SKIP_PACKAGES;
    public String[] skipMethodContains = SKIP_METHOD_CONTAINS;

    @Override
    public String toString() {
        return "TraceConfig{" +
                "targetPackagePath=" + Arrays.toString(targetPackagePath) +
                ", targetClasses=" + Arrays.toString(targetClasses) +
                ", targetProjects=" + Arrays.toString(targetProjects) +
                ", skipClasses=" + Arrays.toString(skipClasses) +
                ", skipPackages=" + Arrays.toString(skipPackages) +
                ", skipMethodContains=" + Arrays.toString(skipMethodContains) +
                '}';
    }

    public void setTargetPackagePath(String[] targetPackagePath) {
        this.targetPackagePath = targetPackagePath;
    }

    public void setTargetClasses(String[] targetClasses) {
        this.targetClasses = targetClasses;
    }

    public void setTargetProjects(String[] targetProjects) {
        this.targetProjects = targetProjects;
    }

    public void setSkipClasses(String[] skipClasses) {
        String[] resultClasses = mergeParamsWithDefault(skipClasses, this.skipClasses);
        this.skipClasses = resultClasses;
    }

    public void setSkipMethodContains(String[] skipMethods) {
        String[] resultClasses = mergeParamsWithDefault(skipMethods, this.skipMethodContains);
        this.skipMethodContains = resultClasses;
    }

    public void setSkipPackages(String[] skipPackages) {
        String[] resultClasses = mergeParamsWithDefault(skipPackages, this.skipPackages);
        this.skipPackages = resultClasses;
    }

    private String[] mergeParamsWithDefault(String[] src, String[] target) {
        Set<String> result = new HashSet<>();
        result.addAll(Arrays.asList(target));
        result.addAll(Arrays.asList(src));

        return result.toArray(new String[result.size()]);
    }
}
