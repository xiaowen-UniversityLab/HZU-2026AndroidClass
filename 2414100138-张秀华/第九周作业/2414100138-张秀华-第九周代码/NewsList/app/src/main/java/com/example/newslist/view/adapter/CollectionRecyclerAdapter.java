package com.example.newslist.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newslist.R;
import com.example.newslist.model.bean.CollectNews;

import java.util.List;

public class CollectionRecyclerAdapter extends RecyclerView.Adapter<CollectionRecyclerAdapter.CollectionViewHolder> {

    private List<CollectNews> mCollectList;
    private final LayoutInflater mInflater;
    private final OnCancelCollectClickListener mListener;

    public CollectionRecyclerAdapter(List<CollectNews> collectList, Context context, OnCancelCollectClickListener listener) {
        this.mCollectList = collectList;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }


    // 收藏页的取消收藏点击回调接口
    public interface OnCancelCollectClickListener {
        void onCancelClick(CollectNews news);
    }

    //设置列表数据
    public void setCollectList(List<CollectNews> collectList) {
        this.mCollectList = collectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.myrecylcer_layout, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        CollectNews news = mCollectList.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvSource.setText(news.getSource());
        holder.tvTime.setText(news.getCtime());
        holder.btnCollect.setText("取消收藏");
        holder.btnCollect.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCancelClick(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCollectList.size();
    }

    public static class CollectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSource, tvTime;
        Button btnCollect;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSource = itemView.findViewById(R.id.tv_source);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnCollect = itemView.findViewById(R.id.btn_collect);
        }
    }
}
