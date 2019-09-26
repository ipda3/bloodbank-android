package com.ipd3.tech.bloodBank.code.ui.fragment.aboutApplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.setting.Setting;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutApplicationFragment extends BaseFragment {


    @BindView(R.id.about_fragment_tv_about_app)
    TextView AboutTextView;
    Unbinder unbinder;

    private ApiServices apiServices;
    private UserData userData;

    public AboutApplicationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());

        setUpHomeActivity();
//        homeActivity.changeUi(View.VISIBLE,View.GONE);

        getAbout();

        return view;
    }

    private void getAbout() {
        if (InternetState.isConnected(getActivity())) {
            apiServices.getSettings(userData.getApiToken()).enqueue(new Callback<Setting>() {
                @Override
                public void onResponse(Call<Setting> call, Response<Setting> response) {
                    try {
                        if (InternetState.isConnected(getActivity())) {
                            if ((response.body().getStatus() == 1)) {
                                HelperMethod.htmlReader(AboutTextView, response.body().getData().getAboutApp());
                            } else {
                                customToast(getActivity(), response.body().getMsg(), true);
                            }

                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<Setting> call, Throwable t) {
                    customToast(getActivity(), getResources().getString(R.string.error), true);
                }
            });
        } else {
            customToast(getActivity(), getResources().getString(R.string.offline), true);
        }
    }

    @Override
    public void onBack() {
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, homeActivity.articlesAndDonations);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
