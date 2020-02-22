
package com.gaicomo.app.Tweet.model.tweetDatailPojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("user_detail")
    @Expose
    private UserDetail userDetail;
    @SerializedName("post_detail")
    @Expose
    private PostDetail postDetail;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public PostDetail getPostDetail() {
        return postDetail;
    }

    public void setPostDetail(PostDetail postDetail) {
        this.postDetail = postDetail;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}
