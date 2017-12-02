package com.example.newsticker;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {

    private static App INSTANCE;

    private com.example.newsticker.AppComponent appComponent;

    @Override
    public void onCreate() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new com.example.newsticker.AppModule(this))
                .build();
        appComponent.inject(this);

        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        INSTANCE = this;
    }

    public com.example.newsticker.AppComponent getAppComponent() {
        return appComponent;
    }

    public static App get() {
        return INSTANCE;
    }

}