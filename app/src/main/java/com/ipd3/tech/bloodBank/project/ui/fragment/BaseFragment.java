package com.ipd3.tech.bloodBank.project.ui.fragment;

import android.support.v4.app.Fragment;

import com.ipd3.tech.bloodBank.project.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.project.ui.activity.Navigation.NavigationActivity;

public class BaseFragment extends Fragment {

    public BaseActivity baseActivity;
    public NavigationActivity navigationActivity;

    public void setUpActivity() {
        baseActivity = (BaseActivity) getActivity();
    }

    public void setUpHomeActivity() {
        navigationActivity = (NavigationActivity) getActivity();
    }

    public void onBack() {

        baseActivity.superBackPressed();

    }

}




