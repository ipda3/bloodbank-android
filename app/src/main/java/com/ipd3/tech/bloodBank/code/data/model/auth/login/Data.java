
package com.ipd3.tech.bloodBank.code.data.model.auth.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("api_token")
    @Expose
    private String apiToken;
    @SerializedName("client")
    @Expose
    private UserData client;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public UserData getClient() {
        return client;
    }

    public void setClient(UserData client) {
        this.client = client;
    }

}
