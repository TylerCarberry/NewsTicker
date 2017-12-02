package com.example.newsticker;

import android.content.Context;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NewsModule {

    public NewsModule() {

    }

    @Provides
    @Singleton
    Cache providesOkHttpCache(App application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(OkHttpClient okHttpClient, Context context) {
        return new Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }


    @Provides
    @Singleton
    NewsClient providesFireClient(Retrofit retrofit) {
        return retrofit.create(NewsClient.class);
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }

}