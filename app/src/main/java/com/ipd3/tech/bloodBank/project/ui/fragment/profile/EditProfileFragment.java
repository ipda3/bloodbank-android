package com.ipd3.tech.bloodBank.project.ui.fragment.profile;


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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.DateTxt;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.Login;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.cities.Cities;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;
import com.ipd3.tech.bloodBank.project.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.project.ui.activity.Navigation.NavigationActivity;
import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends BaseFragment {

    @BindView(R.id.edit_profile_fragment_et_name)
    EditText editProfileFragmentEtName;
    @BindView(R.id.edit_profile_fragment_et_email)
    EditText editProfileFragmentEtEmail;
    @BindView(R.id.edit_profile_fragment_sp_blood_type)
    Spinner editProfileFragmentSpBloodType;
    @BindView(R.id.edit_profile_fragment_tv_birthday_data)
    TextView editProfileFragmentTvBirth;
    @BindView(R.id.edit_profile_fragment_tv_last_donition_data)
    TextView editProfileFragmentTvLast;
    @BindView(R.id.edit_profile_fragment_sp_government)
    Spinner editProfileFragmentSpGovernment;
    @BindView(R.id.edit_profile_fragment_sp_city)
    Spinner editProfileFragmentSpCity;
    @BindView(R.id.edit_profile_fragment_et_phone_number)
    EditText editProfileFragmentEtPhoneNumber;
    @BindView(R.id.edit_profile_fragment_et_password)
    EditText editProfileFragmentEtPassword;
    @BindView(R.id.edit_profile_fragment_et_confirmation_password)
    EditText editProfileFragmentEtPasswordSure;
    Unbinder unbinder;

    private ApiServices apiServices;
    private DateTxt Bid;
    private DateTxt donationData;
    private List<String> GovernoratesTxt = new ArrayList<>();
    private List<Integer> GovernoratesId = new ArrayList<>();

    private List<String> citiesTxt = new ArrayList<>();
    private List<Integer> citiesId = new ArrayList<>();

    private List<String> BloodTypesTxt = new ArrayList<>();
    private List<Integer> BloodTypesId = new ArrayList<>();
    private int city_id = 0;
    private int blood_type_id = 0;

    private UserData userData;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setUpActivity();

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());
        if (InternetState.isConnected(getActivity())) {

            editProfileFragmentEtName.setHint(userData.getName());
            editProfileFragmentEtEmail.setHint(userData.getEmail());
            editProfileFragmentTvBirth.setHint(userData.getBirthDate());
            editProfileFragmentEtPhoneNumber.setHint(userData.getPhone());
            editProfileFragmentTvLast.setHint(userData.getDonationLastDate());
            editProfileFragmentEtPasswordSure.setText(SharedPreferencesManger.LoadData(getActivity(), SharedPreferencesManger.USER_PASSWORD));
            editProfileFragmentEtPassword.setText(SharedPreferencesManger.LoadData(getActivity(), SharedPreferencesManger.USER_PASSWORD));

        } else {
            HelperMethod.dismissProgressDialog();
            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
        }


        Bid = new DateTxt("01", "01", "1970", "01-01-1970");

        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calander = Calendar.getInstance();
        String cDay = mFormat.format(Double.valueOf(String.valueOf(calander.get(Calendar.DAY_OF_MONTH))));
        String cMonth = mFormat.format(Double.valueOf(String.valueOf(calander.get(Calendar.MONTH + 1))));

        String cYear = String.valueOf(calander.get(Calendar.YEAR));

        donationData = new DateTxt(cDay, cMonth, cYear, cDay + "-" + cMonth + "-" + cYear);

        getBloodTypes();
        getGovernorates();
        return view;
    }

    private void getBloodTypes() {
        apiServices.getBloods().enqueue(new Callback<BloodTypes>() {
            @Override
            public void onResponse(Call<BloodTypes> call, Response<BloodTypes> response) {
                if (response.body().getStatus() == 1) {

                    BloodTypesTxt.add("اختر فصيلة الدم");
                    BloodTypesId.add(0);

                    int pos = 0;

                    for (int i = 0; i < response.body().getData().size(); i++) {
                        BloodTypesTxt.add(response.body().getData().get(i).getName());
                        BloodTypesId.add(response.body().getData().get(i).getId());

                        if (response.body().getData().get(i).getName().equals(userData.getBloodType().getName())) {
                            pos = i + 1;
                        }

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            R.layout.spinner_item, BloodTypesTxt);

                    editProfileFragmentSpBloodType.setAdapter(adapter);

                    editProfileFragmentSpBloodType.setSelection(pos);

                    editProfileFragmentSpBloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void getGovernorates() {
        apiServices.getGovernorates().enqueue(new Callback<Governorates>() {
            @Override
            public void onResponse(Call<Governorates> call, Response<Governorates> response) {
                try {
                    if (response.body().getStatus() == 1) {

                        GovernoratesTxt.add("اختر المحافظه");
                        GovernoratesId.add(0);
                        int pos = 0;

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            GovernoratesTxt.add(response.body().getData().get(i).getName());
                            GovernoratesId.add(response.body().getData().get(i).getId());
                            if (response.body().getData().get(i).getName().equals(userData.getCity().getGovernorate().getName())) {
                                pos = i + 1;
                            }


                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, GovernoratesTxt);

                        editProfileFragmentSpGovernment.setAdapter(adapter);
                        editProfileFragmentSpGovernment.setSelection(pos);
                        getCetis(GovernoratesId.get(pos));

                        editProfileFragmentSpGovernment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                Log.d(TAG, "onFailure: ");
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
                        int pos = 0;

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            citiesTxt.add(response.body().getData().get(i).getName());
                            citiesId.add(response.body().getData().get(i).getId());
                            if (response.body().getData().get(i).getName().equals(userData.getCity().getName())) {
                                pos = i + 1;
                            }

                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, citiesTxt);

                        editProfileFragmentSpCity.setAdapter(adapter);
                        editProfileFragmentSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                        editProfileFragmentSpCity.setSelection(pos);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    @OnClick({R.id.edit_profile_fragment_tv_birthday_data, R.id.edit_profile_fragment_tv_last_donition_data, R.id.edit_profile_fragment_btn_save, R.id.edit_profile_fragment_rl_sub_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_profile_fragment_tv_birthday_data:
                HelperMethod.showCalender(getActivity(), "اختر تاريخ ميلادك", editProfileFragmentTvBirth, Bid);
                break;
            case R.id.edit_profile_fragment_tv_last_donition_data:
                HelperMethod.showCalender(getActivity(), "اختر اخر تاريخ للتبرع", editProfileFragmentTvLast, donationData);

                break;
            case R.id.edit_profile_fragment_btn_save:
                if (InternetState.isConnected(getActivity())) {

                    adjustInformation();


                } else {
                    HelperMethod.dismissProgressDialog();
                    Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit_profile_fragment_rl_sub_view:
                HelperMethod.disappearKeypad(getActivity(), view);
                break;
        }
    }

    private void adjustInformation() {

        String name = editProfileFragmentEtName.getText().toString().trim();
        if (name.isEmpty()) {
            name = editProfileFragmentEtName.getHint().toString().trim();
        }

        String email = editProfileFragmentEtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            email = editProfileFragmentEtEmail.getHint().toString().trim();
        }

        String birth_date = editProfileFragmentTvBirth.getText().toString().trim();
        if (birth_date.isEmpty()) {
            birth_date = editProfileFragmentTvBirth.getHint().toString().trim();
        }

        String phone = editProfileFragmentEtPhoneNumber.getText().toString().trim();
        if (phone.isEmpty()) {
            phone = editProfileFragmentEtPhoneNumber.getHint().toString().trim();
        }

        String donation_last_date = editProfileFragmentTvLast.getText().toString().trim();
        if (donation_last_date.isEmpty()) {
            donation_last_date = editProfileFragmentTvLast.getHint().toString().trim();
        }

        String password = editProfileFragmentEtPassword.getText().toString().trim();
        if (password.isEmpty()) {
            password = editProfileFragmentEtPassword.getHint().toString().trim();
        }

        String password_confirmation = editProfileFragmentEtPasswordSure.getText().toString().trim();
        if (password_confirmation.isEmpty()) {
            password_confirmation = editProfileFragmentEtPasswordSure.getHint().toString().trim();
        }

        if (city_id == 0) {
            return;
        } else {
            userData.setCityId(String.valueOf(citiesId.get(editProfileFragmentSpCity.getSelectedItemPosition())));
            userData.getCity().setId((citiesId.get(editProfileFragmentSpCity.getSelectedItemPosition())));
            userData.getCity().setName((citiesTxt.get(editProfileFragmentSpCity.getSelectedItemPosition())));
        }

        if (blood_type_id == 0) {
            return;
        } else {
            userData.setBloodTypeId(String.valueOf(BloodTypesId.get(editProfileFragmentSpBloodType.getSelectedItemPosition())));
            userData.getBloodType().setId((BloodTypesId.get(editProfileFragmentSpBloodType.getSelectedItemPosition())));
            userData.getBloodType().setName((BloodTypesTxt.get(editProfileFragmentSpBloodType.getSelectedItemPosition())));
        }

        String token = userData.getApiToken();
        if (InternetState.isConnected(getActivity())) {

            apiServices.getProfile(name, email, birth_date, city_id, phone, donation_last_date, blood_type_id
                    , password, password_confirmation, userData.getApiToken()).enqueue(new Callback<Login>() {


                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if (response.body().getStatus() == 1) {
                        UserData userData = response.body().getData().getClient();
                        userData.setApiToken(response.body().getData().getApiToken());
                        SharedPreferencesManger.saveUserData(getActivity(), response.body().getData().getClient());
                        Toast.makeText(getActivity(), "تم تعديل البيانات", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), NavigationActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getActivity(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();

                }
            });


        } else {
            HelperMethod.dismissProgressDialog();
            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
        }
    }

}

