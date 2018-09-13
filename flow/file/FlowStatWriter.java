package com.ztesoft.zsmart.perf.flow.file;

import java.util.List;

import com.ztesoft.zsmart.perf.flow.FlowStat;

public interface FlowStatWriter {

    public void writeTotalFlowStat(List<FlowStat> flowStatList);

    public void writeBusiFlowFile(List<FlowStat> flowStatList);
}
