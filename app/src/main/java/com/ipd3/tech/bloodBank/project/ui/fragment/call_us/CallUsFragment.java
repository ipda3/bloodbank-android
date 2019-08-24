package com.ipd3.tech.bloodBank.project.ui.fragment.call_us;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.contactUs.ContactUs;
import com.ipd3.tech.bloodBank.project.data.model.setting.Setting;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;
import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.showProgressDialog;


public class CallUsFragment extends BaseFragment {

    @BindView(R.id.fragment_call_us_tv_phone)
    TextView fragmentCallUsTvPhone;
    @BindView(R.id.fragment_call_us_tv_email)
    TextView fragmentCallUsTvEmail;
    @BindView(R.id.call_us_fragment_et_message_address)
    EditText callUsFragmentEtMessageAddress;
    @BindView(R.id.call_us_fragment_et_message_text)
    EditText callUsFragmentEtMessageText;
    Unbinder unbinder;

    private ApiServices apiServices;
    private UserData userData;

    public CallUsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call_us, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();
        navigationActivity.changeUi(View.VISIBLE,View.GONE);

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
                    if (response.body().getStatus() == 1) {
                        fragmentCallUsTvPhone.setText(response.body().getData().getPhone());
                        fragmentCallUsTvEmail.setText(response.body().getData().getEmail());
                    }
                    dismissProgressDialog();
                }

                @Override
                public void onFailure(Call<Setting> call, Throwable t) {
                    dismissProgressDialog();
                }
            });


        } else {
            dismissProgressDialog();
            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick({R.id.fragment_call_us_iv_social_gmail, R.id.fragment_call_us_iv_social_what_app, R.id.fragment_call_us_iv_social_instgram, R.id.fragment_call_us_iv_social_youtube
            , R.id.fragment_call_us_iv_social_twitter, R.id.fragment_call_us_iv_social_facebook, R.id.call_us_fragment_btn_send, R.id.call_us_fragment_rl_sub_view})
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
        }
    }

    private void onContactUs() {
        String title = callUsFragmentEtMessageAddress.getText().toString().trim();
        String message = callUsFragmentEtMessageText.getText().toString().trim();

        if (InternetState.isConnected(getActivity())) {
            apiServices.getContactUs(title, message, userData.getApiToken()).enqueue(new Callback<ContactUs>() {
                @Override
                public void onResponse(Call<ContactUs> call, Response<ContactUs> response) {
                    if (response.body().getStatus() == 1) {
                        callUsFragmentEtMessageAddress.setText("");
                        callUsFragmentEtMessageText.setText("");

                        if (title.isEmpty()) {
                            callUsFragmentEtMessageAddress.setError(getString(R.string.enterMessAdress));
                            callUsFragmentEtMessageAddress.requestFocus();
                        }
                        if (message.isEmpty()) {
                            callUsFragmentEtMessageText.setError(getString(R.string.messageContent));
                            callUsFragmentEtMessageText.requestFocus();
                        }
                        Toast.makeText(getActivity(), R.string.callUsMsg, Toast.LENGTH_SHORT).show();


                    }
                }

                @Override
                public void onFailure(Call<ContactUs> call, Throwable t) {
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBack() {
        setUpHomeActivity();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, navigationActivity.articlesAndDonations);
    }

}



