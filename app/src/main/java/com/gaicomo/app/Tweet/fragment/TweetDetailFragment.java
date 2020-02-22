package com.gaicomo.app.Tweet.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaicomo.app.AppController;
import com.gaicomo.app.Base.BaseFragment;
import com.gaicomo.app.HomeModule.activity.HomeActivity;
import com.gaicomo.app.R;
import com.gaicomo.app.Tweet.adapter.CommentAdapter;
import com.gaicomo.app.Tweet.model.likeUnlikePojo.LikeUnlikePojo;
import com.gaicomo.app.Tweet.model.tweetDatailPojo.Comment;
import com.gaicomo.app.Tweet.model.tweetDatailPojo.TweetDetailPojo;
import com.gaicomo.app.network.NetworkCalls;
import com.gaicomo.app.utils.CommonUtil;
import com.gaicomo.app.utils.Constant;
import com.gaicomo.app.utils.TagAdapter;
import com.gaicomo.app.utils.TouchDetectableScrollView;
import com.gaicomo.app.utils.imagecropper.TakeImageClass;
import com.gaicomo.app.views.AppEditText;
import com.gaicomo.app.views.AppTextView;
import com.gaicomo.app.webutils.WebConstants;
import com.gaicomo.app.webutils.WebInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TweetDetailFragment extends BaseFragment implements View.OnClickListener, TakeImageClass.imagefromcropper, NetworkCalls.NetworkCallBack {

    private static final String TAG = "TweetDetailFragment";
    NetworkCalls networkCalls = null;
    FrameLayout flSelected;
    TouchDetectableScrollView nestedScrollView;
    ImageView ivProfileImage, ivLike, ivShare, ivPostImage, ivUploadImage, ivSelectedImage, ivDelete, ivSend;
    LinearLayout llMain, llLike, llComment,llProfile;
    AppTextView tvName, tvUserName, tvTime, tvLikeCount, tvCommentCount, tvDescription;
    AppEditText etComment;
    CardView cardViewTweet;
    ArrayList<Comment> arrComment = new ArrayList<>();
    RecyclerView recyclerComments;
    FlowLayout flow;
    SwipeRefreshLayout swipeToRefresh;
    String tweet_id = "";
    LinearLayoutManager layoutManager;
    CommentAdapter commentAdapter;
    String last_id = "0";
    Boolean isfetching = false, tweetData = true;
    int currentItems, totalItems, scrollOutItems;
    File file = null;
    TakeImageClass takeImageClass;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private TweetDetailPojo tweetDetailPojo = null;
    private Integer likes = 0;

    public TweetDetailFragment() {
    }


    public static TweetDetailFragment newInstance(String id) {
        TweetDetailFragment fragment = new TweetDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constant.TWEET_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.fragment_tweet_detail);
        if (getArguments() != null) {
            tweet_id = getArguments().getString(Constant.TWEET_ID);
        }
    }

    @Override
    protected void initView(View mView) {
        ((HomeActivity) getActivity()).setTitle(getString(R.string.tweet), TweetDetailFragment.this);
        networkCalls = new NetworkCalls(TweetDetailFragment.this, getActivity());
        flSelected = mView.findViewById(R.id.fl_selected);
        llMain = mView.findViewById(R.id.ll_main);
        llLike = mView.findViewById(R.id.ll_like);
        llComment = mView.findViewById(R.id.ll_comment);
        llProfile = mView.findViewById(R.id.ll_profile);
        ivProfileImage = mView.findViewById(R.id.iv_user_image);
        ivLike = mView.findViewById(R.id.iv_like);
        ivShare = mView.findViewById(R.id.iv_share);
        ivPostImage = mView.findViewById(R.id.iv_tweet_image);
        ivUploadImage = mView.findViewById(R.id.iv_upload_image);
        ivDelete = mView.findViewById(R.id.iv_delete);
        ivSelectedImage = mView.findViewById(R.id.iv_selected_image);
        ivSend = mView.findViewById(R.id.iv_send_image);
        tvName = mView.findViewById(R.id.tv_name);
        tvUserName = mView.findViewById(R.id.tv_user_name);
        tvTime = mView.findViewById(R.id.tv_time);
        tvLikeCount = mView.findViewById(R.id.tv_likes_count);
        tvCommentCount = mView.findViewById(R.id.tv_comment_count);
        tvDescription = mView.findViewById(R.id.tv_description);
        cardViewTweet = mView.findViewById(R.id.card_view_tweet);
        etComment = mView.findViewById(R.id.et_comment);
        recyclerComments = mView.findViewById(R.id.recycler_comments);
        flow = mView.findViewById(R.id.flow);
        swipeToRefresh = mView.findViewById(R.id.swipeToRefresh);
        nestedScrollView = mView.findViewById(R.id.nested_scroll);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerComments.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(TweetDetailFragment.this, arrComment);
        recyclerComments.setAdapter(commentAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerComments, false);
    }

    @Override
    protected void bindControls() {

        llLike.setOnClickListener(this);
        llComment.setOnClickListener(this);
        llProfile.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivUploadImage.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivSend.setOnClickListener(this);

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(etComment.getText().toString().trim().length()>0){
                   ivSend.setImageResource(R.drawable.send_blue);
               }else{
                   ivSend.setImageResource(R.drawable.send_icon);
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrComment.clear();
                last_id = "0";
                tweetData = true;
                getTweet();
                swipeToRefresh.setRefreshing(false);
            }
        });

