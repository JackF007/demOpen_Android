package com.gaicomo.app.HomeModule.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseActivity;
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

public class SearchActivity extends BaseActivity implements View.OnClickListener {


    AppEditText etSearch;
    AppTextView tvNoDataFound;
    ImageView ivCross, ivSearch;
    RecyclerView recyclerTags;
    ArrayList<Data> arrTagList = new ArrayList<>();
    ArrayList<Data> arrTagListSearched = new ArrayList<>();
    TagAdapter tagAdapter;
    String text = "", type = "";
    Intent returnIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getIntent().hasExtra(Constant.TYPE)) {
            type = getIntent().getStringExtra(Constant.TYPE);
        }
        etSearch = findViewById(R.id.et_search);
        ivCross = findViewById(R.id.iv_cross);
        ivSearch = findViewById(R.id.iv_search);

        recyclerTags = findViewById(R.id.recycler_tag);
        tvNoDataFound = findViewById(R.id.tv_no_match);


        ivCross.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
//        etSearch.setText(AppController.getManager().getSearch());
//        etSearch.setSelection(etSearch.getText().length());
        if (type.equals("OpenDataFragment") || type.equals("TweetListFragment")) {
            recyclerTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            tagAdapter = new TagAdapter(arrTagListSearched, type);
            recyclerTags.setAdapter(tagAdapter);
            if (arrTagList.size() == 0)
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
        }else{

        }
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

        if (arrTagListSearched.size() == 0) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            recyclerTags.setVisibility(View.GONE);
        } else {
            tvNoDataFound.setVisibility(View.GONE);
            recyclerTags.setVisibility(View.VISIBLE);
        }
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                if (!TextUtils.isEmpty(etSearch.getText().toString().trim()))
                    etSearch.setText("");
                else {
//                    AppController.getManager().setSearch(etSearch.getText().toString().trim());
                    returnIntent.putExtra(Constant.SEARCH_TEXT, "");
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            case R.id.iv_search:
//                AppController.getManager().setSearch(etSearch.getText().toString().trim());
                returnIntent.putExtra(Constant.SEARCH_TEXT, etSearch.getText().toString().trim());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
//                if (!type.equals("OpenDataFragment") && !type.equals("TweetListFragment")) {
//
//                } else {
//                    snackBar("Please select the tag from the list.", recyclerTags);
//                }
                break;
        }
    }


    public void getTagList() {
        if (CommonUtil.isConnectingToInternet(this)) {
            showProgressDialog("Please wait...");
            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<TagListPojo> call = Service.tagList();
            call.enqueue(new Callback<TagListPojo>() {
                @Override
                public void onResponse(Call<TagListPojo> call, Response<TagListPojo> response) {
                    cancelProgressDialog();
                    TagListPojo tagListPojo = response.body();
                    if (tagListPojo.getStatus()) {
                        arrTagList.addAll(tagListPojo.getData());
                        arrTagListSearched.addAll(tagListPojo.getData());
                        tagAdapter.notifyDataSetChanged();
                    } else {
                        snackBar(tagListPojo.getMessage(), recyclerTags);
                    }
                }

                @Override
                public void onFailure(Call<TagListPojo> call, Throwable t) {
                    snackBar(t.getMessage(), recyclerTags);
                }
            });

        } else {
            snackBar(getString(R.string.internet_connection), recyclerTags);
        }
    }


    public void selectedTag(String id,String text){
        returnIntent.putExtra(Constant.SEARCH_TEXT, text);
        returnIntent.putExtra(Constant.ID, id);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
