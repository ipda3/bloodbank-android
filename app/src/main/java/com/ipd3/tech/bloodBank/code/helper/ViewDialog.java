package com.ipd3.tech.bloodBank.code.helper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ipd3.tech.bloodBank.code.R;
import com.ipd3.tech.bloodBank.code.data.api.ApiServices;
import com.ipd3.tech.bloodBank.code.data.model.auth.login.UserData;
import com.ipd3.tech.bloodBank.code.ui.activity.authentication.UserCycleActivity;

import static com.ipd3.tech.bloodBank.code.data.api.RetrofitClient.getClient;
import static com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger.clean;
import static com.ipd3.tech.bloodBank.code.data.local.SharedPreferencesManger.loadUserData;
import static com.ipd3.tech.bloodBank.code.helper.HelperMethod.removeNotificationToken;

public class ViewDialog {
    private ApiServices apiServices;
    private UserData userData;
    private Activity activity;

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        dialog.setCanceledOnTouchOutside(true);
        apiServices = getClient().create(ApiServices.class);
        userData = loadUserData(activity);
        TextView text = (TextView) dialog.findViewById(R.id.text);

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialog_ok);
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Call
                removeNotificationToken(apiServices, userData);

                clean(activity);

                Intent i = new Intent(activity, UserCycleActivity.class);

                activity.startActivity(i);
                // close this activity
                activity.finish();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        Button dialogButtonNo = (Button) dialog.findViewById(R.id.dialog_no);
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        dialog.show();

    }
}
