
package com.ipd3.tech.bloodBank.code.data.model.publiceData.bloodTypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.GeneralResponseData;

import java.util.List;

public class BloodTypes {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private List<GeneralResponseData> data = null;

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

    public List<GeneralResponseData> getData() {
        return data;
    }

    public void setData(List<GeneralResponseData> data) {
        this.data = data;
    }

}
