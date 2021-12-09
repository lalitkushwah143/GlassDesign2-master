package com.example.android.glass.glassdesign2.data;

public class DataSpec1 {

    private String name;
    private String desc;

    public DataSpec1() {
    }

    public DataSpec1(String name, String desc) {
        this.name = name;
        this.desc = desc;
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
