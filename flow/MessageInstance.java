package com.ztesoft.zsmart.perf.flow;

public class MessageInstance {

    private Boolean isSuccess;

    private Long averageTime;

    public MessageInstance() {
        isSuccess = false;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(Long averageTime) {
        this.averageTime = averageTime;
    }

}
