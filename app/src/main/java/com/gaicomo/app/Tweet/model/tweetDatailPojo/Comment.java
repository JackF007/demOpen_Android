
package com.gaicomo.app.Tweet.model.tweetDatailPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("comment_id")
    @Expose
    private String commentId;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("created_date")
    @Expose
    private Long createdDate;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("user_detail")
    @Expose
    private UserDetail_ userDetail;
    @SerializedName("is_liked")
    @Expose
    private String isLikes;
    public String getIsLikes() {
        return isLikes;
    }

    public void setIsLikes(String isLikes) {
        this.isLikes = isLikes;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public UserDetail_ getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail_ userDetail) {
        this.userDetail = userDetail;
    }

}
