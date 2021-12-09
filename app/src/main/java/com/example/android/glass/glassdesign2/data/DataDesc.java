package com.example.android.glass.glassdesign2.data;

public class DataDesc {

    private String key;
    private String title;
    private String desc;

    public DataDesc(String key, String title, String desc) {
        this.key = key;
        this.title = title;
        this.desc = desc;
    }

    public DataDesc() {
    }

    public DataDesc(String title, String desc) {
        this.title = title;
        this.desc = desc;
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
}
