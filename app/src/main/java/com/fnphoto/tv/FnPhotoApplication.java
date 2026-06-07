package com.fnphoto.tv;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

public class FnPhotoApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return appContext;
    }
}
