package com.example.android.glass.glassdesign2.data;

public class DataCheck {

    private int index;
    private Boolean status;

    public DataCheck(int index, Boolean status) {
        this.index = index;
        this.status = status;
    }

    public DataCheck() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
