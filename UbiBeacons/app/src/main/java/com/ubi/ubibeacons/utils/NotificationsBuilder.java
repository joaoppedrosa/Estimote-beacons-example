package com.ubi.ubibeacons.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.ubi.ubibeacons.R;

/**
 * @author João Pedro Pedrosa, SE on 23-02-2016.
 */
public class NotificationsBuilder {

    public static void showNotificationBeacon(Context mContext, Intent notifyIntent, int id, String title, String message) {
        PendingIntent pendingIntent = PendingIntent.getActivities(mContext, 0, new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.ic_beacons)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
}


