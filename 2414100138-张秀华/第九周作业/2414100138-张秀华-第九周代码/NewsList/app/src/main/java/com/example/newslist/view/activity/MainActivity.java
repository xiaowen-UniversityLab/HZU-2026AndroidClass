package com.example.newslist.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.newslist.R;
import com.example.newslist.databinding.ActivityMainBinding;
import com.example.newslist.model.bean.NewsBean;
import com.example.newslist.view.adapter.NewsRecyclerAdapter;
import com.example.newslist.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsRecyclerAdapter.OnNewsCollectClickListener {

    private ActivityMainBinding binding;
    private List<NewsBean.NewsListBean> mNewsList = new ArrayList<>();
    private NewsRecyclerAdapter mNewsAdapter;
    private NewsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化 RecyclerView
        binding.rlv.setLayoutManager(new LinearLayoutManager(this));
        mNewsAdapter = new NewsRecyclerAdapter(mNewsList, this, this);
        binding.rlv.setAdapter(mNewsAdapter);

        // 初始化 ViewModel
        mViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(NewsViewModel.class);


        // 观察网络请求数据
        mViewModel.getNewsLiveData().observe(this, newsBean -> {
            if (newsBean != null && newsBean.getResult() != null) {
                Toast.makeText(MainActivity.this, "成功获取新闻~", Toast.LENGTH_SHORT).show();
                mNewsList.clear();
                mNewsList.addAll(newsBean.getResult().getNewslist());
                mNewsAdapter.setNewsList(mNewsList);
            }
        });

        //获取新闻按钮
        binding.btnRefresh.setOnClickListener(v -> {
            mViewModel.RefreshNews();
        });

        //跳转收藏页面按钮
        binding.btnJumpCollection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        });


    }

    //按钮点击回调
    @Override
    public void onCollectClick(NewsBean.NewsListBean news) {
        mViewModel.addCollection(news);
        Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
    }

}