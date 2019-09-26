package com.ipd3.tech.bloodBank.code.ui.activity.startUpCycle;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.ui.activity.homeCycle.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    Animation animation;
    @BindView(R.id.Splash_Iv_Logo)
    ImageView SplashIvLogo;
    private ApiServices apiServices;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethod.changeLang(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);

        SplashIvLogo.setVisibility(View.VISIBLE);
        SplashIvLogo.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i;
                if (SharedPreferencesManger.LoadBoolean(SplashActivity.this, SharedPreferencesManger.REMEMBER)
                        && userData != null) {
                    i = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    i = new Intent(SplashActivity.this, SliderActivity.class);
                }
                startActivity(i);
                // close this activity
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        HelperMethod.changeLang(this, "ar");
        super.onConfigurationChanged(newConfig);
    }

}

