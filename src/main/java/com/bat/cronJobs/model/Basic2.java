package com.bat.cronJobs.model;

public class Basic2 extends Basic {
    private String dateStr;

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    @Override
    public String toString() {
        return "{ dateStr='" + dateStr + '\'' +
                "," + super.toString()+"}";
    }
}
