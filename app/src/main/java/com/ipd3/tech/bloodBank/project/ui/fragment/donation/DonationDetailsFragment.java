package com.ipd3.tech.bloodBank.project.ui.fragment.donation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.model.donation.donationDetails.DonationData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DonationDetailsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.donation_details_fragment_tv_name)
    TextView donationDetailsFragmentTvName;
    @BindView(R.id.donation_details_fragment_tv_age)
    TextView donationDetailsFragmentTvAge;
    @BindView(R.id.donation_details_fragment_tv_blood_type)
    TextView donationDetailsFragmentTvBloodType;
    @BindView(R.id.donation_details_fragment_tv_order_numbers)
    TextView donationDetailsFragmentTvOrderNumbers;
    @BindView(R.id.donation_details_fragment_tv_hospital)
    TextView donationDetailsFragmentTvHospital;
    @BindView(R.id.donation_details_fragment_tv_hospital_address)
    TextView donationDetailsFragmentTvHospitalAddress;
    @BindView(R.id.donation_details_fragment_tv_phone_number)
    TextView donationDetailsFragmentTvPhoneNumber;
    @BindView(R.id.donation_details_fragment_tv_details)
    TextView donationDetailsFragmentTvDetails;
    @BindView(R.id.donation_details_fragment_tv_details_txt)
    TextView donationDetailsFragmentTvDetailsTxt;
    @BindView(R.id.donation_details_fragment_mv_map)
    MapView donationDetailsFragmentMvMap;
    Unbinder unbinder;

    public DonationData donationData;

    private GoogleApiClient googleApiClient;

    public DonationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        setData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        donationDetailsFragmentTvName.setText("الاسم : " + donationData.getPatientName());
        donationDetailsFragmentTvAge.setText("العمر : " + donationData.getPatientAge());
        donationDetailsFragmentTvBloodType.setText("فصيلة الدم : " + donationData.getBloodType().getName());
        donationDetailsFragmentTvOrderNumbers.setText("عدد الأكياس المطلوبة : " + donationData.getBagsNum());
        donationDetailsFragmentTvHospital.setText("المستشفى : " + donationData.getHospitalName());
        donationDetailsFragmentTvHospitalAddress.setText("عنوان المستشفى : " + donationData.getHospitalAddress());
        donationDetailsFragmentTvPhoneNumber.setText("رقم الجوال : " + donationData.getPhone());
        donationDetailsFragmentTvDetailsTxt.setText(donationData.getNotes());

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        donationDetailsFragmentMvMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions currentUserLocation = new MarkerOptions();
                currentUserLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                LatLng currentUserLatLang = new LatLng(Double.parseDouble(donationData.getLatitude()), Double.parseDouble(donationData.getLongitude()));
                currentUserLocation.position(currentUserLatLang);
                googleMap.addMarker(currentUserLocation);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 16));

                float zoom = 10;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, zoom));

            }
        });
    }

    @OnClick(R.id.donation_details_fragment_btn_calling)
    public void onViewClicked() {

        String number = "tel:" + donationData.getPhone();
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        startActivity(callIntent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MarkerOptions currentUserLocation = new MarkerOptions();
        currentUserLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

        LatLng currentUserLatLang = new LatLng(Double.parseDouble(donationData.getLatitude()), Double.parseDouble(donationData.getLongitude()));
        currentUserLocation.position(currentUserLatLang);
        googleMap.addMarker(currentUserLocation);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 16));

        float zoom = 10;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, zoom));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
