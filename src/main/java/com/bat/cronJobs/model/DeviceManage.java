package com.bat.cronJobs.model;

import java.util.Date;

public class DeviceManage {
    private Long id;

    private Integer user;

    private String phoneName;

    private String phoneSystem;

    private Date lastLoginTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName == null ? null : phoneName.trim();
    }

    public String getPhoneSystem() {
        return phoneSystem;
    }

    public void setPhoneSystem(String phoneSystem) {
        this.phoneSystem = phoneSystem == null ? null : phoneSystem.trim();
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}