package com.ipd3.tech.bloodBank.code.ui.fragment.articlesAndRequests;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.adapter.ViewPagerWithFragmentAdapter;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.ui.fragment.BaseFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.donation.CreateDonationRequestFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticlesAndDonationsContainerFragment extends BaseFragment {

    @BindView(R.id.ArticlesAndRequests_Frame)
    FrameLayout ArticlesAndRequestsFrame;
    @BindView(R.id.TabLayout)
    android.support.design.widget.TabLayout TabLayout;
    @BindView(R.id.Viewpager)
    ViewPager Viewpager;
    @BindView(R.id.ArticlesAndRequests_FloatingButton)
    FloatingActionButton ArticlesAndRequestsFloatingButton;
    Unbinder unbinder;

    public boolean backFromFavourites = false;
    private DonationRequestsFragment donationRequestsFragment;

    public ArticlesAndDonationsContainerFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HelperMethod.changeLang(getActivity(), "ar");
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_articles_and_donations, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();
//        homeActivity.changeUi(View.VISIBLE, View.GONE);
        homeActivity.setTitle(getString(R.string.home));

        ViewPagerWithFragmentAdapter vpadapter = new ViewPagerWithFragmentAdapter(getChildFragmentManager());

        ArticlesFragment articlesFragment = new ArticlesFragment();
        articlesFragment.backFromFavourites = backFromFavourites;
        backFromFavourites = false;
        donationRequestsFragment = new DonationRequestsFragment();

        vpadapter.addPager(articlesFragment, getString(R.string.posts));
        vpadapter.addPager(donationRequestsFragment, getString(R.string.donations));

        Viewpager.setAdapter(vpadapter);
        TabLayout.setupWithViewPager(Viewpager);

        setUpActivity();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ArticlesAndRequests_FloatingButton)
    public void onViewClicked() {
        CreateDonationRequestFragment createDonationRequestsFragment = new CreateDonationRequestFragment();
        createDonationRequestsFragment.donationRequestsFragment = donationRequestsFragment;
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, createDonationRequestsFragment);
    }

    @Override
    public void onBack() {
        getActivity().finish();
    }
}