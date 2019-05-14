package com.bat.cronJobs.model;

import java.io.Serializable;
import java.util.Date;

public class PushInfo implements Serializable {
    private static final long serialVersionUID = 4125096758372084309L;

    private Integer id;

    private Integer user;

    private Integer pushCount;

    private Integer successPersonCount;

    private Integer failPersonCount;

    private Integer successGroupCount;

    private Integer failGroupCount;

    private Date pushDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getPushCount() {
        return pushCount;
    }

    public void setPushCount(Integer pushCount) {
        this.pushCount = pushCount;
    }

    public Integer getSuccessPersonCount() {
        return successPersonCount;
    }

    public void setSuccessPersonCount(Integer successPersonCount) {
        this.successPersonCount = successPersonCount;
    }

    public Integer getFailPersonCount() {
        return failPersonCount;
    }

    public void setFailPersonCount(Integer failPersonCount) {
        this.failPersonCount = failPersonCount;
    }

    public Integer getSuccessGroupCount() {
        return successGroupCount;
    }

    public void setSuccessGroupCount(Integer successGroupCount) {
        this.successGroupCount = successGroupCount;
    }

    public Integer getFailGroupCount() {
        return failGroupCount;
    }

    public void setFailGroupCount(Integer failGroupCount) {
        this.failGroupCount = failGroupCount;
    }

    public Date getPushDate() {
        return pushDate;
    }

    public void setPushDate(Date pushDate) {
        this.pushDate = pushDate;
    }

    @Override
    public String toString() {
        return "PushInfo{" +
                "id=" + id +
                ", user=" + user +
                ", pushCount=" + pushCount +
                ", successPersonCount=" + successPersonCount +
                ", failPersonCount=" + failPersonCount +
                ", successGroupCount=" + successGroupCount +
                ", failGroupCount=" + failGroupCount +
                ", pushDate=" + pushDate +
                '}';
    }
}