package com.ipd3.tech.bloodBank.project.ui.activity;

import android.support.v7.app.AppCompatActivity;

import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;

public class BaseActivity extends AppCompatActivity {

    public BaseFragment baseFragment;

    public void superBackPressed() {
        super.onBackPressed();
    }

}

