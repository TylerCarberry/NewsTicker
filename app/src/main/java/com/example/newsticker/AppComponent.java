package com.example.newsticker;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, NewsModule.class})
public interface AppComponent {
    void inject(App app);
    void inject(MainActivity mainActivity);
}