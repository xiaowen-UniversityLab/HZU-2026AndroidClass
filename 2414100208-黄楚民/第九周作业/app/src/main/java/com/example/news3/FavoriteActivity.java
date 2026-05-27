package com.example.news3;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.news3.databinding.ActivityFavoriteBinding;

public class FavoriteActivity extends AppCompatActivity {

    private ActivityFavoriteBinding binding;
    private NewsViewModel viewModel;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteAdapter();
        adapter.setOnDeleteClickListener(article -> viewModel.removeFavorite(article));
        binding.rvFavorites.setAdapter(adapter);

        // 观察收藏数据（LiveData 自动刷新）
        viewModel.getFavorites().observe(this, favorites -> {
            adapter.updateData(favorites);
        });
    }
}
