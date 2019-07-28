package com.ipd3.tech.bloodBank.project.ui.fragment.notification;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.adapter.NotificationListAdapter;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.notifiction.notificationList.NotificationData;
import com.ipd3.tech.bloodBank.project.data.model.notifiction.notificationList.NotificationsList;
import com.ipd3.tech.bloodBank.project.helper.OnEndLess;
import com.ipd3.tech.bloodBank.project.helper.network.InternetState;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.dismissProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    @BindView(R.id.notification_fragment_rv_notification_list)
    RecyclerView notificationFragmentRvNotificationList;
    @BindView(R.id.notification_fragment_tv_no_results)
    TextView notificationFragmentTvNoResults;
    Unbinder unbinder;

    private ApiServices apiServices;
    private UserData userData;

    private NotificationListAdapter notificationListAdapter;
    private List<NotificationData> notificationList = new ArrayList<>();

    private int max = 0;
    private LinearLayoutManager linearLayoutManager;
    private OnEndLess onEndLess;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_push, container, false);
        unbinder = ButterKnife.bind(this, view);

        userData = SharedPreferencesManger.loadUserData(getActivity());
        apiServices = RetrofitClient.getClient().create(ApiServices.class);

        initRecyclerView();

        getList(1);

        return view;
    }

    private void initRecyclerView() {

        linearLayoutManager = new LinearLayoutManager(getActivity());
        notificationFragmentRvNotificationList.setLayoutManager(linearLayoutManager);

        onEndLess = new OnEndLess(linearLayoutManager, 1) {
            @Override
            public void onLoadMore(int current_page) {

                if (current_page <= max) {
                    if (max != 0 && current_page != 1) {
                        onEndLess.previous_page = current_page;

                        getList(current_page);

                    } else {
                        onEndLess.current_page = onEndLess.previous_page;
                    }
                } else {
                    onEndLess.current_page = onEndLess.previous_page;
                }

            }
        };
        notificationFragmentRvNotificationList.addOnScrollListener(onEndLess);
        notificationListAdapter = new NotificationListAdapter(getActivity(), getActivity(), notificationList);
        notificationFragmentRvNotificationList.setAdapter(notificationListAdapter);

    }

    private void getList(int page) {
        if (InternetState.isConnected(getActivity())) {
            apiServices.getNotificationsList(userData.getApiToken(), page).enqueue(new Callback<NotificationsList>() {
                @Override
                public void onResponse(Call<NotificationsList> call, Response<NotificationsList> response) {
                    if (response.body().getStatus() == 1) {
                        max = response.body().getData().getLastPage();
                        notificationList.addAll(response.body().getData().getData());
                        notificationFragmentRvNotificationList.setAdapter(notificationListAdapter);
                        if (response.body().getData().getTotal() > 0) {
                            notificationFragmentTvNoResults.setVisibility(View.GONE);

                        } else {
                            notificationFragmentTvNoResults.setVisibility(View.VISIBLE);

                        }
                    } else {
                        Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onFailure(Call<NotificationsList> call, Throwable t) {
                    dismissProgressDialog();

                    Log.d("", "onFailure: " + t.toString());
                    Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            dismissProgressDialog();

            Toast.makeText(getActivity(), R.string.offline, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
