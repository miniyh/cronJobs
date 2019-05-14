package com.bat.cronJobs.model;

import java.util.Date;

public class LoginDetailDate {
    private Integer user;

    private Date loginTime;

    private Integer state;

    private String version;

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "LoginDetailDate{" +
                "user=" + user +
                ", loginTime=" + loginTime +
                ", state=" + state +
                ", version='" + version + '\'' +
                '}';
    }
}