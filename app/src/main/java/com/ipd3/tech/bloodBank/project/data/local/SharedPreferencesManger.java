package com.ipd3.tech.bloodBank.project.data.local;

import android.app.Activity;
import android.content.SharedPreferences;

import com.ipd3.tech.bloodBank.project.data.model.publiceData.GeneralResponseData;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;

public class SharedPreferencesManger {

    private static SharedPreferences sharedPreferences = null;
    private static String USER_ID = "USER_ID";
    private static String USER_EMAIL = "USER_EMAIL";
    private static String USER_NAME = "USER_NAME";
    private static String USER_BID = "USER_BID";
    private static String USER_PHONE = "USER_PHONE";
    private static String USER_DLD = "USER_DLD";
    private static String USER_PIN_CODE = "USER_PIN_CODE";
    public static String USER_API_TOKEN = "USER_API_TOKEN";
    private static String USER_CITY_ID = "USER_CITY_ID";
    private static String USER_CITY_NAME = "USER_CITY_NAME";
    private static String USER_GOVERMENT_NAME = "USER_GOVERMENT_NAME";
    private static String USER_GOVERMENT_ID = "USER_GOVERMENT_ID";
    private static String USER_BLOOD_TYPE_ID = "USER_BLOOD_TYPE_ID";
    private static String USER_BLOOD_TYPE_NAME = "USER_BLOOD_TYPE_NAME";
    public static String USER_PASSWORD = "USER_PASSWORD";
    public static String REMEMBER = "REMEMBER";

    public static void setSharedPreferences(Activity activity) {
        if (sharedPreferences == null) {
            sharedPreferences = activity.getSharedPreferences(
                    "Blood", activity.MODE_PRIVATE);
        }
    }

    public static void SaveData(Activity activity, String data_Key, String data_Value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(data_Key, data_Value);
            editor.commit();
        } else {
            setSharedPreferences(activity);
        }
    }

    public static void SaveData(Activity activity, String data_Key, boolean data_Value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(data_Key, data_Value);
            editor.commit();
        } else {
            setSharedPreferences(activity);
        }
    }

    public static String LoadData(Activity activity, String data_Key) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
        } else {
            setSharedPreferences(activity);
        }

        return sharedPreferences.getString(data_Key, null);
    }

    public static boolean LoadBoolean(Activity activity, String data_Key) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
        } else {
            setSharedPreferences(activity);
        }

        return sharedPreferences.getBoolean(data_Key, false);
    }

    public static void saveUserData(Activity activity, UserData userData) {
        setSharedPreferences(activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(USER_ID, userData.getId());
            editor.putString(USER_EMAIL, userData.getEmail());
            editor.putString(USER_NAME, userData.getName());
            editor.putString(USER_BID, userData.getBirthDate());
            editor.putString(USER_PHONE, userData.getPhone());
            editor.putString(USER_DLD, userData.getDonationLastDate());
            editor.putString(USER_PIN_CODE, userData.getPinCode());
            editor.putString(USER_API_TOKEN, userData.getApiToken());
            editor.putInt(USER_CITY_ID, userData.getCity().getId());
            editor.putString(USER_CITY_NAME, userData.getCity().getName());
            editor.putString(USER_GOVERMENT_NAME, userData.getCity().getGovernorate().getName());
            editor.putInt(USER_GOVERMENT_ID, userData.getCity().getGovernorate().getId());
            editor.putInt(USER_BLOOD_TYPE_ID, userData.getBloodType().getId());
            editor.putString(USER_BLOOD_TYPE_NAME, userData.getBloodType().getName());

            editor.commit();
        } else {

        }
    }

    public static UserData loadUserData(Activity activity) {
        UserData userData = new UserData();
        setSharedPreferences(activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            int id = sharedPreferences.getInt(USER_ID, 0);
            String name = sharedPreferences.getString(USER_NAME, null);
            String email = sharedPreferences.getString(USER_EMAIL, null);
            String birthDate = sharedPreferences.getString(USER_BID, null);
            String phone = sharedPreferences.getString(USER_PHONE, null);
            String donationLastDate = sharedPreferences.getString(USER_DLD, null);
            String pinCode = sharedPreferences.getString(USER_PIN_CODE, null);
            String apiToken = sharedPreferences.getString(USER_API_TOKEN, null);

            GeneralResponseData city = new GeneralResponseData();
            city.setId(sharedPreferences.getInt(USER_CITY_ID, 0));
            city.setName(sharedPreferences.getString(USER_CITY_NAME, null));


            city.setGovernorateId(String.valueOf(sharedPreferences.getInt(USER_GOVERMENT_ID, 0)));
            GeneralResponseData governorate = new GeneralResponseData();
            governorate.setName(sharedPreferences.getString(USER_GOVERMENT_NAME, null));
            governorate.setId(sharedPreferences.getInt(USER_GOVERMENT_ID, 0));
            city.setGovernorate(governorate);

            GeneralResponseData bloodType = new GeneralResponseData();
            bloodType.setId(sharedPreferences.getInt(USER_BLOOD_TYPE_ID, 0));
            bloodType.setName(sharedPreferences.getString(USER_BLOOD_TYPE_NAME, null));


            userData = new UserData(id, name, email, birthDate, String.valueOf(city.getId()), phone,
                    donationLastDate, String.valueOf(bloodType.getId()), pinCode, apiToken, bloodType, city);

            editor.commit();
        } else {

        }
        return userData;
    }

    public static void clean(Activity activity) {
        setSharedPreferences(activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }
    }

}
