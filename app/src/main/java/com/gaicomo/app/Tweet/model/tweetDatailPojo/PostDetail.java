
package com.gaicomo.app.Tweet.model.tweetDatailPojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostDetail {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("post")
    @Expose
    private String post;
    @SerializedName("tag")
    @Expose
    private List<String> tag = null;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("comments")
    @Expose
    private Integer comments;
    @SerializedName("created_date")
    @Expose
    private Long createdDate;
    @SerializedName("update_date")
    @Expose
    private String updateDate;

    @SerializedName("is_liked")
    @Expose
    private String isLikes;
    public String getIsLikes() {
        return isLikes;
    }

    public void setIsLikes(String isLikes) {
        this.isLikes = isLikes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

}
