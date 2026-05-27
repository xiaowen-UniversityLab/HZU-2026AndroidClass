package com.example.newslist.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newslist.model.api.NewsRetrofit;
import com.example.newslist.model.bean.CollectNews;
import com.example.newslist.model.bean.NewsBean;
import com.example.newslist.model.repository.NewsRepository;

import java.io.Closeable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsViewModel extends AndroidViewModel {

    private final String MY_KEY = "f31fac14c585b000592b3548f2cf9c37";
    private final MutableLiveData<NewsBean> mNewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mNewsState = new MutableLiveData<>();
    private LiveData<List<CollectNews>> collectionNews;
    private NewsRepository repository;
    private NewsRepository repository_Retrofit = new NewsRepository();


    //初始化收藏用的repository
    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(application);
    }


    // 获取网络请求的新闻列表数据
    public LiveData<NewsBean> getNewsLiveData() {
        return mNewsLiveData;
    }


    //获取本地数据库的数据
    public LiveData<List<CollectNews>> getCollectionNews() {
        collectionNews = repository.getAllCollectionNews();
        return collectionNews;
    }


    //请求网络，刷新出新闻
    public void RefreshNews() {

        mNewsState.setValue(true);
        repository_Retrofit.getNews(MY_KEY, 20, new NewsRepository.NewsCallback() {
            @Override
            public void onSuccess(NewsBean newsBean) {
                mNewsLiveData.setValue(newsBean);
                mNewsState.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                mNewsState.setValue(false);
            }
        });

    }


    // 数据库相关操作
    public void addCollection(NewsBean.NewsListBean news) {
        CollectNews collect = new CollectNews(news.getCtime(), news.getTitle(), news.getSource());
        repository.insertCollection(collect);
    }

    public void removeCollection(CollectNews news) {
        repository.deleteCollection(news);
    }


}
