package com.ipd3.tech.bloodBank.project.ui.fragment.donation;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.donation.donationRequestCreate.DonationRequestsCreate;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.cities.Cities;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;
import com.ipd3.tech.bloodBank.project.ui.activity.MapsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.project.ui.activity.MapsActivity.hospital_address;
import static com.ipd3.tech.bloodBank.project.ui.activity.MapsActivity.latitude;
import static com.ipd3.tech.bloodBank.project.ui.activity.MapsActivity.longitude;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateDonationRequestsFragment extends Fragment {


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

    public CreateDonationRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_donation_requests, container, false);
        unbinder = ButterKnife.bind(this, view);
        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());
        MapsActivity mapsActivity = new MapsActivity();
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

                        GovernoratesTxt.add("اختر المحافظه");
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
                                    getCetis(GovernoratesId.get(i));
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

    private void getCetis(int i) {
        apiServices.getCities(i).enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        citiesTxt = new ArrayList<>();
                        citiesId = new ArrayList<>();
                        citiesTxt.add("اختر المدينة");
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

            }
        });
    }

    private void getBloodTypes() {
        apiServices.getBloods().enqueue(new Callback<BloodTypes>() {
            @Override
            public void onResponse(Call<BloodTypes> call, Response<BloodTypes> response) {
                if (response.body().getStatus() == 1) {

                    BloodTypesTxt.add("اختر فصيلة الدم");
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
        switch (view.getId()) {
            case R.id.create_donation_requests_fragment_btn_create_request:
                if (InternetState.isConnected(getActivity())) {

                    sendRequest();

                } else {
                    Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.create_donation_requests_fragment_Iv_open_map:
                if (InternetState.isConnected(getActivity())) {
                    HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    startActivity(intent);

                } else {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.create_donation_requests_fragment_rl_sub_view:
                HelperMethod.disappearKeypad(getActivity(), view);
                break;

        }
    }

    public void setHosName(String hospital_name) {
        createDonationRequestsFragmentEtHospitalName.setText(hospital_name);
    }

    private void sendRequest() {
        if (city_id == 0) {
            return;
        }
        if (blood_type_id == 0) {
            return;
        }

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
            createDonationRequestsFragmentEtName.setError(getString(R.string.enterPatientName));
            createDonationRequestsFragmentEtName.requestFocus();

            return;
        }
        if (patient_age.isEmpty()) {
            createDonationRequestsFragmentEtAge.setError(getString(R.string.enterPatientAge));
            createDonationRequestsFragmentEtAge.requestFocus();

            return;
        }
        if (bags_num.isEmpty()) {
            createDonationRequestsFragmentEtNumbers.setError(getString(R.string.enterbagsNumber));
            createDonationRequestsFragmentEtNumbers.requestFocus();

            return;
        }
        if (hospital_name.isEmpty()) {
            createDonationRequestsFragmentEtHospitalName.setError(getString(R.string.enterHosName));
            createDonationRequestsFragmentEtHospitalName.requestFocus();

            return;
        }
        if (phone.isEmpty()) {
            createDonationRequestsFragmentEtPhone.setError(getString(R.string.enterPhonee));
            createDonationRequestsFragmentEtPhone.requestFocus();

            return;
        }
        if (notes.isEmpty()) {
            createDonationRequestsFragmentEtNotes.setError(getString(R.string.enterNotes));
            createDonationRequestsFragmentEtNotes.requestFocus();

            return;
        }
        if (InternetState.isConnected(getActivity())) {
            HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));
            apiServices.getDonationRequestCreate(userData.getApiToken(), patient_name, patient_age, blood_type_id, bags_num, hospital_name, hospitalAddress, city_id, phone, notes, latitude, longitude).enqueue(new Callback<DonationRequestsCreate>() {
                @Override
                public void onResponse(Call<DonationRequestsCreate> call, Response<DonationRequestsCreate> response) {
                    if (response.body().getStatus() == 1) {
                        dismissProgressDialog();
                        Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<DonationRequestsCreate> call, Throwable t) {

                }
            });


        } else {
            dismissProgressDialog();
            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        createDonationRequestsFragmentEtHospitalAddress.setText(hospital_address);
        hospital_address = "";
    }

}

