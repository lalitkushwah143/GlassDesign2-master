package com.example.android.glass.glassdesign2.data;

public class DataAnno2 {
    private String type;
    private Float height;
    private Float width;
    private Float x;
    private Float y;

    public DataAnno2() {
    }

    public DataAnno2(String type, Float height, Float width, Float x, Float y) {
        this.type = type;
        this.height = height;
        this.width = width;
        this.x = x;
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
