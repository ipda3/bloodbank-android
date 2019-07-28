package com.ipd3.tech.bloodBank.project.ui.fragment.aboutApplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.setting.Setting;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;
import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.customToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends BaseFragment {


    @BindView(R.id.about_fragment_tv_about_app)
    TextView AboutTextView;
    Unbinder unbinder;

    private ApiServices apiServices;
    private UserData userData;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(getActivity());

        setUpHomeActivity();

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
                                customToast(getActivity(), response.body().getMsg());
                            }

                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<Setting> call, Throwable t) {
                    customToast(getActivity(), getResources().getString(R.string.error));
                }
            });
        } else {
            customToast(getActivity(), getResources().getString(R.string.offline));
        }
    }


    @Override
    public void onBack() {
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, navigationActivity.articlesAndDonations);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
