package com.example.android.glass.glassdesign2.data;

public class DataCallMedia {

    private String key;
    private String call_id;
    private String url;

    public DataCallMedia() {
    }

    public DataCallMedia(String key, String call_id, String url) {
        this.key = key;
        this.call_id = call_id;
        this.url = url;
    }

    public DataCallMedia(String call_id, String url) {
        this.call_id = call_id;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCall_id() {
        return call_id;
    }

    public void setCall_id(String call_id) {
        this.call_id = call_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
