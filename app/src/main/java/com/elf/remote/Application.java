package com.elf.remote;

import android.annotation.SuppressLint;
import android.content.Context;

public class Application extends android.app.Application {
    @SuppressLint("StaticFieldLeak")
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Application.instance = getApplicationContext();
    }

    public static Context applicationContext() {
        return Application.instance;
    }
}
