
package com.gaicomo.app.Tweet.model.tweetTagSearch;

import com.gaicomo.app.Tweet.model.tweetPojo.PostDetail;
import com.gaicomo.app.Tweet.model.tweetPojo.UserDetail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

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
