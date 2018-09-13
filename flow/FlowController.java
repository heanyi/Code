package com.ztesoft.zsmart.perf.flow;

import org.springframework.stereotype.Component;

import com.ztesoft.zsmart.cg.entity.message.MessageCommandIntrp;
// import com.ztesoft.zsmart.core.log.ZSmartLogger;

@Component
public class FlowController {

    private FsMapManager flowStatManager;

    public FlowController() {
        flowStatManager = new FsMapManager();
    }
    
    public FsMapManager getFlowStatManager() {
        return flowStatManager;
    }

    public void refreshResponse(MessageInstance instance) {
        refreshTotalResponse(instance);
        refreshBusiResponse(instance);
    }

    private void refreshTotalResponse(MessageInstance instance) {
        FlowStat flowStat = flowStatManager.getCurrTotalFlow();
        flowStat.refreshResponse(instance);
    }

    private void refreshBusiResponse(MessageInstance instance) {
        String busiName = getBusiName(instance);
        if (busiName == null) {
            return;
        }
        FlowStat flowStat = flowStatManager.getCurrBusiFlow(busiName);
        flowStat.refreshResponse(instance);
    }

    public void refreshRequest(MessageInstance instance) {
        refreshTotalRequest(instance);
        refreshBusiRequest(instance);
    }

    private void refreshTotalRequest(MessageInstance instance) {
        FlowStat flowStat = flowStatManager.getCurrTotalFlow();
        flowStat.refreshRequest(instance);
    }

    private void refreshBusiRequest(MessageInstance instance) {
        String busiName = getBusiName(instance);
        if (busiName == null) {
            return;
        }
        FlowStat flowStat = flowStatManager.getCurrBusiFlow(busiName);
        flowStat.refreshRequest(instance);
    }

    private String getBusiName(MessageInstance messageInstance) {
//        if (messageInstance.getMessageCommand() == null) {
//            return null;
//        }
//        MessageCommandIntrp command = messageInstance.getMessageCommand();
//        return command.getName();
        return "http";
    }
}
