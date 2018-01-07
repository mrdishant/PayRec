package com.nearur.payrec;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mrdis on 1/2/2018.
 */

public class MyNotificationService extends FirebaseMessagingService {

   /* @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("Message From",remoteMessage.getFrom());
        Log.i("Message Body",remoteMessage.getNotification().getBody());
    }
*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i("Message From", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i("Data Payload", "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i("message Body", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.i("TokenRefreshed",SharedPrefManager.getInstance(getApplicationContext()).getToken());

            notifyUser(remoteMessage.getFrom(),remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void notifyUser(String from, String body) {

        MyNotificationManager myNotificationManager=new MyNotificationManager(getApplicationContext());
        myNotificationManager.shownotification(from,body,new Intent(getApplicationContext(),MainActivity.class));

    }
}
