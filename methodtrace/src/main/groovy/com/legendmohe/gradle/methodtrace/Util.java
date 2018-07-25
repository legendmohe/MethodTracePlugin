package com.legendmohe.gradle.methodtrace;

/**
 * Created by hexinyu on 2018/7/21.
 */
public class Util {
    public static void log(String... msg) {
        if (msg == null || msg.length == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-> ");
        for (String s : msg) {
            sb.append(s.trim()).append(" ");
        }
        System.out.println(sb.toString());
    }

    public static boolean containsIn(String src, String[] samples) {
        if (src == null || samples == null || samples.length == 0) {
            return false;
        }

        for (String sample : samples) {
            if (src.contains(sample)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWith(String src, String[] samples) {
        if (src == null || samples == null || samples.length == 0) {
            return false;
        }

        for (String sample : samples) {
            if (src.startsWith(sample)) {
                return true;
            }
        }
        return false;
    }

    public static boolean endsWith(String src, String[] samples) {
        if (src == null || samples == null || samples.length == 0) {
            return false;
        }

        for (String sample : samples) {
            if (src.endsWith(sample)) {
                return true;
            }
        }
        return false;
    }
}
