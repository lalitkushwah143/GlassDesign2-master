package com.example.android.glass.glassdesign2.data;

public class DataTemp {

    private String email;
    private String pass;

    public DataTemp() {
    }

    public DataTemp(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
