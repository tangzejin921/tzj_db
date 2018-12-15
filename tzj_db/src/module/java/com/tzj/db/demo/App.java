package com.tzj.db.demo;

import android.app.Application;

import com.tzj.db.SQLiteDelegate;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteDelegate.init(this);
    }
}
