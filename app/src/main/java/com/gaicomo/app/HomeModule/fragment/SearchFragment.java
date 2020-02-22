package com.gaicomo.app.HomeModule.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.HomeModule.adapter.TagAdapter;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.model.tagPojo.Data;
import com.gaicomo.app.Tweet.model.tagPojo.TagListPojo;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebInterface;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends BaseFragment implements View.OnClickListener {


    AppEditText etSearch;
    AppTextView tvNoDataFound;
    ImageView ivCross;
    RecyclerView recyclerTags;
    ArrayList<Data> arrTagList=new ArrayList<>();
    ArrayList<Data> arrTagListSearched=new ArrayList<>();
    TagAdapter  tagAdapter;
    String text="",type;



    public static SearchFragment newInstance(String type) {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        args.putString(Constant.TYPE,type);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_search);
        if(getArguments()!=null){
            type=getArguments().getString(Constant.TYPE);
        }
    }
    @Override
    protected void initView(View mView) {
        etSearch=mView.findViewById(R.id.et_search);
        ivCross=mView.findViewById(R.id.iv_cross);
        recyclerTags=mView.findViewById(R.id.recycler_tag);
        tvNoDataFound=mView.findViewById(R.id.tv_no_match);
    }

    @Override
    protected void bindControls() {
        ivCross.setOnClickListener(this);
        recyclerTags.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
//        tagAdapter= new TagAdapter(SearchFragment.this,arrTagListSearched,type);
        recyclerTags.setAdapter(tagAdapter);
        if(arrTagList.size()==0)
        getTagList();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(etSearch.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void filter(String textTyped) {
        this.text = textTyped.toLowerCase(Locale.getDefault());
        arrTagListSearched.clear();
        if (text.length() == 0) {
            arrTagListSearched.addAll(arrTagList);
        } else {
            for (Data d : arrTagList) {
                if (d.getTag().toLowerCase(Locale.getDefault()).contains(text)) {
                    arrTagListSearched.add(d);
                }
            }
        }

        if(arrTagListSearched.size()==0){
            tvNoDataFound.setVisibility(View.VISIBLE);
            recyclerTags.setVisibility(View.GONE);
        }else{
            tvNoDataFound.setVisibility(View.GONE);
            recyclerTags.setVisibility(View.VISIBLE);
        }
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_cross:
                if(!TextUtils.isEmpty(etSearch.getText().toString().trim()))
                etSearch.setText("");
                else
                    getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((HomeActivity)getActivity()).llToolbar.setVisibility(View.GONE);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        ((HomeActivity)getActivity()).llToolbar.setVisibility(View.VISIBLE);
        super.onDestroyView();
    }


    public void getTagList() {
        if (CommonUtil.isConnectingToInternet(getActivity())) {
            showProgressDialog();
            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<TagListPojo> call = Service.tagList();
            call.enqueue(new Callback<TagListPojo>() {
                @Override
                public void onResponse(Call<TagListPojo> call, Response<TagListPojo> response) {
                    cancelProgressDialog();
                    TagListPojo tagListPojo=response.body();
                    if(tagListPojo.getStatus()) {
                        arrTagList.addAll(tagListPojo.getData());
                        arrTagListSearched.addAll(tagListPojo.getData());
                        tagAdapter.notifyDataSetChanged();
                    }else{
                        snackBar(tagListPojo.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<TagListPojo> call, Throwable t) {
                    snackBar(t.getMessage());
                }
            });

        } else {
            snackBar(getString(R.string.internet_connection));
        }
    }
}
