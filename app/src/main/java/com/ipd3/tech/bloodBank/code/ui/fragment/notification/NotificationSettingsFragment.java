package com.ipd3.tech.bloodBank.code.ui.fragment.notification;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.adapter.NotificationSettingAdapter;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationSettings.NotificationSettings;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.GeneralResponseData;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.bloodTypes.BloodTypes;
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

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationSettingsFragment extends BaseFragment {

    @BindView(R.id.notification_properties_fragment_tv_notification_text)
    TextView notificationPropertiesFragmentTvNotificationText;
    @BindView(R.id.notification_properties_fragment_rv_Blood_types)
    RecyclerView notificationPropertiesFragmentRvBloodTypes;
    @BindView(R.id.notification_properties_fragment_rv_governments_list)
    RecyclerView notificationPropertiesFragmentRvGovernmentsList;
    Unbinder unbinder;

    private ApiServices apiServices;
    private UserData userData;
    private GridLayoutManager gridLayoutManager;
    private NotificationSettingAdapter bloodsAdapter, GovernAdapter;

    private List<GeneralResponseData> governoratesList = new ArrayList<>();
    private List<GeneralResponseData> bloodsList = new ArrayList<>();
    private List<String> oldBloodTypes = new ArrayList<>();
    private List<String> oldGovernorates = new ArrayList<>();

    public NotificationSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_properities, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();
//        homeActivity.changeUi(View.VISIBLE, View.GONE);
        userData = SharedPreferencesManger.loadUserData(getActivity());
        apiServices = RetrofitClient.getClient().create(ApiServices.class);

        onCall(true);

        return view;

    }

    private void onCall(boolean state) {

        if (InternetState.isConnected(getActivity())) {
            HelperMethod.showProgressDialog(getActivity(), getString(R.string.waiit));

            Call<NotificationSettings> call;

            if (state) {
                call = apiServices.getNotificationSettings(userData.getApiToken());
            } else {
                call = apiServices.setNotificationSettings(userData.getApiToken(), GovernAdapter.ids, bloodsAdapter.ids);
            }

            call.enqueue(new Callback<NotificationSettings>() {
                @Override
                public void onResponse(Call<NotificationSettings> call, Response<NotificationSettings> response) {
                    try {

                        dismissProgressDialog();
                        if (state) {

                            if (response.body().getStatus() == 1) {

                                oldBloodTypes = response.body().getData().getBloodTypes();
                                oldGovernorates = response.body().getData().getGovernorates();
                                getBloodTypes();
                                getGovern();

                            } else {
                                customToast(getActivity(), response.body().getMsg(), true);
                            }

                        } else {
                            customToast(getActivity(), response.body().getMsg(), false);
                        }

                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<NotificationSettings> call, Throwable t) {
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getBloodTypes() {

        HelperMethod.setInitRecyclerViewAsGridLayoutManager(getActivity(), notificationPropertiesFragmentRvBloodTypes, gridLayoutManager, 3);

        bloodsAdapter = new NotificationSettingAdapter(getActivity(), getActivity(), bloodsList, oldBloodTypes);

        apiServices.getBloods().enqueue(new Callback<BloodTypes>() {
            @Override
            public void onResponse(Call<BloodTypes> call, Response<BloodTypes> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        if (bloodsList.size() == 0) {
                            bloodsList.addAll(response.body().getData());
                            notificationPropertiesFragmentRvBloodTypes.setAdapter(bloodsAdapter);
                        }
                    } else {
                        dismissProgressDialog();
                        customToast(getActivity(), response.body().getMsg(), true);

                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<BloodTypes> call, Throwable t) {
                dismissProgressDialog();
                customToast(getActivity(), getResources().getString(R.string.error), true);
            }
        });


    }

    private void getGovern() {

        HelperMethod.setInitRecyclerViewAsGridLayoutManager(getActivity(), notificationPropertiesFragmentRvGovernmentsList, gridLayoutManager, 3);

        GovernAdapter = new NotificationSettingAdapter(getActivity(), getActivity(), governoratesList, oldGovernorates);
        apiServices.getGovernorates().enqueue(new Callback<Governorates>() {
            @Override
            public void onResponse(Call<Governorates> call, Response<Governorates> response) {

                try {
                    if (response.body().getStatus() == 1) {
                        if (governoratesList.size() == 0) {
                            governoratesList.addAll(response.body().getData());
                            notificationPropertiesFragmentRvGovernmentsList.setAdapter(GovernAdapter);
                        }
                    } else {
                        customToast(getActivity(), response.body().getMsg(), true);
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Governorates> call, Throwable t) {
                customToast(getActivity(), getResources().getString(R.string.error), true);
            }
        });
    }

    @OnClick(R.id.notification_properties_fragment_btn_save)
    public void onViewClicked() {

        onCall(false);

    }

    @Override
    public void onBack() {
        setUpHomeActivity();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, homeActivity.articlesAndDonations);
    }

}
