package com.ipd3.tech.bloodBank.code.ui.fragment.userCycle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.model.auth.resetpassword.ResetPassword;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.userCycle.ChangePasswordFragment;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends BaseFragment {

    @BindView(R.id.forget_password_fragment_et_phone)
    EditText forgetPasswordFragmentEtPhone;
    Unbinder unbinder;

    private ApiServices apiServices;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HelperMethod.changeLang(getActivity(), "ar");

        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.stat_bar_gray), 10);
        setUpActivity();
        baseActivity.baseFragment = this;

        unbinder = ButterKnife.bind(this, view);

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.forget_password_fragment_btn_send, R.id.forget_password_fragment_rl_sub_view})
    public void onViewClicked(View view) {
        HelperMethod.disappearKeypad(getActivity(), view);
        switch (view.getId()) {
            case R.id.forget_password_fragment_btn_send:

                forgetPassword();

                break;
        }
    }

    public void forgetPassword() {
        String Phone = forgetPasswordFragmentEtPhone.getText().toString().trim();

        if (Phone.isEmpty()) {
            forgetPasswordFragmentEtPhone.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.enterPhone), true);
            return;
        }

        if (Phone.length() != 11) {
            forgetPasswordFragmentEtPhone.requestFocus();
            customToast(getActivity(), getResources().getString(R.string.passwordLengh), true);
            return;
        }

        if (InternetState.isConnected(getActivity())) {
            HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));
            apiServices.getResetPassword(Phone).enqueue(new Callback<ResetPassword>() {
                @Override
                public void onResponse(Call<ResetPassword> call, Response<ResetPassword> response) {
                    try {
                        if (response.body().getStatus() == 1) {
                            customToast(getActivity(), getString(R.string.check_your_email), false);
                            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                            changePasswordFragment.phone = Phone;
                            HelperMethod.replaceFragment(getFragmentManager(), R.id.User_Cycle_Activity_EmptyFrame, changePasswordFragment);

                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);
                        }
                        dismissProgressDialog();

                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<ResetPassword> call, Throwable t) {
                    customToast(getActivity(), getResources().getString(R.string.error), true);
                }
            });
        } else {
            customToast(getActivity(), getResources().getString(R.string.offline), true);
        }

    }

}

