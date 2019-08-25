package com.ipd3.tech.bloodBank.project.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.model.DateTxt;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.donation.donationDetails.DonationDetails;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.generalResponse.GeneralResponse;
import com.ipd3.tech.bloodBank.project.ui.activity.homeCycle.HomeNavigationActivity;
import com.ipd3.tech.bloodBank.project.ui.fragment.donation.DonationDetailsFragment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelperMethod {
    private static ProgressDialog checkDialog;
    public static AlertDialog alertDialog;

    public static void replaceFragment(FragmentManager getChildFragmentManager, int id, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager.beginTransaction();
        transaction.replace(id, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void showCalender(Context context, String title, final TextView text_view_data, final DateTxt data1) {
        DatePickerDialog mDatePicker = new DatePickerDialog(context, AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat mFormat = new DecimalFormat("00", symbols);
                String data = selectedYear + "-" + mFormat.format(Double.valueOf((selectedMonth + 1))) + "-" + mFormat.format(Double.valueOf(selectedDay));
                data1.setDate_txt(data);
                data1.setDay(mFormat.format(Double.valueOf(selectedDay)));
                data1.setMonth(mFormat.format(Double.valueOf(selectedMonth + 1)));
                data1.setYear(String.valueOf(selectedYear));
                text_view_data.setText(data);
            }
        }, Integer.parseInt(data1.getYear()), Integer.parseInt(data1.getMonth()) - 1, Integer.parseInt(data1.getDay()));
        mDatePicker.setTitle(title);
        mDatePicker.show();
    }

    public static void onLoadImageFromUrl(ImageView imageView, String URl, Context context) {
        Glide.with(context)
                .load(URl)
                .into(imageView);
    }

    public static void setInitRecyclerViewAsLinearLayoutManager(Context context, RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public static void setInitRecyclerViewAsGridLayoutManager(Activity activity, RecyclerView recyclerView, GridLayoutManager gridLayoutManager, int numberOfColumns) {
        gridLayoutManager = new GridLayoutManager(activity, numberOfColumns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public static void showProgressDialog(Activity activity, String title) {
        try {

            checkDialog = new ProgressDialog(activity);
            checkDialog.setMessage(title);
            checkDialog.setIndeterminate(false);
            checkDialog.setCancelable(false);

            checkDialog.show();

        } catch (Exception e) {

        }
    }

    public static void dismissProgressDialog() {
        try {

            checkDialog.dismiss();

        } catch (Exception e) {

        }
    }

    public static void disappearKeypad(Activity activity, View v) {
        try {
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    public static void changeLang(Context context, String lang) {
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang)); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
    }

    public static void htmlReader(TextView textView, String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(s));
        }
    }

    public static void getDonation(Activity activity, ApiServices apiServices, String donationRequestId, String ApiToken) {
        showProgressDialog(activity, activity.getString(R.string.waiit));
        apiServices.getDonationDetails(ApiToken, donationRequestId).enqueue(new Callback<DonationDetails>() {
            @Override
            public void onResponse(Call<DonationDetails> call, Response<DonationDetails> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        HomeNavigationActivity navigationActivity = (HomeNavigationActivity) activity;
                        navigationActivity.setCount(false);
                        navigationActivity.donationData = response.body().getData();
                        navigationActivity.changeUiDonation();
                        DonationDetailsFragment donationDetails = new DonationDetailsFragment();
                        donationDetails.donationData = response.body().getData();
                        HelperMethod.replaceFragment(navigationActivity.getSupportFragmentManager(), R.id.Content_Frame_Replace, donationDetails);
                    }
                } catch (Exception e) {

                }
                try {
                    dismissProgressDialog();
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<DonationDetails> call, Throwable t) {

                try {
                    dismissProgressDialog();
                } catch (Exception e) {

                }

            }
        });
    }

    public static void customToast(Activity activity, String ToastTitle) {

        LayoutInflater inflater = activity.getLayoutInflater();

        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = layout.findViewById(R.id.text);
        text.setText(ToastTitle);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void removeNotificationToken(ApiServices apiServices, UserData userData) {
        apiServices.getRemoveNotificationToken(FirebaseInstanceId.getInstance().getToken(), userData.getApiToken()).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
            }
        });
    }

    public static void registerNotificationToken(ApiServices apiServices, UserData userData) {
        apiServices.getRegisterNotificationToken(FirebaseInstanceId.getInstance().getToken(), userData.getApiToken(), "android").enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

            }
        });
    }

}
