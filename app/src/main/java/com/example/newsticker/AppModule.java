package com.example.newsticker;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    App providesApp() {
        return app;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return app;
    }

}