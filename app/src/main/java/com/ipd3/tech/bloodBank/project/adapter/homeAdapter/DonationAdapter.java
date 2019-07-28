package com.ipd3.tech.bloodBank.project.adapter.homeAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.donation.donationDetails.DonationData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ipd3.tech.bloodBank.project.data.api.RetrofitClient.getClient;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.getDonation;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ApiServices apiServices;
    private UserData userData;

    private List<DonationData> donationData = new ArrayList<>();

    public DonationAdapter(Activity activity, Context context, List<DonationData> donationData) {
        this.context = context;
        this.activity = activity;
        this.donationData = donationData;
        apiServices = getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(activity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        setData(holder, position);
        setAction(holder, position);

    }

    @SuppressLint("SetTextI18n")
    private void setData(ViewHolder holder, int position) {
        try {

            holder.DonationAdapterTvBloodType.setText(donationData.get(position).getBloodType().getName());
            holder.DonationAdapterTvPatientName.setText("اسم الحالة:" + donationData.get(position).getPatientName());
            holder.DonationAdapterTvHospitalName.setText("مستشفى :" + donationData.get(position).getHospitalName());
            holder.DonationAdapterTvCityName.setText("المدينة:" + donationData.get(position).getCity().getName());

        } catch (Exception e) {

        }

    }

    private void setAction(ViewHolder holder, int position) {

        try {

            holder.DonationAdapterBtnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDonation(activity, apiServices, String.valueOf(donationData.get(position).getId()), userData.getApiToken());
                }
            });

            holder.DonationAdapterRlCallContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = "tel:" + donationData.get(position).getPhone();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    context.startActivity(callIntent);
                }
            });

        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return donationData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.Donation_adapter_tv_blood_type)
        TextView DonationAdapterTvBloodType;
        @BindView(R.id.Donation_adapter_Btn_Details)
        Button DonationAdapterBtnDetails;
        @BindView(R.id.Donation_adapter_rl_call_container)
        LinearLayout DonationAdapterRlCallContainer;
        @BindView(R.id.Donation_adapter_tv_patient_name)
        TextView DonationAdapterTvPatientName;
        @BindView(R.id.Donation_adapter_tv_hospital_name)
        TextView DonationAdapterTvHospitalName;
        @BindView(R.id.Donation_adapter_tv_city_name)
        TextView DonationAdapterTvCityName;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);

        }

    }

}
