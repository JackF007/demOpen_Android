package com.gaicomo.app.Tweet.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.fragment.ProfileFragment;
import com.gaicomo.app.Tweet.fragment.TweetDetailFragment;
import com.gaicomo.app.Tweet.fragment.TweetListFragment;
import com.gaicomo.app.Tweet.fragment.TweetTagSearchFragment;
import com.gaicomo.app.Tweet.model.likeUnlikePojo.LikeUnlikePojo;
import com.gaicomo.app.Tweet.model.tweetPojo.TweetData;
import com.gaicomo.app.Tweet.model.tweetPojo.TweetListingPojo;
import com.gaicomo.app.Tweet.model.tweetTagSearch.TweetTagSearchPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.MyViewHolder> implements Serializable {
    boolean animation=true;
    private ArrayList<TweetData> dataSet;
    Context mContext;
    Fragment fragment;

    private static final String TAG = "LiveAdapter";
    private int lastPosition;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage,ivLike,ivShare,ivPostImage;
        LinearLayout llMain,llLike,llComment,llProfile;
        AppTextView tvName,tvUserName,tvTime,tvLikeCount,tvCommentCount,tvDescription;
        CardView cardViewTweet;
        public MyViewHolder(View itemView) {
            super(itemView);
            llMain=itemView.findViewById(R.id.ll_main);
            llLike=itemView.findViewById(R.id.ll_like);
            llComment=itemView.findViewById(R.id.ll_comment);
            llProfile=itemView.findViewById(R.id.ll_profile);
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
            cardViewTweet=itemView.findViewById(R.id.card_view_tweet);
        }

        public void clearAnimation()
        {
         llMain.clearAnimation();
        }
    }

    public TweetAdapter(Fragment fragment, ArrayList<TweetData> dataSet) {
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
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        ((MyViewHolder)holder).clearAnimation();
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
        holder.tvUserName.setText("");

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

        holder.llProfile.setOnClickListener(new View.OnClickListener() {
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
                CommonUtil.showProgressDialog((HomeActivity)mContext);
                Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://www.google.co.in/?id="+dataSet.get(listPosition).getPostDetail().getId()+"&type="+ Constant.TWEET))
                        .setDynamicLinkDomain("giacomo.page.link")
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        .buildShortDynamicLink()
                        .addOnCompleteListener((HomeActivity)mContext, new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                CommonUtil.cancelProgressDialog();
                                if (task.isSuccessful()) {
                                    // Short link created
                                    Uri shortLink = task.getResult().getShortLink();
                                    CommonUtil.sendLink((HomeActivity)mContext,mContext.getString(R.string.Social_awareness),AppController.getManager().getName()+mContext.getString(R.string.shared_the_tweet)+shortLink.toString());

                                } else {
                                    Toast.makeText((HomeActivity)mContext, mContext.getString(R.string.link_could_not_be_generated), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        if(animation)
            setAnimation(holder.itemView);
        else
            animation=true;



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
                        animation=false;
                       notifyItemChanged(position);

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

    private void setAnimation(View viewToAnimate)
    {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
    }




}