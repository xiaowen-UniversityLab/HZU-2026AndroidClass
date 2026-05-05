package com.example.newslist;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newslist.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Retrofit mRetrofit;
    private NewsRetrofit newsRetrofit;
    private List<NewsBean.NewsListBean> mBeanList = new ArrayList<>();
    private MyRecyclerAdapter mAdapter;


    private final String MY_KEY = "f31fac14c585b000592b3548f2cf9c37";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://apis.tianapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newsRetrofit = mRetrofit.create(NewsRetrofit.class);


        binding.btnRefresh.setOnClickListener(v -> RefreshNews());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rlv.setLayoutManager(layoutManager);
        mAdapter = new MyRecyclerAdapter(mBeanList, MainActivity.this);
        binding.rlv.setAdapter(mAdapter);


    }

    private void RefreshNews() {

        Call<NewsBean> call = newsRetrofit.get(MY_KEY, 20);

        call.enqueue(new Callback<NewsBean>() {
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                Toast.makeText(MainActivity.this, "成功获取新闻~", Toast.LENGTH_SHORT).show();

                NewsBean newsBean = response.body();
                mBeanList.clear();
                mBeanList.addAll(newsBean.getResult().getNewslist());
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {
                Toast.makeText(MainActivity.this, "新闻获取失败！", Toast.LENGTH_SHORT).show();

            }
        });


    }

}