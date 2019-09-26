package com.ipd3.tech.bloodBank.code.ui.fragment.userCycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.Login;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.activity.homeCycle.HomeActivity;
import com.ipd3.tech.bloodBank.code.ui.activity.startUpCycle.SliderActivity;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger.SaveData;
import static com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger.saveUserData;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.disappearKeypad;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.showProgressDialog;

public class LoginFragment extends BaseFragment {

    @BindView(R.id.login_fragment_et_phone_number)
    EditText loginFragmentEtPhoneNumber;
    @BindView(R.id.login_fragment_et_password)
    EditText loginFragmentEtPassword;
    @BindView(R.id.login_fragment_cb_remember)
    CheckBox loginFragmentCbRemember;
    Unbinder unbinder;

    private ApiServices apiServices;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setUpActivity();
        baseActivity.baseFragment = this;

        unbinder = ButterKnife.bind(this, view);

        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.stat_bar_gray), 10);

        apiServices = RetrofitClient.getClient().create(ApiServices.class);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.login_fragment_btn_login, R.id.login_fragment_tv_forget_password, R.id.login_fragment_btn_register, R.id.login_fragment_ll_sub_view})
    public void onViewClicked(View view) {
        disappearKeypad(getActivity(), view);

        switch (view.getId()) {
            case R.id.login_fragment_tv_forget_password:

                ForgetPasswordFragment forgetPasswordFragment = new ForgetPasswordFragment();
                HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.User_Cycle_Activity_EmptyFrame, forgetPasswordFragment);

                break;
            case R.id.login_fragment_btn_login:

                userLogin();

                break;
            case R.id.login_fragment_btn_register:
                RegisterFragment register = new RegisterFragment();
                HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.User_Cycle_Activity_EmptyFrame, register);

                break;
        }
    }

    public void userLogin() {
        String Phone = loginFragmentEtPhoneNumber.getText().toString().trim();
        String Password = loginFragmentEtPassword.getText().toString().trim();

        if (Phone.isEmpty()) {
            loginFragmentEtPhoneNumber.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPhone), true);
            return;
        }

        if (Phone.length() != 11) {
            loginFragmentEtPhoneNumber.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.passwordLengh), true);
            return;
        }

        if (Password.isEmpty()) {
            loginFragmentEtPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPassword), true);
            return;
        }



        if (InternetState.isConnected(getActivity())) {
            showProgressDialog(getActivity(), getString(R.string.logging));
            apiServices.getLogin(Phone, Password).enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {

                    dismissProgressDialog();
                    try {
                        if (response.body().getStatus() == 1) {
                            dismissProgressDialog();
                            customToast(getActivity(), response.body().getMsg(), false);
                            UserData userData = response.body().getData().getClient();
                            userData.setApiToken(response.body().getData().getApiToken());
                            saveUserData(getActivity(), userData);
                            SaveData(getActivity(), SharedPreferencesManger.USER_PASSWORD, Password);
                            SaveData(getActivity(), SharedPreferencesManger.REMEMBER, loginFragmentCbRemember.isChecked());
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);
                        }

                    } catch (Exception e) {
                        dismissProgressDialog();
                        customToast(getActivity(), getResources().getString(R.string.error), true);
                    }

                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
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
    public void onBack() {
        Intent intent = new Intent(getActivity(), SliderActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}




