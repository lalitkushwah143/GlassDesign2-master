package com.example.android.glass.glassdesign2.data;

public class DataNoti {

    private String key;
    private String call_id;
    private String callerId;
    private String receiverId;
    private String message;
    private String status;
    private int index;

    public DataNoti() {
    }

    public DataNoti(String key, String call_id, String callerId, String receiverId, String message, String status, int index) {
        this.key = key;
        this.call_id = call_id;
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.message = message;
        this.status = status;
        this.index = index;
    }

    public DataNoti(String call_id, String callerId, String receiverId, String message, String status, int index) {
        this.call_id = call_id;
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.message = message;
        this.status = status;
        this.index = index;
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

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
