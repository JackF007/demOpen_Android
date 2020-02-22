
package com.gaicomo.app.Tweet.model.tweetPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TweetData {

    @SerializedName("post_detail")
    @Expose
    private PostDetail postDetail;
    @SerializedName("user_detail")
    @Expose
    private UserDetail userDetail;

    public PostDetail getPostDetail() {
        return postDetail;
    }

    public void setPostDetail(PostDetail postDetail) {
        this.postDetail = postDetail;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

}
