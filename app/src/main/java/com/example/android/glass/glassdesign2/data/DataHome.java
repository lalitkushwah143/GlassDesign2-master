package com.example.android.glass.glassdesign2.data;

public class DataHome {

    private String title;
    private int image;

    public DataHome(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public DataHome() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
