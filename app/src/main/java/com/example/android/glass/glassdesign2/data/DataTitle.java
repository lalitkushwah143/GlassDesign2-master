package com.example.android.glass.glassdesign2.data;

public class DataTitle {

    private String key;
    private String title;
    private int index;

    public DataTitle(String key, String title, int index) {
        this.key = key;
        this.title = title;
        this.index = index;
    }

    public DataTitle(String title, int index) {
        this.title = title;
        this.index = index;
    }

    public DataTitle() {
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
