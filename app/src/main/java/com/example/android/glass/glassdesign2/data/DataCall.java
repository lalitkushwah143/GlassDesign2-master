package com.example.android.glass.glassdesign2.data;

import com.google.firebase.Timestamp;

public class DataCall {

    private String key;
    private String manual;
    private String step;
    private Timestamp time;


    public DataCall() {
    }

    public DataCall(String manual, String step, Timestamp time) {
        this.manual = manual;
        this.step = step;
        this.time = time;
    }

    public DataCall(String key, String manual, String step, Timestamp time) {
        this.key = key;
        this.manual = manual;
        this.step = step;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
