package com.ipd3.tech.bloodBank.code.ui.activity.homeCycle;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.data.model.donation.donationDetails.DonationData;
import com.ipd3.tech.bloodBank.code.data.model.notifiction.notificationsCount.NotificationsCount;
import com.ipd3.tech.bloodBank.code.helper.HelperMethod;
import com.ipd3.tech.bloodBank.code.helper.ViewDialog;
import com.ipd3.tech.bloodBank.code.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.code.ui.fragment.ContactUs.ContactUsFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.aboutApplication.AboutApplicationFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.articlesAndRequests.ArticlesAndDonationsContainerFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.articlesAndRequests.ArticlesFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.notification.NotificationFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.notification.NotificationSettingsFragment;
import com.ipd3.tech.bloodBank.code.ui.fragment.userCycle.EditProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.registerNotificationToken;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.App_Bar_TextViewChange)
    TextView AppBarTextViewChange;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.Content_Frame_Replace)
    FrameLayout ContentFrameReplace;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.Home_Detales)
    FrameLayout HomeDetales;
    @BindView(R.id.home_navigation_activity_iv_notification)
    ImageView homeNavigationActivityIvNotification;
    @BindView(R.id.home_navigation_activity_tv_notificationCount)
    TextView homeNavigationActivityTvNotificationCount;

    private ApiServices apiServices;
    private UserData userData;

    public ArticlesAndDonationsContainerFragment articlesAndDonations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethod.changeLang(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navigation);
        ButterKnife.bind(this);

        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(this);

        try {
            //Call regesiter noti
            registerNotificationToken(apiServices, userData);
        } catch (Exception e) {

        }

        articlesAndDonations = new ArticlesAndDonationsContainerFragment();
        HelperMethod.replaceFragment(getSupportFragmentManager(),
                R.id.Content_Frame_Replace, articlesAndDonations);

        ImageView imageView = (ImageView) findViewById(R.id.menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setCount();
    }

    public void setCount() {
        apiServices.getNotificationsCount(userData.getApiToken()).enqueue(new Callback<NotificationsCount>() {
            @Override
            public void onResponse(Call<NotificationsCount> call, Response<NotificationsCount> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        homeNavigationActivityTvNotificationCount.setText(String.valueOf(response.body().getData().getNotificationsCount()));
                        if
                        (response.body().getData().getNotificationsCount() == 0) {
                            homeNavigationActivityTvNotificationCount.setVisibility(View.GONE);
                        }else if  (response.body().getData().getNotificationsCount() > 0) {
                            homeNavigationActivityTvNotificationCount.setVisibility(View.VISIBLE);
                        }


                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<NotificationsCount> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.me) {
            EditProfileFragment adjustInformation = new EditProfileFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, adjustInformation);
            AppBarTextViewChange.setText(R.string.adjustInf);

        } else if (id == R.id.not) {
            NotificationSettingsFragment notificationProperities = new NotificationSettingsFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, notificationProperities);
            AppBarTextViewChange.setText(R.string.noti_porep);


        } else if (id == R.id.fav) {
            ArticlesFragment favourites = new ArticlesFragment();
            favourites.favourites = true;
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, favourites);
            AppBarTextViewChange.setText(R.string.fav);


        } else if (id == R.id.main) {
            ArticlesAndDonationsContainerFragment articlesAndDonations = new ArticlesAndDonationsContainerFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, articlesAndDonations);
            AppBarTextViewChange.setText("");

        } else if (id == R.id.conn) {
            ContactUsFragment call_us = new ContactUsFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, call_us);
            AppBarTextViewChange.setText(R.string.call_us);


        } else if (id == R.id.abo) {
            AboutApplicationFragment about = new AboutApplicationFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, about);
            AppBarTextViewChange.setText(R.string.about);


        } else if (id == R.id.out) {

            ViewDialog alert = new ViewDialog();
            alert.showDialog(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        item.setChecked(true);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({ R.id.home_navigation_activity_iv_notification})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.home_navigation_activity_iv_notification:

                NotificationFragment notificationPush = new NotificationFragment();
                HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, notificationPush);
                AppBarTextViewChange.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //drawer is open
            drawer.closeDrawer(GravityCompat.START);
        } else {
            baseFragment.onBack();
        }

    }



    public void setTitle(String title) {
        AppBarTextViewChange.setText(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        HelperMethod.changeLang(this, "ar");
        super.onConfigurationChanged(newConfig);
    }
}
