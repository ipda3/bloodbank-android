package com.ipd3.tech.bloodBank.project.ui.fragment.userCycle.forgetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.auth.newPassword.NewPassword;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;
import com.ipd3.tech.bloodBank.project.ui.activity.homeCycle.HomeNavigationActivity;
import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.userCycle.LoginFragment;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.customToast;

public class ChangePasswordFragment extends BaseFragment {

    @BindView(R.id.change_password_fragment_et_check)
    EditText changePasswordFragmentEtCheck;
    @BindView(R.id.change_password_fragment_et_new_password)
    EditText changePasswordFragmentEtNewPassword;
    @BindView(R.id.change_password_fragment_et_confirmation_password)
    EditText changePasswordFragmentEtConfirmationPassword;
    Unbinder unbinder;

    private UserData userData;
    private ApiServices apiServices;
    public String phone;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HelperMethod.changeLang(getActivity(), "ar");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.stat_bar_gray), 10);
        setUpActivity();
        baseActivity.baseFragment = this;

        unbinder = ButterKnife.bind(this, view);
        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.change_password_fragment_btn_change_password, R.id.change_password_fragment_ll_sub_view})
    public void onViewClicked(View view) {
        HelperMethod.disappearKeypad(getActivity(), view);

        switch (view.getId()) {
            case R.id.change_password_fragment_btn_change_password:

                changePassword();

                break;
        }
    }

    public void changePassword() {

        String pin_code = changePasswordFragmentEtCheck.getText().toString().trim();
        String password = changePasswordFragmentEtNewPassword.getText().toString().trim();
        String password_confirmation = changePasswordFragmentEtConfirmationPassword.getText().toString().trim();
        HelperMethod.showProgressDialog(getActivity(), "");


        if (password.isEmpty()) {
            changePasswordFragmentEtNewPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPasswordd));
            return;
        }

        if (password.length() < 2) {
            changePasswordFragmentEtNewPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.passwordWeak));
            return;
        }

        if (password_confirmation.isEmpty() || !password_confirmation.equals(password)) {
            changePasswordFragmentEtConfirmationPassword.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPasswordAgain));
            return;
        }

        if (InternetState.isConnected(getActivity())) {
            HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));
            apiServices.getNewPassword(pin_code, password, password_confirmation, phone).enqueue(new Callback<NewPassword>() {
                @Override
                public void onResponse(Call<NewPassword> call, Response<NewPassword> response) {
                    try {
                        HelperMethod.dismissProgressDialog();
                        customToast(getActivity(), response.body().getMsg());

                        if (response.body().getStatus() == 1) {
                            SharedPreferencesManger.saveUserData(getActivity(), userData);

                            Intent intent = new Intent(getActivity(), HomeNavigationActivity.class);
                            startActivity(intent);

                        }
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<NewPassword> call, Throwable t) {
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
        LoginFragment loginFragment = new LoginFragment();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.frame, loginFragment);
    }
}
