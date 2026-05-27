package com.example.anew;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.anew.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NewsApi newsApi;
    private NewsAdapter adapter;
    private List<News.Article> articleList = new ArrayList<>();

    private static final String API_KEY = "d28aec02329d46150613b932b660dd9b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化 Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v.juhe.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsApi = retrofit.create(NewsApi.class);

        // 初始化 RecyclerView
        binding.rvNews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(articleList);
        binding.rvNews.setAdapter(adapter);

        // 刷新按钮
        binding.btnRefresh.setOnClickListener(v -> loadNews());

        // 首次加载
        loadNews();
    }

    private void loadNews() {
        newsApi.getTopNews("top", API_KEY).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().result != null
                        && response.body().result.data != null) {
                    articleList.clear();
                    articleList.addAll(response.body().result.data);
                    adapter.updateData(articleList);
                    Toast.makeText(MainActivity.this, "新闻加载成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(MainActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
