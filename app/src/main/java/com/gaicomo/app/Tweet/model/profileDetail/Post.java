
package com.gaicomo.app.Tweet.model.profileDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("post_detail")
    @Expose
    private PostDetail postDetail;

    public PostDetail getPostDetail() {
        return postDetail;
    }

    public void setPostDetail(PostDetail postDetail) {
        this.postDetail = postDetail;
    }

}
