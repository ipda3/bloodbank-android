package com.ipd3.tech.bloodBank.code.ui.fragment.donation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationDetails.DonationData;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.checkWriteExternalPermission;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.onPermission;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.replaceFragment;

public class DonationDetailsFragment extends BaseFragment implements OnMapReadyCallback,
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
    public boolean fromDonation = false;

    public DonationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUpActivity();
        View view = inflater.inflate(R.layout.fragment_donation_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();
        donationDetailsFragmentMvMap.onCreate(savedInstanceState);

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

        donationDetailsFragmentTvName.setText(getString(R.string.name_) + " " + donationData.getPatientName());
        donationDetailsFragmentTvAge.setText(getString(R.string.age_) + " " + donationData.getPatientAge());
        donationDetailsFragmentTvBloodType.setText(getString(R.string.bloodtype) + ": " + donationData.getBloodType().getName());
        donationDetailsFragmentTvOrderNumbers.setText(getString(R.string.counter_order) + donationData.getBagsNum());
        donationDetailsFragmentTvHospital.setText(getString(R.string.hospital_name) + " " + donationData.getHospitalName());
        donationDetailsFragmentTvHospitalAddress.setText(getString(R.string.hospital_address) + " " + donationData.getHospitalAddress());
        donationDetailsFragmentTvPhoneNumber.setText(getString(R.string.phone_) + donationData.getPhone());
        donationDetailsFragmentTvDetailsTxt.setText(donationData.getNotes());

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        donationDetailsFragmentMvMap.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        donationDetailsFragmentMvMap.getMapAsync(this);
    }

    @OnClick(R.id.donation_details_fragment_btn_calling)
    public void onViewClicked() {

        if (checkWriteExternalPermission(getActivity())) {
            onPermission(getActivity());
            return;
        }

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

    @Override
    public void onBack() {
        if (fromDonation) {
            setUpHomeActivity();
            replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, homeActivity.articlesAndDonations);

        } else {
            super.onBack();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        donationDetailsFragmentMvMap.onLowMemory();
    }
}
