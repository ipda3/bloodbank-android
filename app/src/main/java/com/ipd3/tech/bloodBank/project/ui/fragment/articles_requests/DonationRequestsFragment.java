package com.ipd3.tech.bloodBank.project.ui.fragment.articles_requests;


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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.adapter.homeAdapter.DonationAdapter;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.donation.donationDetails.DonationData;
import com.ipd3.tech.bloodBank.project.data.model.donation.donationRequests.AllDonation;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.bloodTypes.BloodTypes;
import com.ipd3.tech.bloodBank.project.data.model.publiceData.governorates.Governorates;
import com.ipd3.tech.bloodBank.project.helper.OnEndLess;
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

import static android.support.constraint.Constraints.TAG;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.customToast;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.dismissProgressDialog;
import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.showProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class DonationRequestsFragment extends Fragment {

    @BindView(R.id.donation_requests_fragment_iv_search)
    ImageView DonationRequestsImageViewSearch;
    @BindView(R.id.donation_requests_fragment_sp_blood_types)
    Spinner donationRequestsFragmentSpBloodTypes;
    @BindView(R.id.donation_requests_fragment_sp_city)
    Spinner donationRequestsFragmentSpCity;
    @BindView(R.id.donation_requests_fragment_rl_donations_list)
    RecyclerView donationRequestsFragmentRlDonationsList;
    @BindView(R.id.donation_requests_fragment_tv_no_results)
    TextView donation_requests_fragment_tv_no_results;
    @BindView(R.id.donation_requests_fragment_srl_donations_list_refresh)
    SwipeRefreshLayout donationRequestsFragmentSrlDonationsListRefresh;
    Unbinder unbinder;

    private List<String> BloodTypesTxt = new ArrayList<>();
    private List<Integer> BloodTypesId = new ArrayList<>();
    private List<String> GovernoratesTxt = new ArrayList<>();
    private List<Integer> GovernoratesId = new ArrayList<>();
    public List<DonationData> donationList = new ArrayList<>();

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donation_requests, container, false);
        unbinder = ButterKnife.bind(this, view);
        user = SharedPreferencesManger.loadUserData(getActivity());
        apiServices = RetrofitClient.getClient().create(ApiServices.class);

        initRecyclerView();

        getGovernorates();
        getBloodTypes();

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

                getDonations(1);
            }
        });

        getDonations(1);
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

                        BloodTypesTxt.add("كل الفصائل");
                        BloodTypesId.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            BloodTypesTxt.add(response.body().getData().get(i).getName());
                            BloodTypesId.add(response.body().getData().get(i).getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item_small, BloodTypesTxt);

                        donationRequestsFragmentSpBloodTypes.setAdapter(adapter);

                        donationRequestsFragmentSpBloodTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                bloodTypeId = BloodTypesId.get(i);
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

                        GovernoratesTxt.add("جميع المحافظات");
                        GovernoratesId.add(0);

                        for (int i = 0; i < response.body().getData().size(); i++) {
                            GovernoratesTxt.add(response.body().getData().get(i).getName());
                            GovernoratesId.add(response.body().getData().get(i).getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item_small, GovernoratesTxt);

                        donationRequestsFragmentSpCity.setAdapter(adapter);
                        donationRequestsFragmentSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                governorateId = (GovernoratesId.get(i));


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
        if (InternetState.isConnected(getActivity())) {

            donationRequestsCall = RetrofitClient.getClient().create(ApiServices.class).getDonationRequests(user.getApiToken(), page);

            callData(page, donationRequestsCall);

        } else {
            dismissProgressDialog();
            customToast(getActivity(), getResources().getString(R.string.offline));

            donationRequestsFragmentSrlDonationsListRefresh.setRefreshing(false);
        }

    }

    @OnClick(R.id.donation_requests_fragment_iv_search)
    public void onViewClicked() {

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
        if (InternetState.isConnected(getActivity())) {

            if (page == 1) {
                showProgressDialog(getActivity(), getString(R.string.waiit));
            }

            callData(page, apiServices.getDonationRequestsFilter(user.getApiToken(), bloodTypeId, governorateId, page));

        } else {
            dismissProgressDialog();
            customToast(getActivity(), getResources().getString(R.string.offline));

            donationRequestsFragmentSrlDonationsListRefresh.setRefreshing(false);
        }


    }

    private void callData(int page, Call<AllDonation> donationRequestsCall) {

        donationRequestsCall.enqueue(new Callback<AllDonation>() {
            @Override
            public void onResponse(Call<AllDonation> call, Response<AllDonation> response) {
                if (response.body().getStatus() == 1) {
                    if (response.body().getData().getTotal() > 0) {
                        donation_requests_fragment_tv_no_results.setVisibility(View.GONE);
                    } else {
                        donation_requests_fragment_tv_no_results.setVisibility(View.VISIBLE);

                    }
                    max = response.body().getData().getLastPage();
                    donationList.addAll(response.body().getData().getData());
                    donationAdapter.notifyDataSetChanged();
                    dismissProgressDialog();


                } else {
                    Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AllDonation> call, Throwable t) {
                dismissProgressDialog();
                Log.d("", "onFailure: " + t.toString());
                customToast(getActivity(), getResources().getString(R.string.error));
                donationRequestsFragmentSrlDonationsListRefresh.setRefreshing(false);
            }
        });

    }

}

