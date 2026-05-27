package com.example.newslist.model.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.newslist.model.api.NewsRetrofit;
import com.example.newslist.model.bean.NewsBean;
import com.example.newslist.model.database.NewsDao;
import com.example.newslist.model.database.NewsDatabase;
import com.example.newslist.model.bean.CollectNews;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsRepository {
    private NewsDao newsDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private NewsRetrofit newsRetrofit;


    //初始化Room数据库
    public NewsRepository(Application application) {
        NewsDatabase db = NewsDatabase.getInstance(application);
        newsDao = db.newsDao();
    }


    //初始化网络请求工具
    public NewsRepository() {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://apis.tianapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsRetrofit = mRetrofit.create(NewsRetrofit.class);
    }

    //发起网络请求
    public void getNews(String key, int num, final NewsCallback callback) {
        Call<NewsBean> call = newsRetrofit.get(key, num);
        call.enqueue(new Callback<NewsBean>() {
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("请求失败");
                }
            }

            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface NewsCallback {
        void onSuccess(NewsBean newsBean);
        void onError(String errorMsg);
    }


    //操作数据库

    // 插入
    public void insertCollection(CollectNews news) {
        executor.execute(() -> newsDao.insertNews(news));
    }

    // 删除
    public void deleteCollection(CollectNews news) {
        executor.execute(() -> newsDao.deleteNews(news));
    }

    // 查询所有
    public LiveData<List<CollectNews>> getAllCollectionNews() {
        return newsDao.getAllCollectionNews();
    }

}
