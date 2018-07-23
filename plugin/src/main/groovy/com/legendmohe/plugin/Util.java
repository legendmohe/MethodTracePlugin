package com.legendmohe.plugin;

/**
 * Created by hexinyu on 2018/7/21.
 */
public class Util {
    public static void log(String ... msg) {
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
}
