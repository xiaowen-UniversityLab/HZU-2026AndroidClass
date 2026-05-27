package com.example.newslist;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
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
    private List<NewsBean.NewsListBean> mBeanList = new ArrayList<>();
    private MyRecyclerAdapter mAdapter;
    private NewsViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //初始化RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rlv.setLayoutManager(layoutManager);
        mAdapter = new MyRecyclerAdapter(mBeanList, MainActivity.this);
        binding.rlv.setAdapter(mAdapter);


        //初始化ViewModel
        mViewModel = new ViewModelProvider(this).get(NewsViewModel.class);


        //初始化Retrofit
        mViewModel.InitialRetrofit();


        //观察数据
        mViewModel.getNewsLiveData().observe(this, newsBean -> {
            if (newsBean != null && newsBean.getResult() != null) {
                Toast.makeText(MainActivity.this, "成功获取新闻~", Toast.LENGTH_SHORT).show();
                //更新列表
                mBeanList.clear();
                mBeanList.addAll(newsBean.getResult().getNewslist());
                mAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getNewsState().observe(this, newsState -> {
            if (newsState != null && !newsState && mViewModel.getNewsLiveData().getValue() == null) {
                Toast.makeText(MainActivity.this, "获取新闻失败！", Toast.LENGTH_SHORT).show();

            }
        });


        //刷新、获取新闻
        binding.btnRefresh.setOnClickListener(v -> mViewModel.RefreshNews());


    }


}