//        if (arrComment.size() == 0)
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
        switch (v.getId()) {
            case R.id.ll_like:
                likeUnlike();
                break;
            case R.id.iv_delete:
                flSelected.setVisibility(View.GONE);
                ivSelectedImage.setImageDrawable(null);
                file = null;
                break;
            case R.id.iv_upload_image:
                permissionCheck();
                break;
            case R.id.ll_profile:
                ((HomeActivity)getActivity()).replaceFragment(ProfileFragment.newInstance(tweetDetailPojo.getData().getUserDetail().getId()));
                break;
            case R.id.iv_send_image:
                if (!TextUtils.isEmpty(etComment.getText().toString().trim())) {
                    networkCalls.NetworkAPICall(WebConstants.COMMENT_POST_URL, true);
                } else {
                    snackBar(getString(R.string.please_type_a_comment));
                }
                break;
            case R.id.iv_share:
                showProgressDialog();
                Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://www.google.co.in/?id="+tweet_id+"&type="+ Constant.TWEET))
                        .setDynamicLinkDomain("giacomo.page.link")
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        .buildShortDynamicLink()
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                cancelProgressDialog();
                                if (task.isSuccessful()) {
                                    // Short link created
                                    Uri shortLink = task.getResult().getShortLink();
                                    CommonUtil.sendLink(getActivity(),getString(R.string.Social_awareness),AppController.getManager().getName()+getString(R.string.shared_the_tweet)+shortLink.toString());

                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.link_could_not_be_generated), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
            Call<TweetDetailPojo> call = Service.getTweetsDetail(AppController.getManager().getId(), tweet_id, last_id);
            call.enqueue(new Callback<TweetDetailPojo>() {
                @Override
                public void onResponse(Call<TweetDetailPojo> call, Response<TweetDetailPojo> response) {
                    cancelProgressDialog();
                    tweetDetailPojo = response.body();
                    if (tweetDetailPojo.getStatus()) {
//                        if (tweetData) {
                            setData(tweetDetailPojo);
                            tweetData = true;
//                        }

                        arrComment.addAll(tweetDetailPojo.getData().getComments());
                        commentAdapter.notifyDataSetChanged();
                        last_id = String.valueOf(arrComment.size());
                    }
                    isfetching = false;
                }

                @Override
                public void onFailure(Call<TweetDetailPojo> call, Throwable t) {
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

    private void setData(TweetDetailPojo tweetDetailPojo) {
        if (!TextUtils.isEmpty(tweetDetailPojo.getData().getUserDetail().getProfilePicture())) {
            Glide.with(getActivity()).load(tweetDetailPojo.getData().getUserDetail().getProfilePicture()).placeholder(R.drawable.default_user_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivProfileImage);
        } else {
            ivProfileImage.setImageResource(R.drawable.default_user_icon);
        }

        tvName.setText(tweetDetailPojo.getData().getUserDetail().getName());
        tvUserName.setText(tweetDetailPojo.getData().getUserDetail().getUserName());

        ////////////Tweet Detail//////////////
        likes = tweetDetailPojo.getData().getPostDetail().getLikes();
        if (!TextUtils.isEmpty(tweetDetailPojo.getData().getPostDetail().getPhoto())) {
            cardViewTweet.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(tweetDetailPojo.getData().getPostDetail().getPhoto()).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivPostImage);
        } else {
            cardViewTweet.setVisibility(View.GONE);
        }

        Long time = tweetDetailPojo.getData().getPostDetail().getCreatedDate();


        if (tweetDetailPojo.getData().getPostDetail().getIsLikes().equals("0")) {
            ivLike.setImageResource(R.drawable.unlike);
        } else {
            ivLike.setImageResource(R.drawable.liked);
        }
        tvTime.setText(DateUtils.getRelativeTimeSpanString(time).equals(getString(R.string.minutes_ago)) ? getString(R.string.just_now ): DateUtils.getRelativeTimeSpanString(time));
        tvDescription.setText(tweetDetailPojo.getData().getPostDetail().getPost());
        tvCommentCount.setText(String.valueOf(tweetDetailPojo.getData().getPostDetail().getComments()));
        tvLikeCount.setText(String.valueOf(tweetDetailPojo.getData().getPostDetail().getLikes()));

        if(tweetDetailPojo.getData().getPostDetail().getTag().size()!=0) {
            new TagAdapter(getActivity(),tweetDetailPojo.getData().getPostDetail().getTag(),flow);
            flow.setVisibility(View.VISIBLE);
        }else {
            flow.setVisibility(View.GONE);
        }

    }

    private void likeUnlike() {
        if (CommonUtil.isConnectingToInternet(getActivity())) {
            showProgressDialog();
            WebInterface Service = AppController.getRetrofitInstance().create(WebInterface.class);
            Call<LikeUnlikePojo> call = Service.tweetLikeUnlike(AppController.getManager().getId(), tweetDetailPojo.getData().getPostDetail().getId());
            call.enqueue(new Callback<LikeUnlikePojo>() {
                @Override
                public void onResponse(Call<LikeUnlikePojo> call, Response<LikeUnlikePojo> response) {
                    cancelProgressDialog();
                    LikeUnlikePojo likeUnlikePojo = response.body();
                    if (likeUnlikePojo.getStatus()) {
                        if (likeUnlikePojo.getLikeStatus().equals("0")) {
                            ivLike.setImageResource(R.drawable.unlike);
                            likes = likes - 1;
                        } else {
                            ivLike.setImageResource(R.drawable.liked);
                            likes = likes + 1;
                        }
                        tvLikeCount.setText(String.valueOf(likes));
                    } else {
                        snackBar(likeUnlikePojo.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<LikeUnlikePojo> call, Throwable t) {
                    cancelProgressDialog();
                    snackBar(t.getMessage());
                }
            });

        } else {
            snackBar(getString(R.string.internet_connection));
        }
    }

    @Override
    public Builders.Any.B getAPIB(String ApiName) {
        Builders.Any.B ion = null;
        switch (ApiName) {
            case WebConstants.COMMENT_POST_URL:
                ion = (Builders.Any.B) Ion.with(getActivity()).load(WebConstants.BASE_URL + WebConstants.COMMENT_POST_URL).
                        setMultipartParameter(Constant.USER_ID, AppController.getManager().getId()).
                        setMultipartParameter(Constant.POST_ID, tweet_id).
                        setMultipartParameter(Constant.COMMENT, etComment.getText().toString().trim());
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
            case WebConstants.COMMENT_POST_URL:
                try {
                    if (Result.optBoolean(Constant.STATUS)) {
//                        snackBar(Result.getString(Constant.MESSAGE));
                        etComment.setText("");
                        file=null;
                        ivSelectedImage.setImageDrawable(null);
                        flSelected.setVisibility(View.GONE);
                        getTweet();
//                        if (tweetDetailPojo.getDataTagSearch().getComments().size() <= 4) {
//
//                        } else {
//                           nestedScrollView.smoothScrollTo(0,0);
//                        }
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

        ivSelectedImage.setImageBitmap(takeImageClass.StringToBitMap(str));
        file = new File(str);
        flSelected.setVisibility(View.VISIBLE);
    }


}
