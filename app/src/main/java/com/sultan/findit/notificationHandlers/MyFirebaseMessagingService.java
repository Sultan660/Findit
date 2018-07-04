package com.sultan.findit.notificationHandlers;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title = "";
    String text = "";
    JSONObject jsonObject;

    int count = 0;
    
    private static final String TAG = "MyFirebaseMessagingServ";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
            title = remoteMessage.getNotification().getTitle();
            text = remoteMessage.getNotification().getBody();
    }

    private void saveNotification(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

       // model = new NotificationModel(0, title, text,  dateFormat.format(date), true, notificationType, serviceName);
       // DataManager dm = new DataManager(this);
       // model.setId(dm.newNotification(model));
       // count = dm.unReadNotificationCount();
    }

    private Intent createIntentNotification(String title) {
        Intent intent = new Intent();
        intent.setAction("NEW_NOTIFICATION");
        intent.putExtra("notification", title);

        return intent;
    }

    @Override
    public void onDeletedMessages() {

    }
}
