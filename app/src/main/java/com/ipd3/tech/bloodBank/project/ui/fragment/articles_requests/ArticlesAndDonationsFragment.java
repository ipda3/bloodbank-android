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
import com.ipd3.tech.bloodBank.project.adapter.ViewPagerAdapter;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.ui.fragment.donation.CreateDonationRequestsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticlesAndDonationsFragment extends Fragment {

    @BindView(R.id.ArticlesAndRequests_Frame)
    FrameLayout ArticlesAndRequestsFrame;
    @BindView(R.id.TabLayout)
    android.support.design.widget.TabLayout TabLayout;
    @BindView(R.id.Viewpager)
    ViewPager Viewpager;
    @BindView(R.id.ArticlesAndRequests_FloatingButton)
    FloatingActionButton ArticlesAndRequestsFloatingButton;
    Unbinder unbinder;

    public ArticlesAndDonationsFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_articles_and_donations, container, false);
        unbinder = ButterKnife.bind(this, view);

        ViewPagerAdapter vpadapter = new ViewPagerAdapter(getChildFragmentManager());

        ArticlesFragment articals_fragment = new ArticlesFragment();
        com.ipd3.tech.bloodBank.project.ui.fragment.articles_requests.DonationRequestsFragment dontaion_fragment = new com.ipd3.tech.bloodBank.project.ui.fragment.articles_requests.DonationRequestsFragment();

        vpadapter.addPager(articals_fragment, "المقالات");
        vpadapter.addPager(dontaion_fragment, "طلبات التبرع ");

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
        CreateDonationRequestsFragment requestFragment = new CreateDonationRequestsFragment();
        HelperMethod.replaceFragment(getActivity().getSupportFragmentManager(), R.id.Content_Frame_Replace, requestFragment);
    }
}