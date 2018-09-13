package com.ztesoft.zsmart.perf.flow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ztesoft.zsmart.perf.flow.file.FlowStatWriterJob;

public class FluxGateway {

//    private static final ZSmartLogger logger = ZSmartLogger.getLogger(FluxGateway.class);

    private static FlowController flowCtrl;
    
    private String protocol;

    public static FluxGateway getInst(){
        return new FluxGateway();
    }
    
    public FluxGateway (){
        flowCtrl = new FlowController();
        System.out.println("11");
    }
    
    public void messageReceived(MessageInstance messageInstance) {
        flowCtrl.refreshResponse(messageInstance);
    }

    public void messageSend(MessageInstance messageInstance) {
        flowCtrl.refreshRequest(messageInstance);;
    }

    public void runJobs() {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(new FlowStatWriterJob(flowCtrl), 0, 1, TimeUnit.SECONDS);
//        ChannelJob job = new ChannelJob("flowStat-write", JobThreadName.FLOWSTAT_WRITE, new FlowStatWriterJob(flowCtrl),
//            new ConstJobInterval(1000));
//        job.executeJob();
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
}
