package com.ztesoft.zsmart.perf.flow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FsMapManager {

    private static final FlowStatMapWrapper totalFlow = new FlowStatMapWrapper("total");

    private static final ConcurrentMap<String, FlowStatMapWrapper> busiFlowMap = new ConcurrentHashMap<String, FlowStatMapWrapper>();

    public FsMapManager() {
        
    }

    /**
     * get totol flow statistics recode
     * 
     * @return
     */
    public FlowStat getCurrTotalFlow() {
        return totalFlow.getCurrentFlowStat();
    }

    public FlowStat getTotalFlowLastSecond() {
        return totalFlow.getFlowStatLastSecond();
    }

    public List<FlowStat> getTotalFlowStatByIndex(int start, int end) {
        return totalFlow.getFlowStatByIndex(start, end);
    }

    public List<FlowStat> getBusiFlowStatByIndex(int start, int end) {
        List<FlowStat> retList = new ArrayList<FlowStat>();
        Iterator<FlowStatMapWrapper> ir = busiFlowMap.values().iterator();
        while (ir.hasNext()) {
            retList.addAll(ir.next().getFlowStatByIndex(start, end));
        }
        return retList;
    }

    /**
     * get last bussiness flow statistics recode
     * 
     * @param busiName
     * @return
     */
    public FlowStat getCurrBusiFlow(String busiName) {
        synchronized (busiFlowMap) {
            if (busiFlowMap.containsKey(busiName)) {
                return busiFlowMap.get(busiName).getCurrentFlowStat();
            }
            FlowStatMapWrapper wrapper = new FlowStatMapWrapper(busiName);
            busiFlowMap.put(busiName, wrapper);
            return wrapper.getCurrentFlowStat();
        }
    }

}
