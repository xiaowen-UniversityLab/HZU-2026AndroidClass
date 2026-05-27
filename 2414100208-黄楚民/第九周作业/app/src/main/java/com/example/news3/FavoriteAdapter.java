package com.example.news3;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.news3.databinding.ItemFavoriteBinding;
import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private List<FavoriteArticle> data = new ArrayList<>();
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(FavoriteArticle article);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<FavoriteArticle> newData) {
        this.data.clear();
        if (newData != null) {
            this.data.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteArticle article = data.get(position);
        holder.binding.tvFavTitle.setText(article.title);
        holder.binding.tvFavAuthor.setText(article.author);
        holder.binding.tvFavDate.setText(article.date != null ? article.date : "");
        holder.binding.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemFavoriteBinding binding;
        ViewHolder(ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
