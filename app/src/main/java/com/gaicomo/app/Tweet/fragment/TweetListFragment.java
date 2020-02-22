package com.gaicomo.app.Tweet.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.HomeModule.fragment.CategoryFragment;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.adapter.TweetAdapter;
import com.gaicomo.app.Tweet.model.tweetPojo.TweetData;
import com.gaicomo.app.Tweet.model.tweetPojo.TweetListingPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TweetListFragment extends BaseFragment implements View.OnClickListener {


    FloatingActionButton fabPost;
    ArrayList<TweetData> arrTweets = new ArrayList<>();
    TweetAdapter tweetAdapter;
    SwipeRefreshLayout swipeToRefresh;
    RecyclerView recyclerTweets;
    AppTextView tvNoData, tvCategory;

    LinearLayoutManager layoutManager;
    String last_id = "0",search="";
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    ProgressBar progressBar;

    public TweetListFragment() {
        // Required empty public constructor
    }

    public static TweetListFragment newInstance() {
        TweetListFragment fragment = new TweetListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_tweet_list);
        if (getArguments() != null) {

        }
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity) getActivity()).setTitle(getString(R.string.app_name), TweetListFragment.this);
        tvCategory = mView.findViewById(R.id.tv_category);
        tvNoData = mView.findViewById(R.id.tv_no_data);
        recyclerTweets = mView.findViewById(R.id.recycler_tweets);
        swipeToRefresh = mView.findViewById(R.id.swipeToRefresh);
        fabPost = mView.findViewById(R.id.fab_post);
//        progressBar = mView.findViewById(R.tab_id.progressBar);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerTweets.setLayoutManager(layoutManager);
//        recyclerTweets.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down));
        tweetAdapter = new TweetAdapter(TweetListFragment.this, arrTweets);
        recyclerTweets.setAdapter(tweetAdapter);
    }

    @Override
    protected void bindControls() {

        tvCategory.setOnClickListener(this);
        fabPost.setOnClickListener(this);

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrTweets.clear();
                last_id = "0";
                getlist();
                swipeToRefresh.setRefreshing(false);
            }
        });

        if (arrTweets.size() == 0)
            getlist();

        recyclerTweets.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrollOutItems = layoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    getlist();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_category:
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount()!=0)
                getActivity().getSupportFragmentManager().popBackStack();
                else
                    ((HomeActivity)getActivity()).replaceFragment(CategoryFragment.newInstance());
                break;
            case R.id.fab_post:
                ((HomeActivity)getActivity()).replaceFragment(PostTweetFragment.newInstance());
                break;
        }
    }


    private void getlist() {
        if (CommonUtil.isConnectingToInternet(getActivity())) {
            if (last_id.equals("0")) {
                showProgressDialog();
            }

            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<TweetListingPojo> call = Service.getTweets(AppController.getManager().getId(),last_id,search);
            call.enqueue(new Callback<TweetListingPojo>() {
                @Override
                public void onResponse(Call<TweetListingPojo> call, Response<TweetListingPojo> response) {
                    cancelProgressDialog();
                    TweetListingPojo tweetListingPojo = response.body();
                    if (tweetListingPojo.getStatus()) {
                        arrTweets.addAll(tweetListingPojo.getData());
                        tweetAdapter.notifyDataSetChanged();
                        if (last_id.equals("0")) {
                            tvNoData.setVisibility(View.GONE);
                            recyclerTweets.setVisibility(View.VISIBLE);
                        }
                        last_id = String.valueOf(arrTweets.size());
                    } else {
                        if (last_id.equals("0")) {
                            tvNoData.setText(getResources().getString(R.string.no_data_found));
                            tvNoData.setVisibility(View.VISIBLE);
                            recyclerTweets.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<TweetListingPojo> call, Throwable t) {
                    if (last_id.equals("0")) {
                        cancelProgressDialog();
                        snackBar(t.getMessage());
                        tvNoData.setText(getResources().getString(R.string.no_data_found));
                        tvNoData.setVisibility(View.VISIBLE);
                        recyclerTweets.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            if (last_id.equals("0")) {
                tvNoData.setText(getResources().getString(R.string.internet_error_message));
                tvNoData.setVisibility(View.VISIBLE);
                recyclerTweets.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        search=data.getStringExtra(Constant.SEARCH_TEXT);
        last_id="0";
        arrTweets.clear();
        getlist();
    }
}
