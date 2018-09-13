package com.ztesoft.zsmart.perf.flow;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.ztesoft.zsmart.cg.kernel.utils.DateUtil;
import com.ztesoft.zsmart.core.util.StringUtil;

public class FlowStat {

    private int index;

    /**
     * Used for client, flow count before send the message in real.
     */
    private AtomicInteger preRequest = new AtomicInteger(0);

    /**
     * Flow count for real message receiving and sending.
     */
    private AtomicInteger request = new AtomicInteger(0);

    private AtomicInteger response = new AtomicInteger(0);

    private AtomicInteger busiSucc = new AtomicInteger(0);

    private AtomicInteger busiFail = new AtomicInteger(0);

    private AtomicInteger succWithError = new AtomicInteger(0);

    private AtomicInteger totalFluxOverload = new AtomicInteger(0);

    private AtomicInteger busiFluxOverload = new AtomicInteger(0);

    private AtomicInteger dubboFluxOverload = new AtomicInteger(0);

    private AtomicInteger repositoryOverload = new AtomicInteger(0);

    private AtomicInteger repositoryTimeOutNum = new AtomicInteger(0);

    private AtomicInteger dubboTimeOutNum = new AtomicInteger(0);

    private AtomicInteger dubboExceptionNum = new AtomicInteger(0);

    private AtomicInteger remotingTimeOutNum = new AtomicInteger(0);

    private AtomicInteger cacheOverloadNum = new AtomicInteger(0);

    private float avgDealTime;

    private long maxBusiTime;

    private AtomicLong totalDealTime = new AtomicLong(0);

    private long avgCacheTime;

    private AtomicLong totalCacheTime = new AtomicLong(0);

    private Object synObj = new Object();

    private long waitTaskNum;

    private String name;

    private Calendar now;

    public FlowStat(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        synchronized (synObj) {
            preRequest.set(0);
            request.set(0);
            response.set(0);
            busiSucc.set(0);
            busiFail.set(0);
            succWithError.set(0);
            totalFluxOverload.set(0);
            busiFluxOverload.set(0);
            dubboFluxOverload.set(0);
            repositoryOverload.set(0);
            repositoryTimeOutNum.set(0);
            dubboTimeOutNum.set(0);
            dubboExceptionNum.set(0);
            remotingTimeOutNum.set(0);
            cacheOverloadNum.set(0);
            avgDealTime = 0f;
            maxBusiTime = 0;
            avgCacheTime = 0;
            totalCacheTime.set(0);
            totalDealTime.set(0);
            waitTaskNum = 0;
            setTime();
        }
    }

    public int refreshPreRequest(MessageInstance messageInstance) {
        return preRequest.addAndGet(1);
    }

    public int refreshRequest(MessageInstance messageInstance) {
        return request.addAndGet(1);
    }

    public void refreshResponse(MessageInstance messageInstance) {
        refreshTimesCnt(messageInstance);
        refreshTimespan(messageInstance);
    }

    private void refreshTimespan(MessageInstance messageInstance) {
        totalDealTime.addAndGet(messageInstance.getAverageTime());
    }

    private void refreshTimesCnt(MessageInstance messageInstance) {
        this.response.addAndGet(1);
        if (messageInstance.getIsSuccess()) {
            this.busiSucc.addAndGet(1);
        }
        else {
            this.busiFail.addAndGet(1);
        }
    }

    public int getPreRequest() {
        return preRequest.get();
    }

    public int getRequest() {
        return request.get();
    }

    public int getResponse() {
        return response.get();
    }

    public int getIndex() {
        return this.index;
    }

    public int getBusiSucc() {
        return busiSucc.get();
    }

    public int getBusiFail() {
        return busiFail.get();
    }

    public float getAvgDealTime() {
        int resp = this.response.get();
        if (resp > 0) {
            avgDealTime = (float) this.totalDealTime.get() / resp;
        }
        else {
            avgDealTime = 0;
        }
        return avgDealTime;
    }

    public float getAvgCacheTime() {
        int resp = this.response.get();
        if (resp > 0) {
            avgCacheTime = this.totalCacheTime.get() / resp;
        }
        else {
            avgCacheTime = 0;
        }
        return avgCacheTime;
    }

    public long getMaxBusiTime() {
        return maxBusiTime;
    }

    public long getFluxOverload() {
        AtomicLong ret = new AtomicLong(this.totalFluxOverload.get());
        ret.addAndGet(this.busiFluxOverload.get());
        ret.addAndGet(this.dubboFluxOverload.get());
        return ret.get();
    }

    public int getRepositoryOverload() {
        return repositoryOverload.get();
    }

    public int getRepositoryTimeOutNum() {
        return repositoryTimeOutNum.get();
    }

    public int getDubboTimeOutNum() {
        return dubboTimeOutNum.get();
    }

    public int getDubboExceptionNum() {
        return dubboExceptionNum.get();
    }

    public int getRemotingTimeOutNum() {
        return remotingTimeOutNum.get();
    }

    public int getCacheOverloadNum() {
        return cacheOverloadNum.get();
    }

    public long getWaitTaskNum() {
        return waitTaskNum;
    }

    private void setTime() {
        now = Calendar.getInstance();
        int second = index % 60;
        now.set(Calendar.SECOND, second);
        now.set(Calendar.MILLISECOND, 0);
    }

    public Calendar getTime() {
        synchronized (synObj) {
            if (null == now) {
                reset();
            }
            return now;
        }
    }

    @Override
    public String toString() {
        String timeStamp = DateUtil.date2String(this.getTime().getTime(), "HH:mm:ss");
        return StringUtil.format("timeStamp=[{}],index=[{}],request=[{}],response=[{}]", timeStamp, index, String.valueOf(request), response);
    }
}
