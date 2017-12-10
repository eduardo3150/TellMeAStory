package com.chavez.eduardo.tellmeastory.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.DetailedStory;
import com.chavez.eduardo.tellmeastory.ui.MainActivity;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by eduardo3150 on 11/13/17.
 */

public class TellAStoryMessagingService extends FirebaseMessagingService {
    public static final String LOG_TAG = TellAStoryMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        Log.d(LOG_TAG, remoteMessage.getNotification().getBody());

        if (remoteMessage.getData().entrySet().isEmpty()) {
            Intent openAppIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_stat_name);
            builder.setContentTitle("Tell Me a Story");
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setContentText(remoteMessage.getNotification().getBody());
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(001, builder.build());
            }
        } else {
            String key;
            int value = 0;
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                key = entry.getKey();
                value = Integer.parseInt(entry.getValue());
                Log.d(LOG_TAG, "key, " + key + " value " + value);
            }

            Intent openAppIntent = new Intent(this, DetailedStory.class);
            openAppIntent.putExtra(ConfigurationUtils.BUNDLE_MAIN_KEY, value);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_stat_name);
            builder.setContentTitle("Tell Me a Story");
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setContentText(remoteMessage.getNotification().getBody());
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(001, builder.build());
            }

        }
    }
}
