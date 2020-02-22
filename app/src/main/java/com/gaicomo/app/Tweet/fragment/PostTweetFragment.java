package com.gaicomo.app.Tweet.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.model.tagPojo.Data;
import com.gaicomo.app.Tweet.model.tagPojo.TagListPojo;
import com.gaicomo.app.network.NetworkCalls;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.utils.imagecropper.TakeImageClass;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebConstants;
import com.gaicomo.app.webutils.WebInterface;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostTweetFragment extends BaseFragment implements View.OnClickListener,NetworkCalls.NetworkCallBack, TakeImageClass.imagefromcropper {

    String selectedId="";
    List<String> tagList=new ArrayList<>();

    ArrayList<Data> arrTagList=new ArrayList<>();
    private static final String TAG = "PostTweetFragment";

    AppTextView tvName;
    AppEditText etPost;
    Button btnSubmit;
    MultiAutoCompleteTextView acHashTag;
    ImageView ivPost,ivUserProfile;
    NetworkCalls networkCalls = null;
    File file = null;
    TakeImageClass takeImageClass;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };


    public PostTweetFragment() {
        // Required empty public constructor
    }

    public static PostTweetFragment newInstance() {
        PostTweetFragment fragment = new PostTweetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_post_tweet);
        if (getArguments() != null) {

        }
    }


    @Override
    protected void initView(View mView) {
        ((HomeActivity)getActivity()).setTitle(getString(R.string.new_post),PostTweetFragment.this);
        networkCalls = new NetworkCalls(PostTweetFragment.this, getActivity());

        tvName=mView.findViewById(R.id.tv_name);
        etPost=mView.findViewById(R.id.et_post);
        btnSubmit= mView.findViewById(R.id.btn_submit);
        acHashTag=mView.findViewById(R.id.ac_hashtag);
        ivPost=mView.findViewById(R.id.iv_post);
        ivUserProfile=mView.findViewById(R.id.iv_user_image);
    }

    @Override
    protected void bindControls() {
        if(!TextUtils.isEmpty(AppController.getManager().getProfile_image()))
            Glide.with(getActivity()).load(AppController.getManager().getProfile_image()).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivUserProfile);

        tvName.setText(AppController.getManager().getName());
        ivPost.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        acHashTag.setOnClickListener(this);


        getTagList();
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_submit:
                selectedId="";
                if(!TextUtils.isEmpty(acHashTag.getText().toString().trim())) {
                    String[] selectedTag = acHashTag.getText().toString().trim().split(",");

                    for(int i=0;i<selectedTag.length;i++){
                        for(int j=0;j<arrTagList.size();j++){
                            if(selectedTag[i].replace(" ","").equals(arrTagList.get(j).getTag())){
                                if(selectedId.equals(""))
                                    selectedId=arrTagList.get(j).getId();
                                else
                                    selectedId=selectedId+","+arrTagList.get(j).getId();
                            }
                        }
                    }
                }

                Log.d(TAG, "onClick: " +selectedId);

                if(!TextUtils.isEmpty(etPost.getText().toString().trim())){
                    networkCalls.NetworkAPICall(WebConstants.TWEET_POST_URL, true);
                }else{
                    snackBar(getString(R.string.please_write_something));
                }

                break;

            case R.id.iv_post:
                permissionCheck();
                break;
        }
    }


    @Override
    public Builders.Any.B getAPIB(String ApiName) {
        Builders.Any.B ion = null;
        switch (ApiName) {
            case WebConstants.TWEET_POST_URL:
                ion = (Builders.Any.B) Ion.with(getActivity()).load(WebConstants.BASE_URL + WebConstants.TWEET_POST_URL).
                        setMultipartParameter(Constant.USER_ID, AppController.getManager().getId()).
                        setMultipartParameter(Constant.POST, etPost.getText().toString().trim());

                if(!TextUtils.isEmpty(selectedId))
                    ion.setMultipartParameter(Constant.TAG,selectedId);

                if (file != null)
                    ion.setMultipartFile(Constant.PHOTO, file);
                return ion;
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject Result, String ApiName) throws JSONException {
        Log.e("SuccessCallBack", "SuccessCallBack " + Result.toString());
        Gson gson = new Gson();

        switch (ApiName) {
            case WebConstants.TWEET_POST_URL:
                try {
                    if (Result.optBoolean(Constant.STATUS)) {
                        snackBar(Result.getString(Constant.MESSAGE));
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        this.ErrorCallBack(Result.getString(Constant.MESSAGE), ApiName);
                    }
                } catch (Exception ex) {
                    this.ErrorCallBack(ex.getMessage() + " : " + ex.getLocalizedMessage(), ApiName);
                    ex.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void ErrorCallBack(String Message, String ApiName) {
    }

    public void permissionCheck() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissions(getActivity(), permissions)) {
                onPickImage();
            }
        } else {
            onPickImage();
        }
    }

    public void onPickImage() {
        takeImageClass = new TakeImageClass(getActivity(), this);
        takeImageClass.getImagePickerDialog(getActivity(), getString(R.string.select_option));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                ArrayList<String> _arPermission = new ArrayList<String>();
                if (grantResults.length > 0) {
                    Log.d(TAG, "length" + permissions.length);
                    for (int i = 0; i < permissions.length; i++) {
                        Log.d(TAG, "lengthch" + permissions[i] + " " + grantResults[i]);
                        if (grantResults[i] != 0) {
                            _arPermission.add("" + grantResults[i]);
                        }
                    }

                    if (_arPermission.size() == 0) {
                        onPickImage();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        CommonUtil.showAlert(getActivity(), getString(R.string.enable_permission), getString(R.string.permissions));
                    }
                }
            }
        }
    }

    @Override
    public void imagePath(String str) {
        ivPost.setImageBitmap(takeImageClass.StringToBitMap(str));
        file = new File(str);
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
                        for(int i=0;i<arrTagList.size();i++)
                            tagList.add(arrTagList.get(i).getTag());

                        acHashTag.setThreshold(1);
                        acHashTag.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                        acHashTag.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tagList));

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
