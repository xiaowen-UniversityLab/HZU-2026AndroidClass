package com.example.news3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.news3.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NewsViewModel viewModel;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        // 初始化 RecyclerView
        binding.rvNews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter();
        adapter.setOnFavoriteClickListener(article -> {
            viewModel.toggleFavorite(article);
            Toast.makeText(this, "已操作收藏", Toast.LENGTH_SHORT).show();
        });
        binding.rvNews.setAdapter(adapter);

        // 观察新闻数据
        viewModel.getNewsLiveData().observe(this, articles -> {
            adapter.updateData(articles);
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        // 刷新按钮
        binding.btnRefresh.setOnClickListener(v -> viewModel.loadNews());

        // 收藏夹按钮
        binding.btnFavorites.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoriteActivity.class));
        });

        // 首次加载
        if (viewModel.getNewsLiveData().getValue() == null) {
            viewModel.loadNews();
        }
    }
}
