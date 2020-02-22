package com.gaicomo.app.Tweet.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.gaicomo.app.Tweet.model.tweetTagSearch.TweetTagSearchPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TweetTagSearchFragment extends BaseFragment {


    ArrayList<TweetData> arrTweets = new ArrayList<>();
    TweetAdapter tweetAdapter;
    SwipeRefreshLayout swipeToRefresh;
    RecyclerView recyclerTweets;
    AppTextView tvNoData, tvCategory;

    LinearLayoutManager layoutManager;
    String last_id = "0";

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    String tag="",id="";

    public TweetTagSearchFragment() {
        // Required empty public constructor
    }

    public static TweetTagSearchFragment newInstance(String id,String tag) {
        TweetTagSearchFragment fragment = new TweetTagSearchFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ID,id);
        args.putString(Constant.TAG,tag);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_tweet_tag_search_list);
        if (getArguments() != null) {
          tag=getArguments().getString(Constant.TAG);
          id=getArguments().getString(Constant.ID);
        }
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity) getActivity()).setTitle(tag,TweetTagSearchFragment.this);
        tvCategory = mView.findViewById(R.id.tv_category);
        tvNoData = mView.findViewById(R.id.tv_no_data);
        recyclerTweets = mView.findViewById(R.id.recycler_tweets);
        swipeToRefresh = mView.findViewById(R.id.swipeToRefresh);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerTweets.setLayoutManager(layoutManager);
        tweetAdapter = new TweetAdapter(TweetTagSearchFragment.this, arrTweets);
        recyclerTweets.setAdapter(tweetAdapter);
    }

    @Override
    protected void bindControls() {

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


    private void getlist() {
        if (CommonUtil.isConnectingToInternet(getActivity())) {
            if (last_id.equals("0")) {
                showProgressDialog();
            }

            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<TweetTagSearchPojo> call = Service.tagSearch(AppController.getManager().getId(),tag,last_id);
            call.enqueue(new Callback<TweetTagSearchPojo>() {
                @Override
                public void onResponse(Call<TweetTagSearchPojo> call, Response<TweetTagSearchPojo> response) {
                    cancelProgressDialog();
                    TweetTagSearchPojo tweetListingPojo = response.body();
                    TweetData tweetData=null;
                    if (tweetListingPojo.getStatus()) {

                        for(int i=0;i<tweetListingPojo.getData().getPosts().size();i++){
                            tweetData=new TweetData();
                            tweetData.setPostDetail(tweetListingPojo.getData().getPosts().get(i).getPostDetail());
                            tweetData.setUserDetail(tweetListingPojo.getData().getPosts().get(i).getUserDetail());
                            arrTweets.add(tweetData);
                        }

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
                public void onFailure(Call<TweetTagSearchPojo> call, Throwable t) {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((HomeActivity)getActivity()).llShadow.setVisibility(View.GONE);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        ((HomeActivity)getActivity()).llShadow.setVisibility(View.VISIBLE);
        super.onDestroyView();
    }
}
