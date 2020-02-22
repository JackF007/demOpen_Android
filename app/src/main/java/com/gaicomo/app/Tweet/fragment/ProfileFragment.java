package com.gaicomo.app.Tweet.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.adapter.ProfileTweetAdapter;
import com.gaicomo.app.Tweet.model.profileDetail.Data;
import com.gaicomo.app.Tweet.model.profileDetail.Post;
import com.gaicomo.app.Tweet.model.profileDetail.PostDetail;
import com.gaicomo.app.Tweet.model.profileDetail.ProfileDetailPojo;
import com.gaicomo.app.Tweet.model.profileDetail.UserData;
import com.gaicomo.app.network.NetworkCalls;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.utils.TouchDetectableScrollView;
import com.gaicomo.app.utils.imagecropper.TakeImageClass;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebConstants;
import com.gaicomo.app.webutils.WebInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends BaseFragment implements View.OnClickListener,TakeImageClass.imagefromcropper,NetworkCalls.NetworkCallBack {

    private static final String TAG = "ProfileFragment";
    UserData userData=new UserData();
    ArrayList<Post> arrTweets = new ArrayList<>();
    String user_id="";
    ImageView ivProfileImage,ivEdit,ivBack;
    AppTextView tvDob,tvGender,tvTitle,tvSave;
    AppEditText etDescription,etName;
    RecyclerView recyclerTweets;
    ProfileTweetAdapter profileTweetAdapter;
    SwipeRefreshLayout swipeToRefresh;
    LinearLayoutManager layoutManager;
    TouchDetectableScrollView nestedScrollView;
    String last_id = "0";
    Boolean isfetching = false, tweetData = true,edit=false;
    private PopupMenu popup;
    NetworkCalls networkCalls = null;
    File file = null;
    TakeImageClass takeImageClass;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };


    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String user_id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(Constant.USER_ID, user_id);

        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_profile);
        if (getArguments() != null) {
            user_id = getArguments().getString(Constant.USER_ID);
        }
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity)getActivity()).setTitle("",ProfileFragment.this);
        networkCalls = new NetworkCalls(ProfileFragment.this, getActivity());

        ivProfileImage=mView.findViewById(R.id.iv_profile);
        ivBack=mView.findViewById(R.id.iv_back);
        ivEdit=mView.findViewById(R.id.iv_edit);
        tvDob=mView.findViewById(R.id.tv_dob);
        tvGender=mView.findViewById(R.id.tv_gender);
        tvTitle=mView.findViewById(R.id.tv_title);
        tvSave=mView.findViewById(R.id.tv_save);
        etDescription=mView.findViewById(R.id.ev_description);
        etName=mView.findViewById(R.id.et_name);
        recyclerTweets=mView.findViewById(R.id.recycler_tweets);
        nestedScrollView = mView.findViewById(R.id.nested_scroll);
        swipeToRefresh = mView.findViewById(R.id.swipeToRefresh);
    }

    @Override
    protected void bindControls() {
        tweetData=true;
        ivBack.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        ivProfileImage.setOnClickListener(this);
        tvDob.setOnClickListener(this);
        tvGender.setOnClickListener(this);

        if(AppController.getManager().getId().equals(user_id))
            ivEdit.setVisibility(View.VISIBLE);
        else
            ivEdit.setVisibility(View.INVISIBLE);

        popup = new PopupMenu(getActivity(), tvGender);
        popup.getMenuInflater().inflate(R.menu.gender_pop_up, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                tvGender.setText(item.getTitle());
                return false;
            }
        });

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerTweets.setLayoutManager(layoutManager);
        profileTweetAdapter = new ProfileTweetAdapter(ProfileFragment.this, arrTweets,userData);
        recyclerTweets.setAdapter(profileTweetAdapter);
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

