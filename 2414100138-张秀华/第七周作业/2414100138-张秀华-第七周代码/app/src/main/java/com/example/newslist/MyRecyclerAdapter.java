package com.example.newslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private List<NewsBean.NewsListBean> mBeanList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;


    public MyRecyclerAdapter(List<NewsBean.NewsListBean> beanList, Context context) {
        mBeanList = beanList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.myrecylcer_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.MyViewHolder holder, int position) {

        NewsBean.NewsListBean bean = mBeanList.get(position);

        holder.mTvTitle.setText(bean.getTitle());
        holder.mTvDescription.setText("具体详情请前往相应平台查看");
        holder.mTvSource.setText(bean.getSource());
        holder.mTvTime.setText(bean.getCtime());

    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTitle;
        TextView mTvDescription;
        ImageView mIvImage;
        TextView mTvSource;
        TextView mTvTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvTitle = itemView.findViewById(R.id.tv_title);
            this.mTvDescription = itemView.findViewById(R.id.tv_description);
            this.mTvSource = itemView.findViewById(R.id.tv_source);
            this.mTvTime = itemView.findViewById(R.id.tv_time);

        }
    }
}
