package com.example.android.glass.glassdesign2.data;

public class DataDQNew {

    private String key;
    private String mid;
    private String name;
    private String desc;

    public DataDQNew() {
    }

    public DataDQNew(String key, String mid, String name, String desc) {
        this.key = key;
        this.mid = mid;
        this.name = name;
        this.desc = desc;
    }

    public DataDQNew(String mid, String name, String desc) {
        this.mid = mid;
        this.name = name;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
