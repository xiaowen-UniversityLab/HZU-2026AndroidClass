package com.example.anew;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("toutiao/index")
    Call<News> getTopNews(
            @Query("type") String type,
            @Query("key") String key
    );
}
