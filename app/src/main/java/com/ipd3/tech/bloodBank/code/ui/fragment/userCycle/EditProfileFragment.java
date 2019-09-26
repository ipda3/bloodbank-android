package com.ipd3.tech.bloodBank.code.ui.fragment.userCycle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.DateTxt;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.Login;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.cities.Cities;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger.LoadData;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.convertStringToDateTxtModel;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.disappearKeypad;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.showCalender;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.showProgressDialog;
import static com.ipd3.tech.bloodBank.code.helper.Validation.setEmailValidation;

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
    @BindView(R.id.edit_profile_fragment_tv_last_donation_data)
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

        setUpHomeActivity();

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());
        editProfileFragmentEtName.setText(userData.getName());
        editProfileFragmentEtEmail.setText(userData.getEmail());
        editProfileFragmentTvBirth.setText(userData.getBirthDate());
        editProfileFragmentEtPhoneNumber.setText(userData.getPhone());
        editProfileFragmentTvLast.setText(userData.getDonationLastDate());
        editProfileFragmentEtPasswordSure.setText(LoadData(getActivity(), SharedPreferencesManger.USER_PASSWORD));
        editProfileFragmentEtPassword.setText(LoadData(getActivity(), SharedPreferencesManger.USER_PASSWORD));

        Bid = convertStringToDateTxtModel(userData.getBirthDate());
        donationData = convertStringToDateTxtModel(userData.getDonationLastDate());

        getBloodTypes();
        getGovernorates();
        return view;
    }


    private void getBloodTypes() {
        apiServices.getBloods().enqueue(new Callback<BloodTypes>() {
            @Override
            public void onResponse(Call<BloodTypes> call, Response<BloodTypes> response) {
                try {

                    if (response.body().getStatus() == 1) {

                        BloodTypesTxt = new ArrayList<>();
                        BloodTypesId = new ArrayList<>();

                        BloodTypesTxt.add(getString(R.string.select_blood));
                        BloodTypesId.add(0);

                        int pos = 0;

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            BloodTypesTxt.add(response.body().getData().get(i).getName());
                            BloodTypesId.add(response.body().getData().get(i).getId());
                            if (response.body().getData().get(i).getName().equals(userData.getBloodType().getName())) {
                                pos = i + 1;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, BloodTypesTxt);

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

                } catch (Exception e) {

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

                        GovernoratesTxt = new ArrayList<>();
                        GovernoratesId = new ArrayList<>();

                        GovernoratesTxt.add(getString(R.string.select_govarnment));
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

                        citiesTxt.add(getString(R.string.select_city_));
                        citiesId.add(0);
                        int pos = 0;

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            citiesTxt.add(response.body().getData().get(i).getName());
                            citiesId.add(response.body().getData().get(i).getId());
                            if (response.body().getData().get(i).getName().equals(userData.getCity().getName())) {
                                pos = i + 1;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, citiesTxt);

                        editProfileFragmentSpCity.setAdapter(adapter);
                        editProfileFragmentSpCity.setSelection(pos);

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

    @OnClick({R.id.edit_profile_fragment_tv_birthday_data,
            R.id.edit_profile_fragment_tv_last_donation_data,
            R.id.edit_profile_fragment_btn_save,
            R.id.edit_profile_fragment_rl_sub_view})
    public void onViewClicked(View view) {
        disappearKeypad(getActivity(), view);
        switch (view.getId()) {
            case R.id.edit_profile_fragment_tv_birthday_data:
                showCalender(getActivity(), "اختر تاريخ ميلادك", editProfileFragmentTvBirth, Bid);
                break;
            case R.id.edit_profile_fragment_tv_last_donation_data:
                showCalender(getActivity(), "اختر اخر تاريخ للتبرع", editProfileFragmentTvLast, donationData);

                break;
            case R.id.edit_profile_fragment_btn_save:
                adjustInformation();
                break;
            case R.id.edit_profile_fragment_rl_sub_view:
                HelperMethod.disappearKeypad(getActivity(), view);
                break;
        }
    }

    private void adjustInformation() {

        String name = editProfileFragmentEtName.getText().toString().trim();
        String email = editProfileFragmentEtEmail.getText().toString().trim();
        String birth_date = editProfileFragmentTvBirth.getText().toString().trim();
        String phone = editProfileFragmentEtPhoneNumber.getText().toString().trim();
        String donation_last_date = editProfileFragmentTvLast.getText().toString().trim();
        String password = editProfileFragmentEtPassword.getText().toString().trim();
        String password_confirmation = editProfileFragmentEtPasswordSure.getText().toString().trim();

        if (name.equals(userData.getName()) && email.equals(userData.getEmail()) && birth_date.equals(userData.getBirthDate()) && phone.equals(userData.getPhone())
                && donation_last_date.equals(userData.getDonationLastDate()) && password.equals(LoadData(getActivity(), SharedPreferencesManger.USER_PASSWORD))
                && city_id == userData.getCity().getId() && blood_type_id == userData.getBloodType().getId()) {

            return;
        }

        if (name.isEmpty()) {
            editProfileFragmentEtName.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterUserName), true);
            return;
        }

        if (email.isEmpty()) {
            editProfileFragmentEtEmail.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterEmaill), true);
            return;
        }

        if (!setEmailValidation(getActivity(), email)) {
            editProfileFragmentEtEmail.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.invalid_Email), true);
            return;
        }
        if (editProfileFragmentSpBloodType.getSelectedItemPosition() == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_blood), true);
            return;
        }

        if (birth_date.isEmpty()) {
            editProfileFragmentTvBirth.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterBirth), true);

            return;
        }

        if (donation_last_date.isEmpty()) {
            editProfileFragmentTvLast.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterLast), true);
            return;
        }
        if (editProfileFragmentSpCity.getSelectedItemPosition() == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_city), true);
            return;
        }
        if (editProfileFragmentSpGovernment.getSelectedItemPosition() == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_governrate), true);
            return;
        }

        if (phone.isEmpty()) {
            editProfileFragmentEtPhoneNumber.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPhoneeeee), true);
            return;
        }

        if (phone.length() != 11) {
            editProfileFragmentEtPhoneNumber.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.passwordLengh), true);
            return;
        }

        if (password.isEmpty()) {
            editProfileFragmentEtPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPasswordd), true);
            return;
        }
        if (password.length() < 2) {
            editProfileFragmentEtPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.passwordWeak), true);
            return;
        }
        if (password_confirmation.isEmpty()) {
            editProfileFragmentEtPasswordSure.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPasswordAgain), true);
            return;
        }

        if (!password_confirmation.equals(password)) {
            editProfileFragmentEtPasswordSure.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.invalid_confirm_password), true);
            return;
        }

        if (city_id == 0) {
            return;
        } else {
            city_id = citiesId.get(editProfileFragmentSpCity.getSelectedItemPosition());
        }

        if (blood_type_id == 0) {
            return;
        } else {
            blood_type_id = BloodTypesId.get(editProfileFragmentSpBloodType.getSelectedItemPosition());
        }

        if (InternetState.isConnected(getActivity())) {
            showProgressDialog(getActivity(), getString(R.string.waiit));

            apiServices.getProfile(name, email, birth_date, city_id, phone, donation_last_date, blood_type_id
                    , password, password_confirmation, userData.getApiToken()).enqueue(new Callback<Login>() {

                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    try {
                        dismissProgressDialog();
                        if (response.body().getStatus() == 1) {
                            UserData newUserData = response.body().getData().getClient();
                            newUserData.setApiToken(userData.getApiToken());
                            SharedPreferencesManger.saveUserData(getActivity(), newUserData);
                            customToast(getActivity(), "تم تعديل البيانات", false);

                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);
                        }

                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    dismissProgressDialog();
                    customToast(getActivity(), getResources().getString(R.string.error), true);
                }
            });


        } else {
            customToast(getActivity(), getResources().getString(R.string.offline), true);
        }
    }

    @Override
    public void onBack() {
        setUpHomeActivity();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, homeActivity.articlesAndDonations);
    }

}

