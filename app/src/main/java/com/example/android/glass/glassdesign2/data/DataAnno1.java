package com.example.android.glass.glassdesign2.data;


public class DataAnno1 {
    private String id;
    private String comment;
    private String mqttId;
    private DataAnno2 mark;

    public DataAnno1(String id, String comment, String mqttId, DataAnno2 mark) {
        this.id = id;
        this.comment = comment;
        this.mqttId = mqttId;
        this.mark = mark;
    }

    public DataAnno1() {
    }

    public String getId() {
        return id;
    }

    public String getMqttId() {
        return mqttId;
    }

    public void setMqttId(String mqttId) {
        this.mqttId = mqttId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DataAnno2 getMark() {
        return mark;
    }

    public void setMark(DataAnno2 mark) {
        this.mark = mark;
    }
}
