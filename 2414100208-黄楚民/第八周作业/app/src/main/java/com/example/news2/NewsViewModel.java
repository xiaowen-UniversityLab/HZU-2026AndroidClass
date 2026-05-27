package com.example.news2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsViewModel extends ViewModel {

    private final MutableLiveData<List<News.Article>> newsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final NewsApi newsApi;
    private boolean hasRequestedNews = false;
    private boolean isLoading = false;

    private static final String API_KEY = "d28aec02329d46150613b932b660dd9b";

    public NewsViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v.juhe.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsApi = retrofit.create(NewsApi.class);
    }

    public LiveData<List<News.Article>> getNewsLiveData() {
        return newsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void loadNewsIfNeeded() {
        if (hasRequestedNews || newsLiveData.getValue() != null || isLoading) {
            return;
        }
        loadNews();
    }

    public void refreshNews() {
        hasRequestedNews = false;
        loadNews();
    }

    private void loadNews() {
        if (isLoading) {
            return;
        }

        hasRequestedNews = true;
        isLoading = true;
        Log.d("NewsViewModel", "request news from network");

        newsApi.getTopNews("top", API_KEY).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null
                        && response.body().result != null
                        && response.body().result.data != null) {
                    newsLiveData.postValue(response.body().result.data);
                } else {
                    errorLiveData.postValue("数据解析失败");
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                isLoading = false;
                errorLiveData.postValue("网络请求失败: " + t.getMessage());
            }
        });
    }
}
