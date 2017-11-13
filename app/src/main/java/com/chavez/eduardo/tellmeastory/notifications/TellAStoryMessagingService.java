package com.chavez.eduardo.tellmeastory.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chavez.eduardo.tellmeastory.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by eduardo3150 on 11/13/17.
 */

public class TellAStoryMessagingService extends FirebaseMessagingService {
    public static final String  LOG_TAG = TellAStoryMessagingService.class.getSimpleName();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        Log.d(LOG_TAG, remoteMessage.getNotification().getBody());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("UDB");
        builder.setContentText(remoteMessage.getNotification().getBody());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(001, builder.build());
        }
    }
}
