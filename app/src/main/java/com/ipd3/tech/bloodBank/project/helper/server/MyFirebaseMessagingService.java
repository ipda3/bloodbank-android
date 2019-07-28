package com.ipd3.tech.bloodBank.project.helper.server;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ipd3.tech.bloodBank.project.R;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {

            //You should use an actual ID instead
            int notificationId = new Random().nextInt(60000);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels();
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(remoteMessage.getData().get("title"))
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .setSummaryText(remoteMessage.getData().get("message")))
                            .setContentText(remoteMessage.getData().get("message"))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri);

            notificationManager.notify(notificationId, notificationBuilder.build());

        } catch (Exception e) {

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = "";
        String adminChannelDescription = "";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}