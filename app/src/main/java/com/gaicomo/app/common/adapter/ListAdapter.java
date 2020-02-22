package com.gaicomo.app.common.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.common.fragment.ListFragment;
import com.gaicomo.app.common.fragment.ParsedListFragment;
import com.gaicomo.app.common.fragment.SubListFragment;
import com.gaicomo.app.common.model.listpojo.ListData;
import com.gaicomo.app.views.AppTextView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> implements Serializable {
    boolean animation=true;
    private ArrayList<ListData> dataSet;
    Context mContext;
    Fragment fragment;

    private static final String TAG = "LiveAdapter";
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        AppTextView tvName;
        LinearLayout llMain;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivIcon =  itemView.findViewById(R.id.iv_icon);
            tvName= itemView.findViewById(R.id.tv_name);
            llMain=itemView.findViewById(R.id.ll_main);
        }

//        public void clearAnimation()
//        {
//            llMain.clearAnimation();
//        }
    }

    public ListAdapter(Fragment fragment, ArrayList<ListData> dataSet) {
        this.fragment = fragment;
        this.dataSet = dataSet;
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
//        ((MyViewHolder)holder).clearAnimation();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_category_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        mContext = parent.getContext();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        if(!TextUtils.isEmpty(dataSet.get(listPosition).getLogo())){
            Ion.with(mContext)
                    .load(dataSet.get(listPosition).getLogo())
                    .asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if(e==null)
                        holder.ivIcon.setImageBitmap(result);
                    else
                        holder.ivIcon.setImageResource(0);
                }
            });
        }else{
            holder.ivIcon.setImageResource(0);
        }

        holder.tvName.setText(dataSet.get(listPosition).getCategoryName());

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment instanceof ListFragment) {
                    if (dataSet.get(listPosition).getHaveChild().equals("0"))
                        ((HomeActivity) mContext).replaceFragment(ParsedListFragment.newInstance(dataSet.get(listPosition).getCategoryName(), dataSet.get(listPosition).getId(),dataSet.get(listPosition).getRootId()));
                    else
                        ((HomeActivity) mContext).replaceFragment(SubListFragment.newInstance(dataSet.get(listPosition).getCategoryName(), dataSet.get(listPosition).getRootId(),dataSet.get(listPosition).getId()));
                }else if(fragment instanceof SubListFragment){
                    ((HomeActivity) mContext).replaceFragment(ParsedListFragment.newInstance(dataSet.get(listPosition).getCategoryName(), dataSet.get(listPosition).getId(),dataSet.get(listPosition).getRootId()));
                }
            }
        });


//        setAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

//    private void setAnimation(View viewToAnimate)
//    {
//        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
//        viewToAnimate.startAnimation(animation);
//    }


}