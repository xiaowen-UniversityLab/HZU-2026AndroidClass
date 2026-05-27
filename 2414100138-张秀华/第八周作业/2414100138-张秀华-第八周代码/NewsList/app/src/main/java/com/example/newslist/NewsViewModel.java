package com.example.newslist;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsViewModel extends ViewModel {

    // 新闻列表数据
    private final MutableLiveData<NewsBean> mNewsLiveData = new MutableLiveData<>();

    public LiveData<NewsBean> getNewsLiveData() {
        return mNewsLiveData;
    }

    //显示toast
    private final MutableLiveData<Boolean> mNewsState = new MutableLiveData<>();

    public LiveData<Boolean> getNewsState() {
        return mNewsState;
    }

    private NewsRetrofit newsRetrofit;
    private final String MY_KEY = "f31fac14c585b000592b3548f2cf9c37";


    //初始化Retrofit
    public void InitialRetrofit() {

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://apis.tianapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsRetrofit = mRetrofit.create(NewsRetrofit.class);
    }

    //请求网络，刷新出新闻
    public void RefreshNews() {

        Call<NewsBean> call = newsRetrofit.get(MY_KEY, 20);

        call.enqueue(new Callback<NewsBean>() {
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                mNewsState.setValue(true);
                if (response.isSuccessful() && response.body() != null) {
                    //把获取到的列表数据设置到 LiveData
                    mNewsLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {
                mNewsState.setValue(false);
            }
        });


    }


}
