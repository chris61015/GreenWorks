package com.dartmouth.cs.greenworks.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.dartmouth.cs.greenworks.Utils.BackendTest;
import com.dartmouth.cs.greenworks.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Varun on 2/18/16.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                String message = extras.getString("message");
                if (message != null && message.length() != 0) {
                    try {
                        long treeId = Long.parseLong(message);
                        showToast("Tree " + treeId + " Updated!");
                        showNotification("Tree " + treeId + " Updated!", treeId);
                    } catch (Exception e) {

                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNotification(String text, long id) {
        String title = "GreenWorks";

        Log.d("IntentService", "Starting notification");

        // resume activity when notification is clicked.
        // remember to set launchMode = singleInstance in manifest.
        Intent myIntent = new Intent(this, com.dartmouth.cs.greenworks.Activity.TreeDetailActivity.class);

        BackendTest backend = new BackendTest();
        backend.getTreeByIDTest(id, myIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
                0, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        long[] v = {500, 1000};

        // Build notification.
        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.dartmouthpine)
                .setContentIntent(pendingIntent)
                .setVibrate(v)
                .build();

        // Doesn't allow system to cancel notification when activity is running.
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify((int) id, notification);
    }
}
