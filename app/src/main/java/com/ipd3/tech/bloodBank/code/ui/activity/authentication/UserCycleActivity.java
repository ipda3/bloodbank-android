package com.ipd3.tech.bloodBank.code.ui.activity.authentication;

import android.content.res.Configuration;
import android.os.Bundle;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.code.ui.fragment.userCycle.LoginFragment;

public class UserCycleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethod.changeLang(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cycle);

        LoginFragment loginFragment = new LoginFragment();
        HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.User_Cycle_Activity_EmptyFrame, loginFragment);

    }

    @Override
    public void onBackPressed() {
        baseFragment.onBack();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        HelperMethod.changeLang(this, "ar");
        super.onConfigurationChanged(newConfig);
    }

}
