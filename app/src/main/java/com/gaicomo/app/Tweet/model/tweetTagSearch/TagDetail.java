
package com.gaicomo.app.Tweet.model.tweetTagSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TagDetail {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("description")
    @Expose
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
