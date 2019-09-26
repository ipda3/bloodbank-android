package com.ipd3.tech.bloodBank.code.data.api;

import com.ipd3.tech.bloodBank.code.data.model.auth.login.Login;
import com.ipd3.tech.bloodBank.code.data.model.auth.newPassword.NewPassword;
import com.ipd3.tech.bloodBank.code.data.model.auth.resetpassword.ResetPassword;
import com.ipd3.tech.bloodBank.code.data.model.contactUs.ContactUs;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationDetails.DonationDetails;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationRequestCreate.DonationRequestsCreate;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationRequests.AllDonation;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationList.NotificationsList;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationSettings.NotificationSettings;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationsCount.NotificationsCount;
import com.ipd3.tech.bloodBank.code.data.model.post.postToggleFavourite.PostToggleFavourite;
import com.ipd3.tech.bloodBank.code.data.model.post.posts.Posts;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.categeories.Categeories;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.cities.Cities;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.generalResponse.GeneralResponse;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.code.data.model.setting.Setting;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServices {

    //Auth
    @POST("login")
    @FormUrlEncoded
    Call<Login> getLogin(@Field("phone") String phone,
                         @Field("password") String password);

    @POST("reset-password")
    @FormUrlEncoded
    Call<ResetPassword> getResetPassword(@Field("phone") String phone);

    @POST("new-password")
    @FormUrlEncoded
    Call<NewPassword> getNewPassword(@Field("pin_code") String pin_code,
                                     @Field("password") String password,
                                     @Field("password_confirmation") String password_confirmation,
                                     @Field("phone") String phone);

    @POST("signup")
    @FormUrlEncoded
    Call<Login> getRegister(@Field("name") String name,
                            @Field("email") String email,
                            @Field("birth_date") String birth_date,
                            @Field("city_id") int city_id,
                            @Field("phone") String phone,
                            @Field("donation_last_date") String donation_last_date,
                            @Field("password") String password,
                            @Field("password_confirmation") String password_confirmation,
                            @Field("blood_type_id") int blood_type_id);

    @POST("profile")
    @FormUrlEncoded
    Call<Login> getProfile(@Field("name") String name,
                           @Field("email") String email,
                           @Field("birth_date") String birth_date,
                           @Field("city_id") int city_id,
                           @Field("phone") String phone,
                           @Field("donation_last_date") String donation_last_date,
                           @Field("blood_type_id") int blood_type_id,
                           @Field("password") String password,
                           @Field("password_confirmation") String password_confirmation,
                           @Field("api_token") String api_token);

    //Posts && Favourites
    @GET("posts")
    Call<Posts> getPosts(@Query("api_token") String api_token,
                         @Query("page") int page);

    @GET("posts")
    Call<Posts> getPostFilter(@Query("api_token") String api_token,
                              @Query("keyword") String keyword,
                              @Query("page") int page,
                              @Query("category_id") int category_id);

    @GET("my-favourites")
    Call<Posts> getFavouritesList(@Query("api_token") String api_token,
                                  @Query("page") int page);

    @POST("post-toggle-favourite")
    @FormUrlEncoded
    Call<PostToggleFavourite> getPostToggleFavourite(@Field("post_id") int post_id,
                                                     @Field("api_token") String api_token);

    //Donation
    @POST("donation-request/create")
    @FormUrlEncoded
    Call<DonationRequestsCreate> getDonationRequestCreate(@Field("api_token") String api_token,
                                                          @Field("patient_name") String patient_name,
                                                          @Field("patient_age") String patient_age,
                                                          @Field("blood_type_id") int blood_type_id,
                                                          @Field("bags_num") String bags_num,
                                                          @Field("hospital_name") String hospital_name,
                                                          @Field("hospital_address") String hospital_address,
                                                          @Field("city_id") int city_id,
                                                          @Field("phone") String phone,
                                                          @Field("notes") String notes,
                                                          @Field("latitude") double latitude,
                                                          @Field("longitude") double longitude);

    @GET("donation-requests")
    Call<AllDonation> getDonationRequests(@Query("api_token") String api_token,
                                          @Query("page") int page);

    @GET("donation-requests")
    Call<AllDonation> getDonationRequestsFilter(@Query("api_token") String api_token,
                                                @Query("blood_type_id") int blood_type_id,
                                                @Query("governorate_id") int governorate_id,
                                                @Query("page") int page);

    @GET("donation-request")
    Call<DonationDetails> getDonationDetails(@Query("api_token") String api_token,
                                             @Query("donation_id") String donation_id);

    //Notification
    @GET("notifications-count")
    Call<NotificationsCount> getNotificationsCount(@Query("api_token") String api_token);

    @GET("notifications")
    Call<NotificationsList> getNotificationsList(@Query("api_token") String api_token,
                                                 @Query("page") int page);

    @POST("signup-token")
    @FormUrlEncoded
    Call<GeneralResponse> getRegisterNotificationToken(@Field("token") String token,
                                                       @Field("api_token") String api_token,
                                                       @Field("type") String platform);
    @POST("remove-token")
    @FormUrlEncoded
    Call<GeneralResponse> getRemoveNotificationToken(@Field("token") String token,
                                                     @Field("api_token") String api_token);

    @POST("notifications-settings")
    @FormUrlEncoded
    Call<NotificationSettings> setNotificationSettings(@Field("api_token") String api_token,
                                                       @Field("governorates[]") List<Integer> governorates,
                                                       @Field("blood_types[]") List<Integer> blood_types);

    @POST("notifications-settings")
    @FormUrlEncoded
    Call<NotificationSettings> getNotificationSettings(@Field("api_token") String api_tokens);


    //General Request
    @GET("governorates")
    Call<Governorates> getGovernorates();

    @GET("cities")
    Call<Cities> getCities(@Query("governorate_id") int governorate_id);

    @GET("blood-types")
    Call<BloodTypes> getBloods();

    @GET("categories")
    Call<Categeories> getCategeories();

    @GET("settings")
    Call<Setting> getSettings(@Query("api_token") String api_token);

    @POST("contact")
    @FormUrlEncoded
    Call<ContactUs> getContactUs(@Field("title") String title,
                                 @Field("message") String message,
                                 @Field("api_token") String api_token);

}
