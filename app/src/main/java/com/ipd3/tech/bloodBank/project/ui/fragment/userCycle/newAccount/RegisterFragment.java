package com.ipd3.tech.bloodBank.project.ui.fragment.userCycle.newAccount;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.ipd3.tech.bloodBank.project.ui.activity.Navigation.NavigationActivity;
import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;
import com.jaeger.library.StatusBarUtil;

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

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.disappearKeypad;


public class RegisterFragment extends BaseFragment {

    @BindView(R.id.register_fragment_et_name)
    EditText registerFragmentEtName;
    @BindView(R.id.register_fragment_et_email)
    EditText registerFragmentEtEmail;
    @BindView(R.id.register_fragment_sp_government)
    Spinner registerFragmentSpGovernment;
    @BindView(R.id.register_fragment_sp_city)
    Spinner registerFragmentSpCity;
    @BindView(R.id.register_fragment_et_phone_number)
    EditText registerFragmentEtPhoneNumber;
    @BindView(R.id.register_fragment_et_password)
    EditText registerFragmentEtPassword;
    @BindView(R.id.register_fragment_et_confirmation_password)
    EditText registerFragmentEtConfirmationPassword;
    @BindView(R.id.register_fragment_tv_birthday_data)
    TextView registerFragmentTvBirthdayData;
    @BindView(R.id.register_fragment_tv_last_dontition_data)
    TextView registerFragmentTvLastDontitionData;
    @BindView(R.id.register_fragment_sp_blood_type)
    Spinner registerFragmentSpBloodType;
    Unbinder unbinder;

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
    private Activity activity;
    private ApiServices apiServices;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HelperMethod.changeLang(getActivity(), "ar");
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.stat_bar_gray), 10);

        setUpActivity();
        baseActivity.baseFragment = this;

        unbinder = ButterKnife.bind(this, view);
        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());
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
                try {
                    if (response.body().getStatus() == 1) {

                        BloodTypesTxt.add("اختر فصيلة الدم");
                        BloodTypesId.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            BloodTypesTxt.add(response.body().getData().get(i).getName());
                            BloodTypesId.add(response.body().getData().get(i).getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, BloodTypesTxt);

                        registerFragmentSpBloodType.setAdapter(adapter);

                        registerFragmentSpBloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                        GovernoratesTxt.add("اختر المحافظه");
                        GovernoratesId.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            GovernoratesTxt.add(response.body().getData().get(i).getName());
                            GovernoratesId.add(response.body().getData().get(i).getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, GovernoratesTxt);

                        registerFragmentSpGovernment.setAdapter(adapter);

                        registerFragmentSpGovernment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                        registerFragmentSpCity.setAdapter(adapter);

                        registerFragmentSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    @OnClick({R.id.register_fragment_tv_birthday_data, R.id.register_fragment_tv_last_dontition_data, R.id.register_fragment_btn_register, R.id.register_fragment_btn_register_rl_sub_view})
    public void onViewClicked(View view) {

        disappearKeypad(getActivity(), view);

        switch (view.getId()) {
            case R.id.register_fragment_tv_birthday_data:
                HelperMethod.showCalender(getActivity(), "اختر تاريخ ميلادك", registerFragmentTvBirthdayData, Bid);

                break;
            case R.id.register_fragment_tv_last_dontition_data:
                HelperMethod.showCalender(getActivity(), "اختر اخر تاريخ للتبرع", registerFragmentTvLastDontitionData, donationData);
                break;
            case R.id.register_fragment_btn_register:
                if (InternetState.isConnected(getActivity())) {
                    userRegister();
                } else {
                    Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void userRegister() {

        String name = registerFragmentEtName.getText().toString().trim();
        String email = registerFragmentEtEmail.getText().toString().trim();
        String birth_date = registerFragmentTvBirthdayData.getText().toString().trim();
        String phone = registerFragmentEtPhoneNumber.getText().toString().trim();
        String donation_last_date = registerFragmentTvLastDontitionData.getText().toString().trim();
        String password = registerFragmentEtPassword.getText().toString().trim();
        String password_confirmation = registerFragmentEtConfirmationPassword.getText().toString().trim();

        if (name.isEmpty()) {
            registerFragmentEtName.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterUserName));
            return;
        }

        if (email.isEmpty()) {
            registerFragmentEtEmail.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterEmaill));
            return;
        }

        if (birth_date.isEmpty()) {
            registerFragmentTvBirthdayData.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterBirth));
            return;
        }

        if (donation_last_date.isEmpty()) {
            registerFragmentTvLastDontitionData.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterLast));
            return;
        }

        if (phone.isEmpty()) {
            registerFragmentEtPhoneNumber.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPhoneeeee));
            return;
        }

        if (phone.length() != 11) {
            registerFragmentEtPhoneNumber.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.passwordLengh));
            return;
        }

        if (password.isEmpty()) {
            registerFragmentEtPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPasswordd));
            return;
        }

        if (password_confirmation.isEmpty() || !password_confirmation.equals(password)) {
            registerFragmentEtConfirmationPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPasswordAgain));
            return;
        }

        if (city_id == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_city));
            return;
        }

        if (blood_type_id == 0) {
            customToast(getActivity(), getResources().getString(R.string.select_blood_bank));
            return;
        }

        if (InternetState.isConnected(getActivity())) {
            apiServices.getRegister(name, email, birth_date, city_id, phone, donation_last_date, password, password_confirmation,
                    blood_type_id).enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    try {
                        if (response.body().getStatus() == 1) {
                            HelperMethod.dismissProgressDialog();
                            UserData userData = response.body().getData().getClient();
                            userData.setApiToken(response.body().getData().getApiToken());
                            SharedPreferencesManger.saveUserData(getActivity(), userData);
                            SharedPreferencesManger.SaveData(getActivity(), SharedPreferencesManger.USER_PASSWORD, password);

                            customToast(getActivity(), "تم انشاء الحساب");

                            Intent intent = new Intent(getActivity(), NavigationActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            HelperMethod.dismissProgressDialog();

                            customToast(getActivity(), response.body().getMsg());
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    HelperMethod.dismissProgressDialog();

                    customToast(getActivity(), getResources().getString(R.string.error));
                }
            });

        } else {
            HelperMethod.dismissProgressDialog();
            customToast(getActivity(), getResources().getString(R.string.offline));
        }

    }

    @Override
    public void onBack() {
        super.onBack();
    }
}