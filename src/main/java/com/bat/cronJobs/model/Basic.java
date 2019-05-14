package com.bat.cronJobs.model;

import java.util.Date;

public class Basic {

    private String version;

    private Integer count;

    private Date startTime;

    private Date endTime;

    private Integer type;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "version='" + version + '\'' +
                ", count=" + count +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", type=" + type ;
    }
}
