package com.example.newsticker;

import com.example.newsticker.model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsClient {

    @GET("v2/top-headlines")
    Call<News> getNews(@Query("apiKey") String apiKey, @Query("sources") String sources);

}