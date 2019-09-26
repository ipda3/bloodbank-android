package com.ipd3.tech.bloodBank.code.ui.fragment;

import android.support.v4.app.Fragment;

import com.ipd3.tech.bloodBank.code.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.code.ui.activity.homeCycle.HomeActivity;

public class BaseFragment extends Fragment {

    public BaseActivity baseActivity;
    public HomeActivity homeActivity;

    public void setUpActivity() {
        baseActivity = (BaseActivity) getActivity();
        baseActivity.baseFragment = this;
    }

    public void setUpHomeActivity() {
        homeActivity = (HomeActivity) getActivity();
    }

    public void onBack() {

        baseActivity.superBackPressed();

    }

}




