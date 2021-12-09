package com.example.android.glass.glassdesign2;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class GlassDesign2 extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
