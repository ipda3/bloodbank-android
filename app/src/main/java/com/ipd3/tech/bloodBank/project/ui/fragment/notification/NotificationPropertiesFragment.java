package com.ipd3.tech.bloodBank.project.ui.fragment.notification;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.adapter.NotificationSettingAdapter;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.notifiction.notificationSettings.NotificationSettings;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.GeneralResponseData;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationPropertiesFragment extends Fragment {

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

    public NotificationPropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_properities, container, false);
        unbinder = ButterKnife.bind(this, view);

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
                call = apiServices.setNotificationSettings(userData.getApiToken(), GovernAdapter.Ids, bloodsAdapter.Ids);
            }

            call.enqueue(new Callback<NotificationSettings>() {
                @Override
                public void onResponse(Call<NotificationSettings> call, Response<NotificationSettings> response) {
                    if (state) {
                        if (response.body().getStatus() == 1) {
                            HelperMethod.dismissProgressDialog();

                            oldBloodTypes = response.body().getData().getBloodTypes();
                            oldGovernorates = response.body().getData().getGovernorates();
                            getBloodTypes();
                            getGovern();
                        }
                    } else {
                        HelperMethod.dismissProgressDialog();

                        Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<NotificationSettings> call, Throwable t) {
                    HelperMethod.dismissProgressDialog();

                    Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            HelperMethod.dismissProgressDialog();
            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
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
                if (response.body().getStatus() == 1) {
                    bloodsList.addAll(response.body().getData());
                    notificationPropertiesFragmentRvBloodTypes.setAdapter(bloodsAdapter);
                }
            }

            @Override
            public void onFailure(Call<BloodTypes> call, Throwable t) {

            }
        });


    }

    private void getGovern() {

        HelperMethod.setInitRecyclerViewAsGridLayoutManager(getActivity(), notificationPropertiesFragmentRvGovernmentsList, gridLayoutManager, 3);

        GovernAdapter = new NotificationSettingAdapter(getActivity(), getActivity(), governoratesList, oldGovernorates);
        apiServices.getGovernorates().enqueue(new Callback<Governorates>() {
            @Override
            public void onResponse(Call<Governorates> call, Response<Governorates> response) {
                if (response.body().getStatus() == 1) {
                    governoratesList.addAll(response.body().getData());
                    notificationPropertiesFragmentRvGovernmentsList.setAdapter(GovernAdapter);

                }
            }

            @Override
            public void onFailure(Call<Governorates> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.notification_properties_fragment_btn_save)
    public void onViewClicked() {
        if (InternetState.isConnected(getActivity())) {

            onCall(false);


        } else {
            HelperMethod.dismissProgressDialog();
            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
        }
    }

}
