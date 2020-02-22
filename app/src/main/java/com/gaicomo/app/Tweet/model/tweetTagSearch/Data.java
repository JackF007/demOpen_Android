
package com.gaicomo.app.Tweet.model.tweetTagSearch;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("tag_detail")
    @Expose
    private TagDetail tagDetail;
    @SerializedName("posts")
    @Expose
    private List<Post> posts = null;

    public TagDetail getTagDetail() {
        return tagDetail;
    }

    public void setTagDetail(TagDetail tagDetail) {
        this.tagDetail = tagDetail;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

}
