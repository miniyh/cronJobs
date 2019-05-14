package com.bat.cronJobs.model;


import java.util.Date;

public class LoginInfo extends Basic {
    private Integer id;

    private Integer user;

    private Integer loginCount;

    private Date loginDate;

    private Date lastLoginTime;

    private String version;

    private Integer versionCode;

    private String phoneName;

    private String phoneSystem;

    private Integer state;

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

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getPhoneSystem() {
        return phoneSystem;
    }

    public void setPhoneSystem(String phoneSystem) {
        this.phoneSystem = phoneSystem;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", user=" + user +
                ", loginCount=" + loginCount +
                ", loginDate=" + loginDate +
                ", lastLoginTime=" + lastLoginTime +
                ", version='" + version + '\'' +
                ", versionCode=" + versionCode +
                ", phoneName='" + phoneName + '\'' +
                ", phoneSystem='" + phoneSystem + '\'' +
                ", state=" + state +
                "} " + super.toString();
    }
}