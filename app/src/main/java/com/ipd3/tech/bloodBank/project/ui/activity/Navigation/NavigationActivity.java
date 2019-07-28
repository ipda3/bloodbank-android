package com.ipd3.tech.bloodBank.project.ui.activity.Navigation;

import android.annotation.SuppressLint;
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

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.data.api.ApiServices;
import com.ipd3.tech.bloodBank.project.data.api.RetrofitClient;
import com.ipd3.tech.bloodBank.project.data.local.SharedPreferencesManger;
import com.ipd3.tech.bloodBank.project.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.project.data.model.donation.donationDetails.DonationData;
import com.ipd3.tech.bloodBank.project.data.model.notifiction.notificationsCount.NotificationsCount;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.helper.ViewDialog;
import com.ipd3.tech.bloodBank.project.ui.activity.BaseActivity;
import com.ipd3.tech.bloodBank.project.ui.fragment.BaseFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.aboutApplication.AboutFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.articles_requests.ArticlesAndDonationsFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.articles_requests.ArticlesFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.call_us.CallUsFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.notification.NotificationFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.notification.NotificationPropertiesFragment;
import com.ipd3.tech.bloodBank.project.ui.fragment.profile.EditProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ipd3.tech.bloodBank.project.helper.HelperMethod.registerNotificationToken;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.App_Bar_TextViewChange)
    TextView AppBarTextViewChange;
    @BindView(R.id.notification)
    ImageView notification;
    @BindView(R.id.notificationCount)
    TextView notificationCount;
    @BindView(R.id.back)
    ImageView back;
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

    public FrameLayout HomeDetales1;
    private ApiServices apiServices;
    private UserData userData;
    public DonationData donationData;

    public BaseFragment baseFragment;
    public ArticlesAndDonationsFragment articlesAndDonations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethod.changeLang(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);
        apiServices = RetrofitClient.getClient().create(ApiServices.class);
        userData = SharedPreferencesManger.loadUserData(this);

        try {
            //Call regesiter noti
            registerNotificationToken(apiServices, userData);
        } catch (Exception e) {

        }

        HomeDetales1 = HomeDetales;
        if (savedInstanceState == null) {
            articlesAndDonations = new ArticlesAndDonationsFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, articlesAndDonations);

        }

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

        setCount(true);
    }

    public void setCount(boolean VISIBLE) {
        apiServices.getNotificationsCount(userData.getApiToken()).enqueue(new Callback<NotificationsCount>() {
            @Override
            public void onResponse(Call<NotificationsCount> call, Response<NotificationsCount> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        if (response.body().getData().getNotificationsCount() > 0) {
                            if (VISIBLE) {
                                notificationCount.setVisibility(View.VISIBLE);
                            } else {
                                notificationCount.setVisibility(View.GONE);
                            }

                            notificationCount.setText(String.valueOf(response.body().getData().getNotificationsCount()));
                        } else {
                            notificationCount.setVisibility(View.GONE);
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
            notification.setVisibility(View.GONE);
            notificationCount.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);

        } else if (id == R.id.not) {
            NotificationPropertiesFragment notificationProperities = new NotificationPropertiesFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, notificationProperities);

            AppBarTextViewChange.setText(R.string.noti_porep);
            notification.setVisibility(View.GONE);
            notificationCount.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);

        } else if (id == R.id.fav) {
            ArticlesFragment favourites = new ArticlesFragment();
            favourites.favourites = true;
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, favourites);
            AppBarTextViewChange.setText(R.string.fav);
            notification.setVisibility(View.VISIBLE);
            notificationCount.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);

        } else if (id == R.id.main) {
            ArticlesAndDonationsFragment articlesAndDonations = new ArticlesAndDonationsFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, articlesAndDonations);
            AppBarTextViewChange.setText("");

        } else if (id == R.id.conn) {
            CallUsFragment call_us = new CallUsFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, call_us);
            AppBarTextViewChange.setText(R.string.call_us);
            notification.setVisibility(View.GONE);
            notificationCount.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);

        } else if (id == R.id.abo) {
            AboutFragment about = new AboutFragment();
            HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, about);
            AppBarTextViewChange.setText(R.string.about);
            notification.setVisibility(View.GONE);
            notificationCount.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);

        } else if (id == R.id.out) {

            ViewDialog alert = new ViewDialog();
            alert.showDialog(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.back, R.id.notification})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();


                break;
            case R.id.notification:

                NotificationFragment notificationPush = new NotificationFragment();
                HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, notificationPush);
                notification.setVisibility(View.GONE);
                notificationCount.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                AppBarTextViewChange.setText(R.string.notiii);
                AppBarTextViewChange.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        ArticlesAndDonationsFragment articlesAndDonations = new ArticlesAndDonationsFragment();
        HelperMethod.replaceFragment(getSupportFragmentManager(), R.id.Content_Frame_Replace, articlesAndDonations);
        notification.setVisibility(View.VISIBLE);
        notificationCount.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);
        AppBarTextViewChange.setVisibility(View.GONE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    public void changeUi() {
        notification.setVisibility(View.GONE);
        notificationCount.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void changeUiDonation() {
        notification.setVisibility(View.GONE);
        notificationCount.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        AppBarTextViewChange.setText(getString(R.string.donation) + donationData.getPatientName());
    }

}
