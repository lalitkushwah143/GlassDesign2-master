package com.example.android.glass.glassdesign2.data;

public class DataModule2 {

    private String key;
    private String title;
    private String desc;
    private String mid;
    private String date;

    public DataModule2() {
    }

    public DataModule2(String key, String title, String desc, String mid, String date) {
        this.key = key;
        this.title = title;
        this.desc = desc;
        this.mid = mid;
        this.date = date;
    }

    public DataModule2(String title, String desc, String mid, String date) {
        this.title = title;
        this.desc = desc;
        this.mid = mid;
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
