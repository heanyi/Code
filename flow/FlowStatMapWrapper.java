package com.ztesoft.zsmart.perf.flow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowStatMapWrapper {
    private FlowStat[] flowStatArr;

    private AtomicInteger lastIndex = new AtomicInteger(-1);

    private String name;

    private static final int saveFlowRecordCnt = 60;

    public FlowStatMapWrapper(String name) {
        flowStatArr = new FlowStat[saveFlowRecordCnt];
        for (int i = 0; i < saveFlowRecordCnt; i++) {
            flowStatArr[i] = new FlowStat(i, name);
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * get current flow stat record
     * 
     * @return
     */
    public FlowStat getCurrentFlowStat() {
        Calendar now = Calendar.getInstance();
        int current = now.get(Calendar.SECOND);
        if (lastIndex.get() != current) {
            resetFlowStatByIndex(current);
        }
        return flowStatArr[current];
    }

    /**
     * get flow stat last second
     * 
     * @return
     */
    public FlowStat getFlowStatLastSecond() {
        getCurrentFlowStat();
        Calendar now = Calendar.getInstance();
        int current = now.get(Calendar.SECOND);
        int last = current - 1;
        if (last < 0) {
            last = saveFlowRecordCnt - 1;
        }
        return flowStatArr[last];

    }

    private synchronized void resetFlowStatByIndex(int current) {
        if (lastIndex.get() == current) {
            return;
        }
        int start = lastIndex.get() + 1;
        int end = current + 1;
        if (end >= start) {
            for (int i = start; i < end; i++) {
                flowStatArr[i].reset();
            }
        }
        else {
            for (int i = start; i < saveFlowRecordCnt; i++) {
                flowStatArr[i].reset();
            }
            for (int i = 0; i < end; i++) {
                flowStatArr[i].reset();
            }
        }
        lastIndex.set(current);
    }

    public List<FlowStat> getFlowStatByIndex(int start, int end) {
        getCurrentFlowStat();
        List<FlowStat> retList = new ArrayList<FlowStat>();

        if (end >= start) {
            for (int i = start; i < end; i++) {
                retList.add(flowStatArr[i]);
            }
        }
        else {
            for (int i = start; i < saveFlowRecordCnt; i++) {
                retList.add(flowStatArr[i]);
            }
            for (int i = 0; i < end; i++) {
                retList.add(flowStatArr[i]);
            }
        }
        return retList;
    }

    // method for total response time control
    // public List<FlowStat> getFlowStatBySecondsWithCurrent(int seconds) {
    // if (seconds == 0) {
    // return null;
    // }
    // Calendar now = Calendar.getInstance();
    // int current = now.get(Calendar.SECOND);
    // if (lastIndex.get() != current) {
    // resetFlowStatByIndex(lastIndex.get() + 1, current + 1);
    // lastIndex.set(current);
    // }
    // if (seconds >= saveFlowRecordCnt) {
    // seconds = saveFlowRecordCnt - 1;
    // }
    // int start = current - seconds;
    // if (start < 0) {
    // start = saveFlowRecordCnt + start;
    // }
    // 这里加1秒是为了获取当前秒的统计信息，用当前秒计算时平均处理时间可能不准
    // 但是可以防止由于统计信息不变，导致流量控制拒绝1秒内的所有流量
    // return getFlowStatByIndex(start + 1, current + 1);
    // }
}
