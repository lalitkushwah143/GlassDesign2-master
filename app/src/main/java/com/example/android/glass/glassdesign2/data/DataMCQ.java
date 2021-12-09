package com.example.android.glass.glassdesign2.data;

import java.util.ArrayList;

public class DataMCQ {

    private String key;
    private int index;
    private String question;
    private ArrayList<String> ans;
    private int ansID;

    public DataMCQ(String key, int index, String question, ArrayList<String> ans, int ansID) {
        this.key = key;
        this.index = index;
        this.question = question;
        this.ans = ans;
        this.ansID = ansID;
    }

    public DataMCQ() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAns() {
        return ans;
    }

    public void setAns(ArrayList<String> ans) {
        this.ans = ans;
    }

    public int getAnsID() {
        return ansID;
    }

    public void setAnsID(int ansID) {
        this.ansID = ansID;
    }
}
