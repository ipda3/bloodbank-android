
package com.ipd3.tech.bloodBank.code.data.model.auth.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.GeneralResponseData;

public class UserData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("donation_last_date")
    @Expose
    private String donationLastDate;

    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("pin_code")
    @Expose
    private String pinCode;

    @SerializedName("blood_type_id")
    @Expose
    private String bloodTypeId;
    @SerializedName("city")
    @Expose
    private GeneralResponseData city;
    @SerializedName("blood_type")
    @Expose
    private GeneralResponseData bloodType;

    private String apiToken = "";

    public UserData() {

    }

    public UserData(int id, String name, String email, String birthDate, String cityId, String phone,
                    String donationLastDate, String bloodTypeId, String pinCode, String apiToken, GeneralResponseData bloodType, GeneralResponseData city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.cityId = cityId;
        this.phone = phone;
        this.donationLastDate = donationLastDate;
        this.bloodTypeId = bloodTypeId;
        this.pinCode = pinCode;
        this.apiToken = apiToken;
        this.bloodType = bloodType;
        this.city = city;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDonationLastDate() {
        return donationLastDate;
    }

    public void setDonationLastDate(String donationLastDate) {
        this.donationLastDate = donationLastDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getBloodTypeId() {
        return bloodTypeId;
    }

    public void setBloodTypeId(String bloodTypeId) {
        this.bloodTypeId = bloodTypeId;
    }

    public GeneralResponseData getCity() {
        return city;
    }

    public void setCity(GeneralResponseData city) {
        this.city = city;
    }

    public GeneralResponseData getBloodType() {
        return bloodType;
    }

    public void setBloodType(GeneralResponseData bloodType) {
        this.bloodType = bloodType;
    }
}
