package com.gaicomo.app.webutils;

import com.gaicomo.app.LoginModule.model.Login.LoginPojo;
import com.gaicomo.app.Tweet.model.likeUnlikePojo.LikeUnlikePojo;
import com.gaicomo.app.Tweet.model.profileDetail.ProfileDetailPojo;
import com.gaicomo.app.Tweet.model.tagPojo.TagListPojo;
import com.gaicomo.app.Tweet.model.tweetDatailPojo.TweetDetailPojo;
import com.gaicomo.app.Tweet.model.tweetPojo.TweetListingPojo;
import com.gaicomo.app.Tweet.model.tweetTagSearch.TweetTagSearchPojo;
import com.gaicomo.app.common.model.listpojo.ListPojo;
import com.gaicomo.app.common.model.parsedList.ParsedPojo;
import com.gaicomo.app.utils.CommonPojo;
import com.gaicomo.app.utils.Constant;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface WebInterface {


    @GET
    @Streaming
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    @FormUrlEncoded
    @POST(WebConstants.LOGIN_URL)

    Call<LoginPojo> login(@Field(Constant.EMAIL) String eamil, @Field(Constant.Password) String password,
                          @Field(Constant.Device_Type) String type, @Field(Constant.Device_Token) String token);

    @FormUrlEncoded
    @POST(WebConstants.SIGNUP_URL)
    Call<LoginPojo> signup(@Field(Constant.NAME) String name, @Field(Constant.EMAIL) String email
            ,@Field(Constant.Country_Code) String Country_Code,@Field(Constant.Mobile) String mobile,@Field(Constant.ADDRESS) String address,
                           @Field(Constant.Password) String password,
                           @Field(Constant.Device_Type) String type, @Field(Constant.Device_Token) String token
            , @Field(Constant.Login_Type) String loginType);

    @FormUrlEncoded
    @POST(WebConstants.FORGOTPASSWORD_URL)
    Call<CommonPojo> forgotPassword(@Field(Constant.EMAIL) String eamil, @Field(Constant.CODE) String link);

    @FormUrlEncoded
    @POST(WebConstants.UPDATE_URL)
    Call<CommonPojo> resetPassword(@Field(Constant.EMAIL) String eamil, @Field(Constant.Password) String password);

    @FormUrlEncoded
    @POST(WebConstants.TWEET_LISTING_URL)
    Call<TweetListingPojo> getTweets(@Field(Constant.USER_ID) String user_id,@Field(Constant.LAST_POST_ID) String id,@Field(Constant.SEARCH_kEY) String search);

    @FormUrlEncoded
    @POST(WebConstants.TWEET_DETAIL_URL)
    Call<TweetDetailPojo> getTweetsDetail(@Field(Constant.USER_ID) String user_id,@Field(Constant.POST_ID) String post_id, @Field(Constant.LAST_COMMENT_ID) String count);

    @FormUrlEncoded
    @POST(WebConstants.TWEET_LIKE_DISLIKE_URL)
    Call<LikeUnlikePojo> tweetLikeUnlike(@Field(Constant.USER_ID) String user_id,@Field(Constant.POST_ID) String post_id);

    @FormUrlEncoded
    @POST(WebConstants.COMMENT_LIKE_DISLIKE_URL)
    Call<LikeUnlikePojo> commentLikeUnlike(@Field(Constant.USER_ID) String user_id,@Field(Constant.COMMENT_ID) String comment_id);

    @FormUrlEncoded
    @POST(WebConstants.TWEET_TAG_SEARCH_URL)
    Call<TweetTagSearchPojo> tagSearch(@Field(Constant.USER_ID) String user_id,@Field(Constant.TAG) String tag_id, @Field(Constant.LAST_COUNT_VALUE) String count);

    @GET(WebConstants.TAG_LIST_URL)
    Call<TagListPojo> tagList();

    @FormUrlEncoded
    @POST(WebConstants.USER_PROFILE_URL)
    Call<ProfileDetailPojo> getUserProfile(@Field(Constant.USER_ID) String user_id, @Field(Constant.LAST_COUNT_VALUE) String count);

    @FormUrlEncoded
    @POST(WebConstants.PARSED_DATA_URL)
    Call<ParsedPojo> parsedData(@Field(Constant.CATEGORY_ID) String id,@Field(Constant.ROOT_ID) String root_id);

    @FormUrlEncoded
    @POST(WebConstants.LISTING_URL)
    Call<ListPojo> Listing(@Field(Constant.CATEGORY_ID) String id, @Field(Constant.LAST_COUNT_VALUE) String count,@Field(Constant.SEARCH_kEY) String search);

    @FormUrlEncoded
    @POST(WebConstants.SUB_LISTING_URL)
    Call<ListPojo> subListing(@Field(Constant.ROOT_ID) String root_id,@Field(Constant.CATEGORY_ID) String id, @Field(Constant.LAST_COUNT_VALUE) String count,@Field(Constant.SEARCH_kEY) String search);

}