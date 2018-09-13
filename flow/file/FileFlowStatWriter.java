package com.ztesoft.zsmart.perf.flow.file;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import com.ztesoft.zsmart.cg.kernel.utils.DateUtil;
import com.ztesoft.zsmart.core.log.ZSmartLogger;
import com.ztesoft.zsmart.perf.flow.FlowStat;

public class FileFlowStatWriter implements FlowStatWriter {

    private static ZSmartLogger logger = ZSmartLogger.getLogger(FileFlowStatWriter.class);

    private static final String FLOW_HEADER = "time,reqeust,response,success,fail,avgDealTime,maxDealTime,avgCacheTime,fluxOverload,dubboTimeout,"
        + "repositoryOverload,repositoryTimeout,remotingTimeout,waitTaskNum\r\n";

    private static final DecimalFormat DCM_FMT = new DecimalFormat("0");

    private final String protocol;

    private final String totalFileNamePrefix;

    private final String busiFileNamePrefix;

    public FileFlowStatWriter(String protocol) {
        this.protocol = protocol;
        String zsmartHome = "";
        this.totalFileNamePrefix = zsmartHome + "flowStat/";
        this.busiFileNamePrefix = zsmartHome + "busiStat/";
    }

    @Override
    public void writeTotalFlowStat(List<FlowStat> flowStatList) {
        for (FlowStat stat : flowStatList) {
            String fileName = getTotalFlowStatFileName(stat);
            writeFlowFile(fileName, stat);
        }
    }

    @Override
    public void writeBusiFlowFile(List<FlowStat> flowStatList) {
        for (FlowStat stat : flowStatList) {
            String fileName = getBusiFlowStatFileName(stat);
            writeFlowFile(fileName, stat);
        }
    }

    private String getTotalFlowStatFileName(FlowStat stat) {
        String dateStr = DateUtil.date2String(stat.getTime().getTime(), "yyyy-MM-dd");
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(totalFileNamePrefix).append(protocol).append("_").append(dateStr).append(".csv");
        return fileNameBuilder.toString();
    }

    private String getBusiFlowStatFileName(FlowStat stat) {
        String dateStr = DateUtil.date2String(stat.getTime().getTime(), "yyyy-MM-dd");
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(busiFileNamePrefix).append(stat.getName()).append("_stat_").append(dateStr).append(".csv");
        return fileNameBuilder.toString();
    }

    private void writeFlowFile(String fileName, FlowStat stat) {
        try {
            File file = new File(fileName);
            boolean addHeader = false;
            if (!file.exists()) {
                addHeader = true;
            }
            DataOutputStream out = null;
            try {
                if (!file.getParentFile().exists()) {
                    boolean ret = file.getParentFile().mkdirs();
                    if (!ret) {
                        logger.error("Failed to create directory for file: [{}]", fileName);
                    }
                }
                out = new DataOutputStream(new FileOutputStream(file, true));
                if (addHeader) {
                    out.writeBytes(FLOW_HEADER);
                }
                out.writeBytes(getFlowStatRecordStr(stat));
                out.flush();
            }
            finally {
                try {
                    if (null != out) {
                        out.close();
                    }
                }
                catch (Exception e) {
                    logger.error("Failed to close the file output stream. ", e);
                }
            }
        }
        catch (FileNotFoundException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
    }

    private String getFlowStatRecordStr(FlowStat stat) {
        StringBuilder recordSb = new StringBuilder();
        recordSb.append(DateUtil.date2String(stat.getTime().getTime(), "HH:mm:ss")).append(',');
        recordSb.append(String.valueOf(stat.getRequest())).append(',');
        recordSb.append(String.valueOf(stat.getResponse())).append(',');
        recordSb.append(String.valueOf(stat.getBusiSucc())).append(',');
        recordSb.append(String.valueOf(stat.getBusiFail())).append(',');
        recordSb.append(String.valueOf(DCM_FMT.format(stat.getAvgDealTime()))).append(',');
        recordSb.append(String.valueOf(stat.getMaxBusiTime())).append(',');
        recordSb.append(String.valueOf(DCM_FMT.format(stat.getAvgCacheTime()))).append(',');
        recordSb.append(String.valueOf(stat.getFluxOverload())).append(',');
        recordSb.append(String.valueOf(stat.getDubboTimeOutNum())).append(',');
        recordSb.append(String.valueOf(stat.getRepositoryOverload())).append(',');
        recordSb.append(String.valueOf(stat.getRepositoryTimeOutNum())).append(',');
        recordSb.append(String.valueOf(stat.getRemotingTimeOutNum())).append(',');
        recordSb.append(String.valueOf(stat.getWaitTaskNum())).append("\r\n");
        return recordSb.toString();
    }
}
