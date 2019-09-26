package com.ipd3.tech.bloodBank.code.ui.fragment.donation;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationRequestCreate.DonationRequestsCreate;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.cities.Cities;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.code.ui.activity.map.MapsActivity;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.articlesAndRequests.DonationRequestsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.disappearKeypad;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.code.ui.activity.map.MapsActivity.hospital_address;
import static com.ipd3.tech.bloodBank.code.ui.activity.map.MapsActivity.latitude;
import static com.ipd3.tech.bloodBank.code.ui.activity.map.MapsActivity.longitude;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateDonationRequestFragment extends BaseFragment {

    @BindView(R.id.create_donation_requests_fragment_et_name)
    EditText createDonationRequestsFragmentEtName;
    @BindView(R.id.create_donation_requests_fragment_et_age)
    EditText createDonationRequestsFragmentEtAge;
    @BindView(R.id.create_donation_requests_fragment_sp_blood_type)
    Spinner createDonationRequestsFragmentSpBloodType;
    @BindView(R.id.create_donation_requests_fragment_et_numbers)
    EditText createDonationRequestsFragmentEtNumbers;
    @BindView(R.id.create_donation_requests_fragment_et_hospital_name)
    EditText createDonationRequestsFragmentEtHospitalName;
    @BindView(R.id.create_donation_requests_fragment_sp_government)
    Spinner createDonationRequestsFragmentSpGovernment;
    @BindView(R.id.create_donation_requests_fragment_sp_city)
    Spinner createDonationRequestsFragmentSpCity;
    @BindView(R.id.create_donation_requests_fragment_et_hospital_address)
    EditText createDonationRequestsFragmentEtHospitalAddress;
    @BindView(R.id.create_donation_requests_fragment_et_phone)
    EditText createDonationRequestsFragmentEtPhone;
    @BindView(R.id.create_donation_requests_fragment_et_notes)
    EditText createDonationRequestsFragmentEtNotes;
    @BindView(R.id.create_donation_requests_fragment_Relative_City)
    RelativeLayout createDonationRequestsFragmentRelativeCity;
    Unbinder unbinder;

    private ApiServices apiServices;
    private List<String> GovernoratesTxt = new ArrayList<>();
    private List<Integer> GovernoratesId = new ArrayList<>();

    private List<String> citiesTxt = new ArrayList<>();
    private List<Integer> citiesId = new ArrayList<>();

    private List<String> BloodTypesTxt = new ArrayList<>();
    private List<Integer> BloodTypesId = new ArrayList<>();

    private int city_id = 0;
    private int blood_type_id = 0;
    private UserData userData;
    public DonationRequestsFragment donationRequestsFragment;

    public CreateDonationRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_donation_requests, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());

        getGovernorates();
        getBloodTypes();

        createDonationRequestsFragmentEtHospitalAddress.setText("");

        return view;

    }

    private void getGovernorates() {
        apiServices.getGovernorates().enqueue(new Callback<Governorates>() {
            @Override
            public void onResponse(Call<Governorates> call, Response<Governorates> response) {
                try {
                    if (response.body().getStatus() == 1) {

                        GovernoratesTxt.add(getString(R.string.select_govarnment));
                        GovernoratesId.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            GovernoratesTxt.add(response.body().getData().get(i).getName());
                            GovernoratesId.add(response.body().getData().get(i).getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, GovernoratesTxt);

                        createDonationRequestsFragmentSpGovernment.setAdapter(adapter);

                        createDonationRequestsFragmentSpGovernment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i != 0) {
                                    getCities(GovernoratesId.get(i));

                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    } else {

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Governorates> call, Throwable t) {

            }
        });
    }

    private void getCities(int i) {
        HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));
        apiServices.getCities(i).enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                try {
                    dismissProgressDialog();
                    if (response.body().getStatus() == 1) {
                        createDonationRequestsFragmentRelativeCity.setVisibility(View.VISIBLE);
                        citiesTxt = new ArrayList<>();
                        citiesId = new ArrayList<>();
                        citiesTxt.add(getString(R.string.select_city));
                        citiesId.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            citiesTxt.add(response.body().getData().get(i).getName());
                            citiesId.add(response.body().getData().get(i).getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, citiesTxt);

                        createDonationRequestsFragmentSpCity.setAdapter(adapter);

                        createDonationRequestsFragmentSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i != 0) {
                                    city_id = citiesId.get(i);
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    } else {

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void getBloodTypes() {
        apiServices.getBloods().enqueue(new Callback<BloodTypes>() {
            @Override
            public void onResponse(Call<BloodTypes> call, Response<BloodTypes> response) {
                try {

                    if (response.body().getStatus() == 1) {

                        BloodTypesTxt.add(getString(R.string.select_blood));
                        BloodTypesId.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            BloodTypesTxt.add(response.body().getData().get(i).getName());
                            BloodTypesId.add(response.body().getData().get(i).getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, BloodTypesTxt);

                        createDonationRequestsFragmentSpBloodType.setAdapter(adapter);

                        createDonationRequestsFragmentSpBloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i != 0) {
                                    blood_type_id = BloodTypesId.get(i);
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<BloodTypes> call, Throwable t) {

            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.create_donation_requests_fragment_btn_create_request, R.id.create_donation_requests_fragment_Iv_open_map, R.id.create_donation_requests_fragment_rl_sub_view})
    public void onViewClicked(View view) {
        disappearKeypad(getActivity(), view);
        switch (view.getId()) {
            case R.id.create_donation_requests_fragment_btn_create_request:
                sendRequest();
                break;
            case R.id.create_donation_requests_fragment_Iv_open_map:

                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);

                break;
            case R.id.create_donation_requests_fragment_rl_sub_view:
                disappearKeypad(getActivity(), view);
                break;

        }
    }

    private void sendRequest() {

        String patient_name = createDonationRequestsFragmentEtName.getText().toString().trim();
        String patient_age = createDonationRequestsFragmentEtAge.getText().toString().trim();
        String bags_num = createDonationRequestsFragmentEtNumbers.getText().toString().trim();
        String hospital_name = createDonationRequestsFragmentEtHospitalName.getText().toString().trim();
        String phone = createDonationRequestsFragmentEtPhone.getText().toString().trim();
        String notes = createDonationRequestsFragmentEtNotes.getText().toString().trim();
        String hospitalAddress = createDonationRequestsFragmentEtHospitalAddress.getText().toString().trim();

        if (hospitalAddress.isEmpty()) {
            createDonationRequestsFragmentEtHospitalAddress.setText(hospital_address);
            createDonationRequestsFragmentEtHospitalAddress.requestFocus();

        }
        if (patient_name.isEmpty()) {
            createDonationRequestsFragmentEtName.requestFocus();

            customToast(getActivity(), getResources().getString(R.string.enterPatientName), true);
            return;
        }
        if (patient_age.isEmpty()) {
            createDonationRequestsFragmentEtAge.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPatientAge), true);
            return;
        }
        if (blood_type_id == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_blood_bank), true);

            return;
        }
        if (bags_num.isEmpty()) {
            createDonationRequestsFragmentEtNumbers.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterbagsNumber), true);


            return;
        }
        if (hospital_name.isEmpty()) {
            createDonationRequestsFragmentEtHospitalName.requestFocus();

            customToast(getActivity(), getResources().getString(R.string.enterHosName), true);
            return;
        }

        if (createDonationRequestsFragmentSpGovernment.getSelectedItemPosition() == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_governrate), true);
            return;
        }
        if (city_id == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_city_), true);
            return;
        }

        if (hospitalAddress.isEmpty()) {
            customToast(getActivity(), getResources().getString(R.string.enterhospitaladdress), true);

        }

        if (phone.isEmpty()) {
            createDonationRequestsFragmentEtPhone.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPhonee), true);
            return;
        }
        if (phone.length() != 11) {
            createDonationRequestsFragmentEtPhone.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.passwordLengh), true);
            return;
        }

        if (notes.isEmpty()) {

            createDonationRequestsFragmentEtNotes.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterNotes), true);
            return;
        }
        if (InternetState.isConnected(getActivity())) {
            HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));
            apiServices.getDonationRequestCreate(userData.getApiToken(), patient_name, patient_age, blood_type_id, bags_num, hospital_name, hospitalAddress, city_id, phone, notes, latitude, longitude).enqueue(new Callback<DonationRequestsCreate>() {
                @Override
                public void onResponse(Call<DonationRequestsCreate> call, Response<DonationRequestsCreate> response) {
                    dismissProgressDialog();

                    try {
                        if (response.body().getStatus() == 1) {
                            donationRequestsFragment.donationList.add(0, response.body().getData());
                            donationRequestsFragment.donationAdapter.notifyDataSetChanged();
                            BaseActivity activity = (BaseActivity) getActivity();
                            activity.superBackPressed();
                            Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();

                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);
                        }
                    } catch (Exception e) {

                        Log.i(TAG, "onResponse: ");

                    }


                }

                @Override
                public void onFailure(Call<DonationRequestsCreate> call, Throwable t) {
                    dismissProgressDialog();

                    customToast(getActivity(), getResources().getString(R.string.error), true);
                }
            });


        } else {
            dismissProgressDialog();
            customToast(getActivity(), getResources().getString(R.string.offline), true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        createDonationRequestsFragmentEtHospitalAddress.setText(hospital_address);
    }

}

