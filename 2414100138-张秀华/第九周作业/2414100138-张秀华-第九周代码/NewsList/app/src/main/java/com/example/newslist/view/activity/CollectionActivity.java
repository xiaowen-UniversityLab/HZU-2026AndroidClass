package com.example.newslist.view.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.newslist.databinding.ActivityCollectionBinding;
import com.example.newslist.model.bean.CollectNews;
import com.example.newslist.view.adapter.CollectionRecyclerAdapter;
import com.example.newslist.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity implements CollectionRecyclerAdapter.OnCancelCollectClickListener {

    private ActivityCollectionBinding binding;
    private List<CollectNews> mCollectList = new ArrayList<>();
    private CollectionRecyclerAdapter mCollectAdapter;
    private NewsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 初始化 RecyclerView
        binding.rlvCollection.setLayoutManager(new LinearLayoutManager(this));
        mCollectAdapter = new CollectionRecyclerAdapter(mCollectList, this, this);
        binding.rlvCollection.setAdapter(mCollectAdapter);

        // 初始化 ViewModel
        mViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(NewsViewModel.class);


        // 观察数据库收藏列表变化
        mViewModel.getCollectionNews().observe(this, collectNewsList -> {
            if (collectNewsList != null) {
                mCollectList.clear();
                mCollectList.addAll(collectNewsList);
                mCollectAdapter.setCollectList(mCollectList);
            }
        });
    }

    //按钮点击回调
    @Override
    public void onCancelClick(CollectNews news) {
        mViewModel.removeCollection(news);
        Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
    }
}