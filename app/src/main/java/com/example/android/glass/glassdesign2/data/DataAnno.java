package com.example.android.glass.glassdesign2.data;

import java.util.ArrayList;

public class DataAnno {

    private String key;
    private String name;
    private String imgUrl;
    private ArrayList<DataAnno1> annotationData = new ArrayList();

    public DataAnno(String key, String name, String imgUrl, ArrayList<DataAnno1> annotationData) {
        this.key = key;
        this.name = name;
        this.imgUrl = imgUrl;
        this.annotationData = annotationData;
    }

    public DataAnno() {
    }

    public DataAnno(String name, String imgUrl, ArrayList<DataAnno1> annotationData) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.annotationData = annotationData;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ArrayList<DataAnno1> getAnnotationData() {
        return annotationData;
    }

    public void setAnnotationData(ArrayList<DataAnno1> annotationData) {
        this.annotationData = annotationData;
    }
}
