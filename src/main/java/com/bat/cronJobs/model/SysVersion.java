package com.bat.cronJobs.model;

public class SysVersion {
    private Integer id;

    private String version;

    private Integer versionCode;

    private Integer isAn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getIsAn() {
        return isAn;
    }

    public void setIsAn(Integer isAn) {
        this.isAn = isAn;
    }
}