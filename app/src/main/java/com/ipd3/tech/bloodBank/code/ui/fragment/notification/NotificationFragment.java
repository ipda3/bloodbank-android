package com.ipd3.tech.bloodBank.code.ui.fragment.notification;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.adapter.NotificationListAdapter;
import com.ipd3.tech.bloodBank.code.adapter.homeAdapter.DonationAdapter;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationList.NotificationData;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationList.NotificationsList;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.OnEndLess;
import com.ipd3.tech.bloodBank.code.helper.network.InternetState;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.showProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends BaseFragment {


    @BindView(R.id.notification_fragment_rv_notification_list)
    RecyclerView notificationFragmentRvNotificationList;
    @BindView(R.id.notification_fragment_tv_no_results)
    TextView notificationFragmentTvNoResults;
    Unbinder unbinder;
    @BindView(R.id.notifications_list_fragment_srl_notifications_list_refresh)
    SwipeRefreshLayout notificationsListFragmentSrlNotificationsListRefresh;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_push, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();
//        homeActivity.changeUi(View.GONE,View.VISIBLE);
        homeActivity.setTitle(getString(R.string.notiii));
        homeActivity.setCount();

        userData = SharedPreferencesManger.loadUserData(getActivity());
        apiServices = RetrofitClient.getClient().create(ApiServices.class);

        initRecyclerView();

        if (notificationList.size() == 0) {
            getNotificationList(1);
        }
        refresh();
        return view;
    }

    private void refresh() {
        notificationsListFragmentSrlNotificationsListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onEndLess.current_page = 1;
                onEndLess.previousTotal = 0;
                onEndLess.previous_page = 1;

                max = 0;

                notificationList = new ArrayList<>();

                notificationListAdapter = new NotificationListAdapter(getActivity(), getActivity(), notificationList);
                notificationFragmentRvNotificationList.setAdapter(notificationListAdapter);

                getNotificationList(1);
            }
        });

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

                        getNotificationList(current_page);

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

    private void getNotificationList(int page) {
        if (InternetState.isConnected(getActivity())) {
            if (page > 1) {
                showProgressDialog(getActivity(), getString(R.string.waiit));
            }
            apiServices.getNotificationsList(userData.getApiToken(), page).enqueue(new Callback<NotificationsList>() {
                @Override
                public void onResponse(Call<NotificationsList> call, Response<NotificationsList> response) {
                    try {
                        dismissProgressDialog();
                        notificationsListFragmentSrlNotificationsListRefresh.setRefreshing(false);

                        if (response.body().getStatus() == 1) {
                            if (response.body().getData().getTotal() > 0) {
                                notificationFragmentTvNoResults.setVisibility(View.GONE);

                            } else {
                                notificationFragmentTvNoResults.setVisibility(View.VISIBLE);

                            }


                            max = response.body().getData().getLastPage();
                            notificationList.addAll(response.body().getData().getData());
                            notificationListAdapter.notifyDataSetChanged();


                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);

                        }
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<NotificationsList> call, Throwable t) {
                    dismissProgressDialog();
                    customToast(getActivity(), getResources().getString(R.string.error), true);
                    try {
                        notificationsListFragmentSrlNotificationsListRefresh.setRefreshing(false);
                    } catch (Exception e) {

                    }

                }
            });

        } else {
            dismissProgressDialog();
            notificationsListFragmentSrlNotificationsListRefresh.setRefreshing(false);
            customToast(getActivity(), getResources().getString(R.string.offline), true);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onBack() {
        setUpHomeActivity();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager()
                , R.id.Content_Frame_Replace
                , homeActivity.articlesAndDonations);
    }
}
