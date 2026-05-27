package com.example.news3;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<News.Article>> newsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final NewsApi newsApi;
    private final FavoriteDao favoriteDao;

    private static final String API_KEY = "d28aec02329d46150613b932b660dd9b";

    public NewsViewModel(Application application) {
        super(application);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v.juhe.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsApi = retrofit.create(NewsApi.class);
        favoriteDao = AppDatabase.getInstance(application).favoriteDao();
    }

    public LiveData<List<News.Article>> getNewsLiveData() {
        return newsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<List<FavoriteArticle>> getFavorites() {
        return favoriteDao.getAll();
    }

    public void loadNews() {
        newsApi.getTopNews("top", API_KEY).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
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
                errorLiveData.postValue("网络请求失败: " + t.getMessage());
            }
        });
    }

    public void toggleFavorite(News.Article article) {
        Executors.newSingleThreadExecutor().execute(() -> {
            FavoriteArticle existing = favoriteDao.findByTitle(article.title);
            if (existing != null) {
                favoriteDao.delete(existing);
            } else {
                FavoriteArticle fav = new FavoriteArticle(
                        article.title,
                        article.author_name,
                        article.date,
                        System.currentTimeMillis()
                );
                favoriteDao.insert(fav);
            }
        });
    }

    public void removeFavorite(FavoriteArticle article) {
        Executors.newSingleThreadExecutor().execute(() -> favoriteDao.delete(article));
    }
}
