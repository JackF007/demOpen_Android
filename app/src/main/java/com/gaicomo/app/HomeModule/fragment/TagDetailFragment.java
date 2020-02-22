package com.gaicomo.app.HomeModule.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.HomeModule.adapter.TagTweetAdapter;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.model.tweetTagSearch.Post;
import com.gaicomo.app.Tweet.model.tweetTagSearch.TagDetail;
import com.gaicomo.app.Tweet.model.tweetTagSearch.TweetTagSearchPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.utils.TouchDetectableScrollView;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagDetailFragment extends BaseFragment implements  View.OnClickListener{

    TagDetail tagDetail=new TagDetail();
    private static final String TAG = "TagDetailFragment";
    ArrayList<Post> arrTweets = new ArrayList<>();
    String user_id="";
    ImageView ivBack;
    AppTextView tvTitle;
    AppEditText tvTag,tvDescription;
    RecyclerView recyclerTweets;
    TagTweetAdapter tagTweetAdapter;
    SwipeRefreshLayout swipeToRefresh;
    LinearLayoutManager layoutManager;
    TouchDetectableScrollView nestedScrollView;
    String last_id = "0";
    Boolean isfetching = false, tweetData = true,edit=false;
    String tag="";

    public static TagDetailFragment newInstance(String tag) {
        Bundle args = new Bundle();
        TagDetailFragment fragment = new TagDetailFragment();
        args.putString(Constant.TAG,tag);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_tag_detail);
        if(getArguments()!=null){
            tag=getArguments().getString(Constant.TAG);
        }
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity)getActivity()).setTitle("",TagDetailFragment.this);
        ivBack=mView.findViewById(R.id.iv_back);

        tvTitle=mView.findViewById(R.id.tv_title);
        tvTag=mView.findViewById(R.id.tv_tag);
        tvDescription=mView.findViewById(R.id.tv_description);
        recyclerTweets=mView.findViewById(R.id.recycler_tweets);
        nestedScrollView = mView.findViewById(R.id.nested_scroll);
        swipeToRefresh = mView.findViewById(R.id.swipeToRefresh);
    }

    @Override
    protected void bindControls() {
        tweetData=true;
        ivBack.setOnClickListener(this);
        tvTitle.setText(tag);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerTweets.setLayoutManager(layoutManager);
        tagTweetAdapter = new TagTweetAdapter(TagDetailFragment.this, arrTweets);
        recyclerTweets.setAdapter(tagTweetAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerTweets, false);

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrTweets.clear();
                last_id = "0";
                tweetData = true;
                getTweet();
                swipeToRefresh.setRefreshing(false);
            }
        });

        if (arrTweets.size() == 0)
            getTweet();


        nestedScrollView.setMyScrollChangeListener(new TouchDetectableScrollView.OnMyScrollChangeListener() {
            @Override
            public void onBottomReached() {
                if (!isfetching)
                    getTweet();
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity)getActivity()).llToolbar.setVisibility(View.GONE);    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((HomeActivity)getActivity()).llToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard(getActivity());
        switch (v.getId()){
            case R.id.iv_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }


    private void getTweet() {
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
                    if (tweetListingPojo.getStatus()) {
                        if (tweetData) {
                            tvTag.setText(tweetListingPojo.getData().getTagDetail().getTag());
                            tvDescription.setText(tweetListingPojo.getData().getTagDetail().getDescription());
                            tweetData = false;
                        }

                        arrTweets.addAll(tweetListingPojo.getData().getPosts());
                        tagTweetAdapter.notifyDataSetChanged();
                        last_id = String.valueOf(arrTweets.size());
                    }
                }

                @Override
                public void onFailure(Call<TweetTagSearchPojo> call, Throwable t) {
                    if (last_id.equals("0")) {
                        cancelProgressDialog();
                        snackBar(t.getMessage());
                    }
                }
            });

        } else {
            if (last_id.equals("0")) {
                snackBar(getResources().getString(R.string.internet_error_message));
            }
        }
    }
}
