package com.ipd3.tech.bloodBank.project.ui.activity.authCycle;

import android.os.Bundle;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.project.ui.fragment.userCycle.LoginFragment;

public class UserCycleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethod.changeLang(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cycle);

        LoginFragment loginFragment = new LoginFragment();
        HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.frame, loginFragment);

    }

    @Override
    public void onBackPressed() {
        baseFragment.onBack();
    }

}
