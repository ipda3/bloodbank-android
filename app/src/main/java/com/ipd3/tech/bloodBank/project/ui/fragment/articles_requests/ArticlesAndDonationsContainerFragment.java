package com.ipd3.tech.bloodBank.project.ui.fragment.articles_requests;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.adapter.ViewPagerWithFragmentAdapter;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.donation.CreateDonationRequestsFragment;

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

    public ArticlesAndDonationsContainerFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_articles_and_donations, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpHomeActivity();
        navigationActivity.changeUi(View.VISIBLE, View.GONE);
        navigationActivity.setTitle(getString(R.string.home));

        ViewPagerWithFragmentAdapter vpadapter = new ViewPagerWithFragmentAdapter(getChildFragmentManager());

        ArticlesFragment articlesFragment = new ArticlesFragment();
        DonationRequestsFragment donationRequestsFragment = new DonationRequestsFragment();

        vpadapter.addPager(articlesFragment, getString(R.string.posts));
        vpadapter.addPager(donationRequestsFragment, getString(R.string.donations));

        Viewpager.setAdapter(vpadapter);
        TabLayout.setupWithViewPager(Viewpager);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ArticlesAndRequests_FloatingButton)
    public void onViewClicked() {
        CreateDonationRequestsFragment createDonationRequestsFragment = new CreateDonationRequestsFragment();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, createDonationRequestsFragment);
    }
}