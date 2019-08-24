package com.ipd3.tech.bloodBank.project.ui.fragment;

import android.support.v4.app.Fragment;

import com.ipd3.tech.bloodBank.project.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.project.ui.activity.homeCycle.HomeNavigationActivity;

public class BaseFragment extends Fragment {

    public BaseActivity baseActivity;
    public HomeNavigationActivity navigationActivity;

    public void setUpActivity() {
        baseActivity = (BaseActivity) getActivity();
        baseActivity.baseFragment = this;
    }

    public void setUpHomeActivity() {
        navigationActivity = (HomeNavigationActivity) getActivity();
    }

    public void onBack() {

        baseActivity.superBackPressed();

    }

}




