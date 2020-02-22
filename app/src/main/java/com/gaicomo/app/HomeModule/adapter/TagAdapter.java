package com.gaicomo.app.HomeModule.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gaicomo.app.HomeModule.activity.SearchActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.model.tagPojo.Data;
import com.gaicomo.app.views.AppTextView;

import java.io.Serializable;
import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder> implements Serializable {

    private static final String TAG = "LiveAdapter";
    Context mContext;
    Fragment fragment;
    String type;
    private ArrayList<Data> dataSet;

    public TagAdapter( ArrayList<Data> dataSet, String type) {
        this.dataSet = dataSet;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_tag_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        mContext = parent.getContext();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        holder.tvTag.setText(dataSet.get(listPosition).getTag());
        holder.tvTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchActivity)mContext).selectedTag(dataSet.get(listPosition).getId(), dataSet.get(listPosition).getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AppTextView tvTag;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tv_tag);
        }
    }


}