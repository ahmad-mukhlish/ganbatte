package com.programmerbaper.skripsi.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.misc.NotificationID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createNotificationChannel();
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_pedagang);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(icon)
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotificationID.getID(), builder.build());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Coba";
            String description = "CIK";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}

