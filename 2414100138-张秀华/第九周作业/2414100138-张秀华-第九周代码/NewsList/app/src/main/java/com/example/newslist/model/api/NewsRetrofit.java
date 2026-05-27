package com.example.newslist.model.api;

import com.example.newslist.model.bean.NewsBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsRetrofit {

    @GET("social/index")
    Call<NewsBean> get(@Query("key") String myKey, @Query("num") int num);
}
