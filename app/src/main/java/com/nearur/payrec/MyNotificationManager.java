package com.nearur.payrec;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

/**
 * Created by mrdis on 1/2/2018.
 */

public class MyNotificationManager {

    private Context context;

    public MyNotificationManager(Context context) {
        this.context = context;
    }

    public void shownotification(String from,String message,Intent intent){

        PendingIntent intent1=PendingIntent.getActivity(context,1234,intent,PendingIntent.FLAG_CANCEL_CURRENT);


        Notification notification=new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentText(message)
                .setContentTitle(from)
                .setSmallIcon(R.drawable.email)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.email))
                .setContentIntent(intent1).build();
        notification.flags |=Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1234,notification);


    }
}
