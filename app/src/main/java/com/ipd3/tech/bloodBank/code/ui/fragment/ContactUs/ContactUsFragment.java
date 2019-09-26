package com.ipd3.tech.bloodBank.code.ui.fragment.ContactUs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.contactUs.ContactUs;
import com.ipd3.tech.bloodBank.code.data.model.setting.Setting;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.disappearKeypad;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.showProgressDialog;


public class ContactUsFragment extends BaseFragment {

    @BindView(R.id.fragment_call_us_tv_phone)
    TextView fragmentCallUsTvPhone;
    @BindView(R.id.fragment_call_us_tv_email)
    TextView fragmentCallUsTvEmail;
    @BindView(R.id.call_us_fragment_et_message_address)
    EditText callUsFragmentEtMessageAddress;
    @BindView(R.id.call_us_fragment_et_message_text)
    EditText callUsFragmentEtMessageText;
    Unbinder unbinder;
    @BindView(R.id.call_us_fragment_relative)
    RelativeLayout callUsFragmentRelative;

    private ApiServices apiServices;
    private UserData userData;

    public ContactUsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call_us, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();
//        homeActivity.changeUi(View.VISIBLE,View.GONE);

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());

        settings();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void settings() {
        if (InternetState.isConnected(getActivity())) {
            showProgressDialog(getActivity(), getString(R.string.waiit));

            apiServices.getSettings(userData.getApiToken()).enqueue(new Callback<Setting>() {
                @Override
                public void onResponse(Call<Setting> call, Response<Setting> response) {
                    try {
                        if (response.body().getStatus() == 1) {
                            fragmentCallUsTvPhone.setText(response.body().getData().getPhone());
                            fragmentCallUsTvEmail.setText(response.body().getData().getEmail());
                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);

                        }

                    } catch (Exception e) {

                    }
                    dismissProgressDialog();
                }

                @Override
                public void onFailure(Call<Setting> call, Throwable t) {
                    dismissProgressDialog();
                    customToast(getActivity(), getResources().getString(R.string.error), true);
                }
            });


        } else {
            dismissProgressDialog();
            customToast(getActivity(), getResources().getString(R.string.offline), true);
        }
    }


    @OnClick({R.id.fragment_call_us_iv_social_gmail,
            R.id.fragment_call_us_iv_social_what_app,
            R.id.fragment_call_us_iv_social_instgram,
            R.id.fragment_call_us_iv_social_youtube,
            R.id.fragment_call_us_iv_social_twitter,
            R.id.fragment_call_us_iv_social_facebook,
            R.id.call_us_fragment_btn_send,
            R.id.call_us_fragment_rl_sub_view,
            R.id.call_us_fragment_relative
    })
    public void onViewClicked(View view) {
        HelperMethod.disappearKeypad(getActivity(), view);
        switch (view.getId()) {
            case R.id.fragment_call_us_iv_social_gmail:
                break;
            case R.id.fragment_call_us_iv_social_what_app:
                break;
            case R.id.fragment_call_us_iv_social_instgram:
                break;
            case R.id.fragment_call_us_iv_social_youtube:
                break;
            case R.id.fragment_call_us_iv_social_twitter:
                break;
            case R.id.fragment_call_us_iv_social_facebook:
                break;
            case R.id.call_us_fragment_btn_send:
                onContactUs();
                break;
            case R.id.call_us_fragment_relative:
                disappearKeypad(getActivity(), getView());
                break;
        }
    }

    private void onContactUs() {
        String title = callUsFragmentEtMessageAddress.getText().toString().trim();
        String message = callUsFragmentEtMessageText.getText().toString().trim();

        if (title.isEmpty()) {
            customToast(getActivity(), getResources().getString(R.string.enterMessAdress), true);
            callUsFragmentEtMessageAddress.requestFocus();
            return;
        }

        if (message.isEmpty()) {
            customToast(getActivity(), getResources().getString(R.string.messageContent), true);
            callUsFragmentEtMessageText.requestFocus();
            return;
        }
        if (InternetState.isConnected(getActivity())) {
            showProgressDialog(getActivity(), getString(R.string.waiit));

            apiServices.getContactUs(title, message, userData.getApiToken()).enqueue(new Callback<ContactUs>() {
                @Override
                public void onResponse(Call<ContactUs> call, Response<ContactUs> response) {

                    dismissProgressDialog();
                    try {
                        if (response.body().getStatus() == 1) {

                            customToast(getActivity(), response.body().getMsg(), false);


                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);
                        }


                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<ContactUs> call, Throwable t) {
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
        setUpHomeActivity();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, homeActivity.articlesAndDonations);
    }

}



