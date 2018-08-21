package com.legendmohe.tracelib;

public class MethodTrace {
    private static boolean enabled = false;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        MethodTrace.enabled = enabled;
    }
}
