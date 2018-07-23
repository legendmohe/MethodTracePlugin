package com.legendmohe.methodtrace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TraceMonitor {
    private static final String TAG = "TraceMonitor";

    private boolean mEnable = false;

    private boolean mPrintLogcat = false;

    private Listener mListener;

    private static class LazyHolder {
        private static final TraceMonitor INSTANCE = new TraceMonitor();
    }

    public static TraceMonitor getInstance() {
        return LazyHolder.INSTANCE;
    }

    private ConcurrentHashMap<String, List<TraceNode>> mBuffer = new ConcurrentHashMap<>();

    ///////////////////////////////////public///////////////////////////////////

    /**
     * DIRECTION dir;
     * String threadName;
     * String className;
     * String methodName;
     * long ts;
     * long hash;
     */
    public void onTrace(boolean dirIn, String threadName, String className, String methodName, long ts, int objHash) {
        if (!mEnable) {
            return;
        }
        List<TraceNode> traceNodes = mBuffer.get(threadName);
        if (traceNodes == null) {
            mBuffer.putIfAbsent(threadName, new ArrayList<TraceNode>());
            // 再拿一次，避免竞争引起的list覆盖问题
            traceNodes = mBuffer.get(threadName);
        }
        TraceNode newNode = new TraceNode();
        newNode.dir = dirIn ? DIRECTION.IN : DIRECTION.OUT;
        newNode.threadName = threadName;
        newNode.className = className;
        newNode.methodName = methodName;
        newNode.ts = ts;
        newNode.hash = objHash;
        traceNodes.add(newNode);
        if (mListener != null) {
            mListener.onTrace(newNode);
        }

        if (mPrintLogcat) {
            System.out.println(printFormattedNodeInfo(newNode));
        }
    }

    public void clearBuffer() {
        mBuffer.clear();
        if (mListener != null) {
            mListener.onClearBuffer();
        }
    }

    public static String printFormattedNodeInfo(TraceNode node) {
        StringBuffer sb = new StringBuffer();
        if (node.dir == DIRECTION.IN) {
            sb.append("<---> enter ")
                    .append(node.threadName).append("|")
                    .append(node.className).append("|")
                    .append(node.methodName).append("|")
                    .append(node.ts).append("|")
                    .append(node.hash);
        } else {
            sb.append("<---> exit ")
                    .append(node.threadName).append("|")
                    .append(node.className).append("|")
                    .append(node.methodName).append("|")
                    .append(node.ts).append("|")
                    .append(node.hash);
        }
        return sb.toString();
    }

    ///////////////////////////////////getter&setter///////////////////////////////////

    public boolean isEnable() {
        return mEnable;
    }

    public void setEnable(boolean enable) {
        mEnable = enable;
    }

    public boolean isPrintLogcat() {
        return mPrintLogcat;
    }

    public void setPrintLogcat(boolean printLogcat) {
        mPrintLogcat = printLogcat;
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    //////////////////////////////////interface////////////////////////////////////

    public interface Listener {
        void onTrace(TraceNode node);

        void onClearBuffer();
    }

    public static class TraceNode {
        // format: <---> exit main|com.legendmohe.methoddiff.MainActivity|onCreate()|4126|188072276
        DIRECTION dir;
        String threadName;
        String className;
        String methodName;
        long ts;
        int hash;
    }

    private enum DIRECTION {
        IN,
        OUT
    }
}