//        if (arrTweets.size() == 0)
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
    public void onClick(View v) {
        hideKeyboard(getActivity());
        switch (v.getId()){
            case R.id.iv_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.iv_edit:
                ivEdit.setVisibility(View.GONE);
                tvSave.setVisibility(View.VISIBLE);
                edit=true;
                etName.setEnabled(true);
                etName.requestFocus();
                etName.setSelection(etName.length());
                etDescription.setEnabled(true);
                break;
            case R.id.tv_save:
                networkCalls.NetworkAPICall(WebConstants.UPDATE_PROFILE_URL, true);
                break;
            case R.id.tv_dob:
                if(edit)
                    CommonUtil.DatePicker(getActivity(),tvDob);
                break;
            case R.id.tv_gender:
                if(edit)
                    popup.show();
                break;
            case R.id.iv_profile:
                 if(edit)
                     permissionCheck();
                break;
        }
    }

    private void getTweet() {

        isfetching = true;
        if (CommonUtil.isConnectingToInternet(getActivity())) {
            if (last_id.equals("0")) {
                showProgressDialog();
            }

            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<ProfileDetailPojo> call = Service.getUserProfile(user_id, last_id);
            call.enqueue(new Callback<ProfileDetailPojo>() {
                @Override
                public void onResponse(Call<ProfileDetailPojo> call, Response<ProfileDetailPojo> response) {
                    cancelProgressDialog();
                    ProfileDetailPojo ProfileDetailPojo = response.body();
                    if (ProfileDetailPojo.getStatus()) {
                        if (tweetData) {
                            userData=ProfileDetailPojo.getData().getUserData();
                            setData(userData);
                            tweetData = false;
                        }
                        arrTweets.addAll(ProfileDetailPojo.getData().getPosts());
                        profileTweetAdapter.notifyDataSetChanged();
                        last_id = String.valueOf(arrTweets.size());
                    }
                    isfetching = false;
                }

                @Override
                public void onFailure(Call<ProfileDetailPojo> call, Throwable t) {
                    if (last_id.equals("0")) {
                        cancelProgressDialog();
                        snackBar(t.getMessage());
                    }
                    isfetching = false;
                }
            });

        } else {
            if (last_id.equals("0")) {
                snackBar(getString(R.string.internet_connection));
            }
            isfetching = false;
        }
    }


    private void setData(UserData userData) {
        if (!TextUtils.isEmpty(userData.getProfilePicture())) {
            Glide.with(getActivity()).load(userData.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivProfileImage);
        }

        etName.setText(userData.getName());
        etDescription.setText(userData.getDescription());
        tvDob.setText(userData.getDob());
        tvGender.setText(userData.getGender());
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
    public Builders.Any.B getAPIB(String ApiName) {
        Builders.Any.B ion = null;
        switch (ApiName) {
            case WebConstants.UPDATE_PROFILE_URL:
                ion = (Builders.Any.B) Ion.with(getActivity()).load(WebConstants.BASE_URL + WebConstants.UPDATE_PROFILE_URL).
                        setMultipartParameter(Constant.ID, AppController.getManager().getId()).
                        setMultipartParameter(Constant.NAME, etName.getText().toString().trim()).
                        setMultipartParameter(Constant.Dob, tvDob.getText().toString().trim()).
                        setMultipartParameter(Constant.description, etDescription.getText().toString().trim()).
                        setMultipartParameter(Constant.GENDER, tvGender.getText().toString().trim());


                if (file != null)
                    ion.setMultipartFile(Constant.PROFILE_PICTURE, file);
                return ion;
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject Result, String ApiName) throws JSONException {
        Log.e("SuccessCallBack", "SuccessCallBack " + Result.toString());
        Gson gson = new Gson();

        switch (ApiName) {
            case WebConstants.UPDATE_PROFILE_URL:
                try {
                    if (Result.optBoolean(Constant.STATUS)) {
                        JSONObject jsonObject=Result.getJSONObject("data");
                        com.gaicomo.app.LoginModule.model.Login.Data data =new Gson().fromJson(jsonObject.toString(),com.gaicomo.app.LoginModule.model.Login.Data.class);
                        AppController.getManager().setId(data.getId());
                        AppController.getManager().setName(data.getName());
                        AppController.getManager().setEmail(data.getEmail());
                        AppController.getManager().setMobile(data.getMobile());
                        AppController.getManager().setProfile_image(data.getProfilePicture());
                        AppController.getManager().setUser_name(data.getUserName());
                        ((HomeActivity)getActivity()).setImage();
                        etName.setEnabled(false);
                        etDescription.setEnabled(false);
                        ivEdit.setVisibility(View.VISIBLE);
                        tvSave.setVisibility(View.GONE);
                        edit=false;
                        snackBar(Result.getString(Constant.MESSAGE));
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
        snackBar(Message);
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
        ivProfileImage.setImageBitmap(takeImageClass.StringToBitMap(str));
        file = new File(str);
    }

}
