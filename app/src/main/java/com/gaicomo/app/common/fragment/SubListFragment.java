package com.gaicomo.app.common.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.common.adapter.ListAdapter;
import com.gaicomo.app.common.model.listpojo.ListData;
import com.gaicomo.app.common.model.listpojo.ListPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubListFragment extends BaseFragment {

    ListAdapter listAdapter;
    ArrayList<ListData> arrCategory = new ArrayList<>();
    SwipeRefreshLayout swipeToRefresh;
    RecyclerView recyclerView;
    AppTextView tvNoData;
    LinearLayoutManager layoutManager;
    String last_id = "0", search = "", name = "",root_id="",category_id="";
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;

    public SubListFragment() {
    }

    public static SubListFragment newInstance(String name, String root_id,String category_id) {
        SubListFragment fragment = new SubListFragment();
        Bundle args = new Bundle();
        args.putString(Constant.NAME,name);
        args.putString(Constant.ROOT_ID,root_id);
        args.putString(Constant.CATEGORY_ID,category_id);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_list);
        if(getArguments()!=null){
            name=getArguments().getString(Constant.NAME);
            root_id=getArguments().getString(Constant.ROOT_ID);
            category_id=getArguments().getString(Constant.CATEGORY_ID);
        }
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity) getActivity()).setTitle(name, SubListFragment.this);
        recyclerView = mView.findViewById(R.id.recycler_view);
        swipeToRefresh = mView.findViewById(R.id.swipeToRefresh);
        tvNoData = mView.findViewById(R.id.tv_no_data);
    }

    @Override
    protected void bindControls() {
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new ListAdapter(this, arrCategory);
        recyclerView.setAdapter(listAdapter);

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrCategory.clear();
                last_id = "0";
                getlist();
                swipeToRefresh.setRefreshing(false);
            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrCategory.clear();
                last_id = "0";
                getlist();
                swipeToRefresh.setRefreshing(false);
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


        if (arrCategory.size() == 0)
            getlist();
    }


    private void getlist() {
        if (CommonUtil.isConnectingToInternet(getActivity())) {
            if (last_id.equals("0")) {
                showProgressDialog();
            }

            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<ListPojo> call = Service.subListing(root_id,category_id, last_id, search);
            call.enqueue(new Callback<ListPojo>() {
                @Override
                public void onResponse(Call<ListPojo> call, Response<ListPojo> response) {
                    cancelProgressDialog();
                    ListPojo listPojo = response.body();
                    if (listPojo.getStatus()) {
                        arrCategory.addAll(listPojo.getData());
                        listAdapter.notifyDataSetChanged();
                        if (last_id.equals("0")) {
                            tvNoData.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        last_id = String.valueOf(arrCategory.size());
                    } else {
                        if (last_id.equals("0")) {
                            tvNoData.setText(getResources().getString(R.string.no_data_found));
                            tvNoData.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<ListPojo> call, Throwable t) {
                    if (last_id.equals("0")) {
                        cancelProgressDialog();
                        snackBar(t.getMessage());
                        tvNoData.setText(getResources().getString(R.string.no_data_found));
                        tvNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            if (last_id.equals("0")) {
                tvNoData.setText(getResources().getString(R.string.internet_error_message));
                tvNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        search = data.getStringExtra(Constant.SEARCH_TEXT);
        last_id = "0";
        arrCategory.clear();
        getlist();
    }



}
