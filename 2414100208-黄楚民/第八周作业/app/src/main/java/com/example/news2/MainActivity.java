package com.example.news2;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.news2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NewsViewModel viewModel;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        // 初始化 RecyclerView
        binding.rvNews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter();
        binding.rvNews.setAdapter(adapter);

        // 观察 LiveData
        viewModel.getNewsLiveData().observe(this, articles -> {
            adapter.updateData(articles);
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        // 刷新按钮
        binding.btnRefresh.setOnClickListener(v -> viewModel.refreshNews());

        // 首次进入页面自动加载；旋转屏幕后 ViewModel 保留，不会重复请求。
        viewModel.loadNewsIfNeeded();
    }
}
