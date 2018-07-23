package com.legendmohe.methodtrace;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TraceMonitor {
    private static final String TAG = "TraceMonitor";

    public static final String THREAD_NAME_MAIN = "main";

    private static final int MAX_BUFFER_NUMBER = 1000;

    private boolean mEnable = false;

    private boolean mPrintLogcat = false;

    private static class LazyHolder {
        private static final TraceMonitor INSTANCE = new TraceMonitor();
    }

    public static TraceMonitor getInstance() {
        return LazyHolder.INSTANCE;
    }

    private ConcurrentHashMap<String, CircularFifoQueue<TraceNode>> mBuffer = new ConcurrentHashMap<>();

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
        CircularFifoQueue<TraceNode> traceNodes = mBuffer.get(threadName);
        if (traceNodes == null) {
            mBuffer.putIfAbsent(threadName, new CircularFifoQueue<TraceNode>(MAX_BUFFER_NUMBER));
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

        if (mPrintLogcat) {
            System.out.println(printFormattedNodeInfo(newNode));
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

    public List<TraceNode> getBuffer(String threadName) {
        CircularFifoQueue<TraceNode> traceNodes = mBuffer.get(threadName);
        return new ArrayList<>(traceNodes);
    }

    public List<String> getThreadNames() {
        ArrayList<String> arrayList = new ArrayList<>();
        Enumeration<String> enumeration = mBuffer.keys();
        while (enumeration.hasMoreElements()) {
            arrayList.add(enumeration.nextElement());
        }
        return arrayList;
    }

    //////////////////////////////////interface////////////////////////////////////

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
