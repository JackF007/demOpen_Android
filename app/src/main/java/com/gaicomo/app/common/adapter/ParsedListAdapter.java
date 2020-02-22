package com.gaicomo.app.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.common.fragment.WebFragment;
import com.gaicomo.app.common.model.parsedList.ParsedData;
import com.gaicomo.app.views.AppTextView;

import java.io.Serializable;
import java.util.ArrayList;

public class ParsedListAdapter extends RecyclerView.Adapter<ParsedListAdapter.MyViewHolder> implements Serializable {

    private ArrayList<ParsedData> dataSet;
    Context mContext;
    Fragment fragment;

    private static final String TAG = "LiveAdapter";
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AppTextView tvTitle,tvDescription;
        LinearLayout llMain;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle= itemView.findViewById(R.id.tv_title);
            tvDescription= itemView.findViewById(R.id.tv_description);
            llMain=itemView.findViewById(R.id.ll_main);
        }

        public void clearAnimation()
        {
            llMain.clearAnimation();
        }
    }

    public ParsedListAdapter(Fragment fragment, ArrayList<ParsedData> dataSet) {
        this.fragment = fragment;
        this.dataSet = dataSet;
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        ((MyViewHolder)holder).clearAnimation();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_parsed_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        mContext = parent.getContext();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {


        holder.tvTitle.setText(dataSet.get(listPosition).getTitle().replace("\n","").trim());
        holder.tvDescription.setText(dataSet.get(listPosition).getDescription().replace("\n","").trim());

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeActivity)mContext).replaceFragment(WebFragment.newInstance(dataSet.get(listPosition).getTitle(),dataSet.get(listPosition).getLink()));
            }
        });
        setAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private void setAnimation(View viewToAnimate)
    {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }
}