package com.ztesoft.zsmart.perf.flow.file;

import java.util.List;

import com.ztesoft.zsmart.cg.entity.handler.job.JobRunnable;
import com.ztesoft.zsmart.core.log.ZSmartLogger;
import com.ztesoft.zsmart.perf.flow.FlowController;
import com.ztesoft.zsmart.perf.flow.FlowStat;

public class FlowStatWriterJob implements Runnable {

    private static final ZSmartLogger logger = ZSmartLogger.getLogger(FlowStatWriterJob.class);

    private final FlowController fcController;

    private final FlowStatWriter flowStatWriter;

    private int writeIndex = -1;

    public FlowStatWriterJob(FlowController fcController) {
        this.fcController = fcController;
        flowStatWriter = new FileFlowStatWriter("stat");
    }

    @Override
    public void run() {
        FlowStat flowStatLastSecond = fcController.getFlowStatManager().getTotalFlowLastSecond();
        if (writeIndex == -1) {
            writeIndex = flowStatLastSecond.getIndex();
        }
        else {
            int endIndex = flowStatLastSecond.getIndex();
            List<FlowStat> totalflowStatList = fcController.getFlowStatManager().getTotalFlowStatByIndex(writeIndex,
                endIndex);
            flowStatWriter.writeTotalFlowStat(totalflowStatList);
            List<FlowStat> busiflowStatList = fcController.getFlowStatManager().getBusiFlowStatByIndex(writeIndex,
                endIndex);
            flowStatWriter.writeBusiFlowFile(busiflowStatList);
            writeIndex = endIndex;
        }
    }
}
