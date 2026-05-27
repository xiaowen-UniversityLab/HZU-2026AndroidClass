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
import com.example.newslist.model.bean.NewsBean;

import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder> {

    private List<NewsBean.NewsListBean> mNewsList;
    private final LayoutInflater mInflater;
    private final OnNewsCollectClickListener mListener;


    public NewsRecyclerAdapter(List<NewsBean.NewsListBean> newsList, Context context, OnNewsCollectClickListener listener) {
        this.mNewsList = newsList;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    // 首页的收藏点击回调接口
    public interface OnNewsCollectClickListener {
        void onCollectClick(NewsBean.NewsListBean news);
    }

    //设置列表数据
    public void setNewsList(List<NewsBean.NewsListBean> newsList) {
        this.mNewsList = newsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.myrecylcer_layout, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsBean.NewsListBean news = mNewsList.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvSource.setText(news.getSource());
        holder.tvTime.setText(news.getCtime());
        holder.btnCollect.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCollectClick(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSource, tvTime;
        Button btnCollect;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSource = itemView.findViewById(R.id.tv_source);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnCollect = itemView.findViewById(R.id.btn_collect);
        }
    }
}
