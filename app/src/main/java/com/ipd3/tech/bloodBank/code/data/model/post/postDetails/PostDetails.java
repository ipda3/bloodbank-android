
package com.ipd3.tech.bloodBank.code.data.model.post.postDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ipd3.tech.bloodBank.code.data.model.post.posts.PostsData;

public class PostDetails {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private PostsData data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PostsData getData() {
        return data;
    }

    public void setData(PostsData data) {
        this.data = data;
    }

}
