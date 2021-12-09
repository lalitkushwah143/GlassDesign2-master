package com.example.android.glass.glassdesign2.data;

public class DataContact {

    private String key;
    private String name;
    private String number;
    private int imageUrl;

    public DataContact() {
    }

    public DataContact(String key, String name, String number, int imageUrl) {
        this.key = key;
        this.name = name;
        this.number = number;
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
