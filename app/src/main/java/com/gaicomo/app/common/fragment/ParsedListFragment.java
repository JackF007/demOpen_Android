package com.gaicomo.app.common.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.common.adapter.ParsedListAdapter;
import com.gaicomo.app.common.model.parsedList.ParsedData;
import com.gaicomo.app.common.model.parsedList.ParsedPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ParsedListFragment extends BaseFragment {

    String name="",id="",root_id="";
    ParsedListAdapter parsedListAdapter;
    ArrayList<ParsedData> arrParsed=new ArrayList<>();
    SwipeRefreshLayout swipeToRefresh;
    RecyclerView recyclerView;
    AppTextView tvNoData;


    public ParsedListFragment() {
    }

    public static ParsedListFragment newInstance(String name, String id, String root_id) {
        ParsedListFragment fragment = new ParsedListFragment();
        Bundle args = new Bundle();
        args.putString(Constant.SUB_CAT_ID,id);
        args.putString(Constant.ROOT_ID,root_id);
        args.putString(Constant.NAME,name);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_list);
        if(getArguments()!=null){
            name=getArguments().getString(Constant.NAME);
            id=getArguments().getString(Constant.SUB_CAT_ID);
            root_id=getArguments().getString(Constant.ROOT_ID);
        }
    }


    @Override
    protected void initView(View mView) {
        ((HomeActivity)getActivity()).setTitle(name,ParsedListFragment.this);

        recyclerView=mView.findViewById(R.id.recycler_view);
        swipeToRefresh=mView.findViewById(R.id.swipeToRefresh);
        tvNoData=mView.findViewById(R.id.tv_no_data);
    }

    @Override
    protected void bindControls() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        parsedListAdapter=new ParsedListAdapter(this, arrParsed);
        recyclerView.setAdapter(parsedListAdapter);

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getlist();
                swipeToRefresh.setRefreshing(false);
            }
        });

        if(arrParsed.size()==0)
            getlist();
    }

    private void getlist() {
        if (CommonUtil.isConnectingToInternet(getActivity())) {
            showProgressDialog();
            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<ParsedPojo> call = Service.parsedData(id,root_id);
            call.enqueue(new Callback<ParsedPojo>() {
                @Override
                public void onResponse(Call<ParsedPojo> call, Response<ParsedPojo> response) {
                    cancelProgressDialog();
                    ParsedPojo parsedPojo = response.body();
                    if (parsedPojo.getStatus()) {
                        arrParsed.clear();
                        arrParsed.addAll(parsedPojo.getData());
                        parsedListAdapter.notifyDataSetChanged();
                        tvNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        snackBar(parsedPojo.getMessage());
                        tvNoData.setText(parsedPojo.getMessage());
                        tvNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    parsedListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<ParsedPojo> call, Throwable t) {
                    cancelProgressDialog();
                    snackBar(t.getMessage());
                    tvNoData.setText(getResources().getString(R.string.no_data_found));
                    tvNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            });

        }else{
            tvNoData.setText(getResources().getString(R.string.internet_error_message));
            tvNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


}
