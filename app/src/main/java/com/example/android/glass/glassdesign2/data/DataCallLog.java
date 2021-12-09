package com.example.android.glass.glassdesign2.data;

import com.google.firebase.Timestamp;

public class DataCallLog {

    private String key;
    private String user_id;
    private String machine_id;
    private String manual;
    private String step;
    private Timestamp time;
    private String manual_name;

    public DataCallLog() {
    }

    public DataCallLog(String user_id, String machine_id, String manual, String step, Timestamp time, String manual_name) {
        this.user_id = user_id;
        this.machine_id = machine_id;
        this.manual = manual;
        this.step = step;
        this.time = time;
        this.manual_name = manual_name;
    }

    public DataCallLog(String key, String user_id, String machine_id, String manual, String step, Timestamp time, String manual_name) {
        this.key = key;
        this.user_id = user_id;
        this.machine_id = machine_id;
        this.manual = manual;
        this.step = step;
        this.time = time;
        this.manual_name = manual_name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(String machine_id) {
        this.machine_id = machine_id;
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

    public String getManual_name() {
        return manual_name;
    }

    public void setManual_name(String manual_name) {
        this.manual_name = manual_name;
    }
}
