
package com.ipd3.tech.bloodBank.code.data.model.donation.donationRequests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllDonation {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private DonationsPagination data;

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

    public DonationsPagination getData() {
        return data;
    }

    public void setData(DonationsPagination data) {
        this.data = data;
    }

}
