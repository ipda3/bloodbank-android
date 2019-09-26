package com.ipd3.tech.bloodBank.code.ui.fragment.articlesAndRequests;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.adapter.homeAdapter.DonationAdapter;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationDetails.DonationData;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationRequests.AllDonation;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.GeneralResponseData;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.code.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.OnEndLess;
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

import static android.support.constraint.Constraints.TAG;
import static com.ipd3.tech.bloodBank.code.data.api.RetrofitClient.getClient;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.setSpinner;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.showProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class DonationRequestsFragment extends BaseFragment {

    @BindView(R.id.donation_requests_fragment_iv_search)
    ImageView DonationRequestsImageViewSearch;
    @BindView(R.id.donation_requests_fragment_sp_blood_types)
    Spinner donationRequestsFragmentSpBloodTypes;
    @BindView(R.id.donation_requests_fragment_sp_city)
    Spinner donationRequestsFragmentSpCity;
    @BindView(R.id.donation_requests_fragment_rl_donations_list)
    RecyclerView donationRequestsFragmentRlDonationsList;
    @BindView(R.id.donation_requests_fragment_tv_no_results)
    TextView donationRequestsFragmentTvNoResults;
    @BindView(R.id.donation_requests_fragment_srl_donations_list_refresh)
    SwipeRefreshLayout donationRequestsFragmentSrlDonationsListRefresh;
    Unbinder unbinder;

    private List<String> bloodTypesNames = new ArrayList<>();
    private List<Integer> bloodTypesIds = new ArrayList<>();
    private List<String> governoratesNames = new ArrayList<>();
    private List<Integer> governoratesIds = new ArrayList<>();
    public List<DonationData> donationList = new ArrayList<>();
    private List<GeneralResponseData> bloodTypesData = new ArrayList<>();
    private List<GeneralResponseData> governoratesData = new ArrayList<>();

    public DonationAdapter donationAdapter;
    private Call<AllDonation> donationRequestsCall;
    private UserData user;
    private ApiServices apiServices;

    private int governorateId = 0;
    private int bloodTypeId = 0;
    private int max = 0;
    private boolean Filter = false;
    private LinearLayoutManager linearLayoutManager;
    private OnEndLess onEndLess;

    public DonationRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HelperMethod.changeLang(getActivity(), "ar");
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donation_requests, container, false);
        unbinder = ButterKnife.bind(this, view);
        user = SharedPreferencesManger.loadUserData(getActivity());
        apiServices = getClient().create(ApiServices.class);

        initRecyclerView();

        if (bloodTypesData.size() == 0) {
            getBloodTypes();
        } else {
            setSpinner(getActivity(), donationRequestsFragmentSpBloodTypes, bloodTypesNames);
        }

        if (governoratesData.size() == 0) {
            getGovernorates();
        } else {
            setSpinner(getActivity(), donationRequestsFragmentSpCity, governoratesNames);
        }

        donationRequestsFragmentSrlDonationsListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onEndLess.current_page = 1;
                onEndLess.previousTotal = 0;
                onEndLess.previous_page = 1;

                max = 0;

                donationList = new ArrayList<>();

                donationAdapter = new DonationAdapter(getActivity(), getActivity(), donationList);
                donationRequestsFragmentRlDonationsList.setAdapter(donationAdapter);

                if (Filter) {
                    getFilter(1);
                } else {
                    getDonations(1);
                }
            }
        });

        if (donationList.size() == 0) {
            getDonations(1);
        }

        return view;

    }

    private void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        donationRequestsFragmentRlDonationsList.setLayoutManager(linearLayoutManager);

        onEndLess = new OnEndLess(linearLayoutManager, 1) {
            @Override
            public void onLoadMore(int current_page) {
                if (current_page <= max) {
                    if (max != 0 && current_page != 1) {
                        onEndLess.previous_page = current_page;

                        if (Filter) {
                            getFilter(current_page);
                        } else {
                            getDonations(current_page);
                        }

                    } else {
                        onEndLess.current_page = onEndLess.previous_page;
                    }
                } else {
                    onEndLess.current_page = onEndLess.previous_page;
                }
            }
        };
        donationRequestsFragmentRlDonationsList.addOnScrollListener(onEndLess);
        donationAdapter = new DonationAdapter(getActivity(), getActivity(), donationList);
        donationRequestsFragmentRlDonationsList.setAdapter(donationAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getBloodTypes() {
        apiServices.getBloods().enqueue(new Callback<BloodTypes>() {
            @Override
            public void onResponse(Call<BloodTypes> call, Response<BloodTypes> response) {
                try {
                    if (response.body().getStatus() == 1) {

                        bloodTypesData = new ArrayList<>();

                        bloodTypesData = response.body().getData();

                        bloodTypesNames = new ArrayList<>();
                        bloodTypesIds = new ArrayList<>();

                        bloodTypesNames.add(getString(R.string.all_blood));
                        bloodTypesIds.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            bloodTypesNames.add(response.body().getData().get(i).getName());
                            bloodTypesIds.add(response.body().getData().get(i).getId());
                        }

                        setSpinner(getActivity(), donationRequestsFragmentSpBloodTypes, bloodTypesNames);

                        donationRequestsFragmentSpBloodTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                bloodTypeId = bloodTypesIds.get(i);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<BloodTypes> call, Throwable t) {

            }
        });

    }

    private void getGovernorates() {
        apiServices.getGovernorates().enqueue(new Callback<Governorates>() {
            @Override
            public void onResponse(Call<Governorates> call, Response<Governorates> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        governoratesData = new ArrayList<>();
                        governoratesData = response.body().getData();

                        governoratesNames = new ArrayList<>();
                        governoratesIds = new ArrayList<>();

                        governoratesNames.add(getString(R.string.all_governorates));
                        governoratesIds.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            governoratesNames.add(response.body().getData().get(i).getName());
                            governoratesIds.add(response.body().getData().get(i).getId());
                        }

                        setSpinner(getActivity(), donationRequestsFragmentSpCity, governoratesNames);

                        donationRequestsFragmentSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                governorateId = (governoratesIds.get(i));


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    } else {

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Governorates> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void getDonations(int page) {

        callData(page, getClient().create(ApiServices.class).getDonationRequests(user.getApiToken(), page));

    }

    @OnClick(R.id.donation_requests_fragment_iv_search)
    public void onViewClicked() {

        showProgressDialog(getActivity(), getString(R.string.waiit));
        if (donationRequestsFragmentSpBloodTypes.getSelectedItemPosition() == 0
                && donationRequestsFragmentSpCity.getSelectedItemPosition() == 0) {

            if (Filter) {
                Filter = false;

                onEndLess.current_page = 1;
                onEndLess.previous_page = 1;
                onEndLess.previousTotal = 0;
                onEndLess.totalItemCount = 0;
                max = 0;

                donationList = new ArrayList<>();
                donationAdapter = new DonationAdapter(getActivity(), getActivity(), donationList);
                donationRequestsFragmentRlDonationsList.setAdapter(donationAdapter);

                getDonations(1);
            }else {
                dismissProgressDialog();
            }

        } else {
            Filter = true;

            onEndLess.current_page = 1;
            onEndLess.previous_page = 1;
            onEndLess.previousTotal = 0;
            onEndLess.totalItemCount = 0;
            max = 0;

            donationList = new ArrayList<>();
            donationAdapter = new DonationAdapter(getActivity(), getActivity(), donationList);
            donationRequestsFragmentRlDonationsList.setAdapter(donationAdapter);

            getFilter(1);
        }

    }

    private void getFilter(int page) {

        callData(page, apiServices.getDonationRequestsFilter(user.getApiToken(), bloodTypeId, governorateId, page));

    }

    private void callData(int page, Call<AllDonation> donationRequestsCall) {
        if (page > 1) {
            showProgressDialog(getActivity(), getString(R.string.waiit));
        }

        if (InternetState.isConnected(getActivity())) {

            donationRequestsCall.enqueue(new Callback<AllDonation>() {
                @Override
                public void onResponse(Call<AllDonation> call, Response<AllDonation> response) {
                    try {
                        dismissProgressDialog();
                        donationRequestsFragmentSrlDonationsListRefresh.setRefreshing(false);
                        if (response.body().getStatus() == 1) {
                            if (response.body().getData().getTotal() > 0) {
                                donationRequestsFragmentTvNoResults.setVisibility(View.GONE);
                            } else {
                                donationRequestsFragmentTvNoResults.setVisibility(View.VISIBLE);

                            }
                            max = response.body().getData().getLastPage();
                            donationList.addAll(response.body().getData().getData());
                            donationAdapter.notifyDataSetChanged();

                        } else {
                            customToast(getActivity(), response.body().getMsg(), true);
                        }
                    } catch (Exception e) {
                        customToast(getActivity(), response.body().getMsg(), true);
                    }
                }

                @Override
                public void onFailure(Call<AllDonation> call, Throwable t) {
                    dismissProgressDialog();
                    customToast(getActivity(), getResources().getString(R.string.error), true);

                    try {
                        donationRequestsFragmentSrlDonationsListRefresh.setRefreshing(false);
                    } catch (Exception e) {

                    }
                }
            });

        } else {
            dismissProgressDialog();
            customToast(getActivity(), getResources().getString(R.string.offline), true);

            donationRequestsFragmentSrlDonationsListRefresh.setRefreshing(false);
        }

    }

    @Override
    public void onBack() {
        getActivity().finish();
    }

}

