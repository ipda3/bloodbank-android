package com.ipd3.tech.bloodBank.project.ui.activity.splashCycel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ipd3.tech.bloodBank.project.R;
import com.ipd3.tech.bloodBank.project.adapter.SlideAdapter;
import com.ipd3.tech.bloodBank.project.helper.HelperMethod;
import com.ipd3.tech.bloodBank.project.ui.activity.authCycle.UserCycleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class StartUpSlideActivity extends AppCompatActivity {

    @BindView(R.id.Start_Up_Slide_Point1)
    CircleImageView StartUpSlidePoint1;
    @BindView(R.id.Start_Up_Slide_Point2)
    CircleImageView StartUpSlidePoint2;
    @BindView(R.id.Start_Up_Slide_ViewPager)
    ViewPager StartUpSlideViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethod.changeLang(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_slide);
        ButterKnife.bind(this);

        SlideAdapter adapter = new SlideAdapter(this);
        StartUpSlideViewPager.setAdapter(adapter);
        StartUpSlideViewPager.setCurrentItem(adapter.getCount() - 1);

        StartUpSlideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    StartUpSlidePoint2.setBackgroundResource(R.drawable.check_circle);
                    StartUpSlidePoint1.setBackgroundResource(R.drawable.uncheck_circle);
                } else {
                    StartUpSlidePoint1.setBackgroundResource(R.drawable.check_circle);
                    StartUpSlidePoint2.setBackgroundResource(R.drawable.uncheck_circle);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.Start_Up_Slide_Ban_Skip)
    public void onViewClicked() {

        Intent intent = new Intent(StartUpSlideActivity.this, UserCycleActivity.class);
        startActivity(intent);
        finish();

    }
}
