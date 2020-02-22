package com.gaicomo.app.HomeModule.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaicomo.app.AppController;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.fragment.ProfileFragment;
import com.gaicomo.app.Tweet.fragment.TweetDetailFragment;
import com.gaicomo.app.Tweet.model.likeUnlikePojo.LikeUnlikePojo;
import com.gaicomo.app.Tweet.model.tweetTagSearch.Post;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.utils.TagAdapter;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;
import com.nex3z.flowlayout.FlowLayout;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagTweetAdapter extends RecyclerView.Adapter<TagTweetAdapter.MyViewHolder> implements Serializable {

    private ArrayList<Post> dataSet;
    Context mContext;
    Fragment fragment;

    private static final String TAG = "LiveAdapter";

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage,ivLike,ivShare,ivPostImage;
        LinearLayout llMain,llLike,llComment,ll_profile;
        AppTextView tvName,tvUserName,tvTime,tvLikeCount,tvCommentCount,tvDescription,tvHasTag;
        CardView cardViewTweet;
        FlowLayout hastTagFlow;
        public MyViewHolder(View itemView) {
            super(itemView);
            llMain=itemView.findViewById(R.id.ll_main);
            llLike=itemView.findViewById(R.id.ll_like);
            llComment=itemView.findViewById(R.id.ll_comment);
            ll_profile=itemView.findViewById(R.id.ll_profile);
            ivProfileImage=itemView.findViewById(R.id.iv_user_image);
            ivLike=itemView.findViewById(R.id.iv_like);
            ivShare=itemView.findViewById(R.id.iv_share);
            ivPostImage=itemView.findViewById(R.id.iv_tweet_image);
            tvName=itemView.findViewById(R.id.tv_name);
            tvUserName=itemView.findViewById(R.id.tv_user_name);
            tvTime =itemView.findViewById(R.id.tv_time);
            tvLikeCount=itemView.findViewById(R.id.tv_likes_count);
            tvCommentCount=itemView.findViewById(R.id.tv_comment_count);
            tvDescription=itemView.findViewById(R.id.tv_description);
            hastTagFlow=itemView.findViewById(R.id.flow);
            cardViewTweet=itemView.findViewById(R.id.card_view_tweet);
        }
    }

    public TagTweetAdapter(Fragment fragment, ArrayList<Post> dataSet) {
        this.fragment = fragment;
        this.dataSet = dataSet;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_tweet_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        mContext = parent.getContext();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        /////////////User Detail////////////////

        if(!TextUtils.isEmpty(dataSet.get(listPosition).getUserDetail().getProfilePicture())) {
            Glide.with(mContext).load(dataSet.get(listPosition).getUserDetail().getProfilePicture()).placeholder(R.drawable.default_user_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.ivProfileImage);
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.default_user_icon);
        }

        holder.tvName.setText(dataSet.get(listPosition).getUserDetail().getName());

//        if(!TextUtils.isEmpty(dataSet.get(listPosition).getUserDetail().getUserName())) {
//            holder.tvUserName.setVisibility(View.VISIBLE);
//            holder.tvUserName.setText(userData.getUserName());
//        }
//        else
            holder.tvUserName.setVisibility(View.GONE);



        ////////////Tweet Detail//////////////

        if(!TextUtils.isEmpty(dataSet.get(listPosition).getPostDetail().getPhoto())) {
            holder.cardViewTweet.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(dataSet.get(listPosition).getPostDetail().getPhoto()).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.ivPostImage);
        } else {
            holder.cardViewTweet.setVisibility(View.GONE);
        }

        if(dataSet.get(listPosition).getPostDetail().getIsLikes().equals("0")){
            holder.ivLike.setImageResource(R.drawable.unlike);
        }else{
            holder.ivLike.setImageResource(R.drawable.liked);
        }


        if(dataSet.get(listPosition).getPostDetail().getTag().size()!=0) {
            new TagAdapter(mContext,dataSet.get(listPosition).getPostDetail().getTag(),holder.hastTagFlow);
            holder.hastTagFlow.setVisibility(View.VISIBLE);
        }else {
            holder.hastTagFlow.setVisibility(View.GONE);
        }

        Long time=dataSet.get(listPosition).getPostDetail().getCreatedDate();

        holder.tvTime.setText(DateUtils.getRelativeTimeSpanString(time).equals(mContext.getString(R.string.minutes_ago))?mContext.getString(R.string.just_now):DateUtils.getRelativeTimeSpanString(time));
        holder.tvDescription.setText(dataSet.get(listPosition).getPostDetail().getPost());
        holder.tvCommentCount.setText(String.valueOf(dataSet.get(listPosition).getPostDetail().getComments()));
        holder.tvLikeCount.setText(String.valueOf(dataSet.get(listPosition).getPostDetail().getLikes()));


        //////////Click/////////////

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)mContext).replaceFragment(TweetDetailFragment.newInstance(dataSet.get(listPosition).getPostDetail().getId()));
            }
        });

        holder.ll_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)mContext).replaceFragment(ProfileFragment.newInstance(dataSet.get(listPosition).getUserDetail().getId()));

            }
        });

        holder.llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeUnlike(listPosition);
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    private void likeUnlike(final int position) {
        if (CommonUtil.isConnectingToInternet(mContext)) {
            CommonUtil.showProgressDialog((HomeActivity)mContext);
            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<LikeUnlikePojo> call = Service.tweetLikeUnlike(AppController.getManager().getId(),dataSet.get(position).getPostDetail().getId());
            call.enqueue(new Callback<LikeUnlikePojo>() {
                @Override
                public void onResponse(Call<LikeUnlikePojo> call, Response<LikeUnlikePojo> response) {
                    CommonUtil.cancelProgressDialog();
                    LikeUnlikePojo likeUnlikePojo = response.body();
                    if (likeUnlikePojo.getStatus()) {
                        dataSet.get(position).getPostDetail().setIsLikes(likeUnlikePojo.getLikeStatus());
                        int likes=dataSet.get(position).getPostDetail().getLikes();
                        if(likeUnlikePojo.getLikeStatus().equals("0"))
                            dataSet.get(position).getPostDetail().setLikes(likes-1);
                        else
                            dataSet.get(position).getPostDetail().setLikes(likes+1);
                        notifyDataSetChanged();
                    } else {
                        CommonUtil.snackBar(likeUnlikePojo.getMessage(),(HomeActivity)mContext);
                    }
                }

                @Override
                public void onFailure(Call<LikeUnlikePojo> call, Throwable t) {
                    CommonUtil.cancelProgressDialog();
                    CommonUtil.snackBar(t.getMessage(),(HomeActivity)mContext);
                }
            });

        } else {
            CommonUtil.snackBar(mContext.getString(R.string.internet_connection),(HomeActivity)mContext);
        }
    }
}