package com.programmerbaper.skripsi.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.programmerbaper.skripsi.R;
import com.programmerbaper.skripsi.activities.DetailTransaksiActivity;
import com.programmerbaper.skripsi.misc.CurrentActivityContext;
import com.programmerbaper.skripsi.misc.NotificationID;
import com.programmerbaper.skripsi.model.api.Transaksi;
import com.programmerbaper.skripsi.retrofit.api.APIClient;
import com.programmerbaper.skripsi.retrofit.api.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsi.misc.Config.BASE_URL;
import static com.programmerbaper.skripsi.misc.Config.DATA_TRANSAKSI;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createNotificationChannel();
        getTransaksiByID(remoteMessage);




    }

    private void showNotification(Transaksi transaksi, RemoteMessage remoteMessage) {

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, DetailTransaksiActivity.class);
        resultIntent.putExtra(DATA_TRANSAKSI, transaksi);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_pedagang);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(icon)
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        builder.setContentIntent(resultPendingIntent);
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

    private void getTransaksiByID(final RemoteMessage remoteMessage) {

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<Transaksi> call = apiInterface.transaksiByIDGet(Integer.parseInt(remoteMessage.getData().get("id_transaksi")));

        call.enqueue(new Callback<Transaksi>() {
            @Override
            public void onResponse(Call<Transaksi> call, Response<Transaksi> response) {

                final Transaksi transaksi = response.body();
                showNotification(transaksi, remoteMessage);
                if (CurrentActivityContext.getActualContext() != null) {
                    //Let this be the code in your n'th level thread from main UI thread

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            showDialog(CurrentActivityContext.getActualContext(),transaksi);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Transaksi> call, Throwable t) {

            }
        });


    }

    private void showDialog(Context context, Transaksi transaksi) {


        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentActivityContext.getActualContext());
        View rootDialog = LayoutInflater.from(CurrentActivityContext.getActualContext()).inflate(R.layout.dialogue_notif_pesanan, null);

        builder.setView(rootDialog);
        final AlertDialog dialog = builder.create();
        dialog.show();

        ImageView image = rootDialog.findViewById(R.id.dialogue_image);

        Glide.with(context)
                .load(BASE_URL + "storage/pembeli-profiles/" + transaksi.getFoto())
                .placeholder(R.drawable.pembeli_holder)
                .into(image);


        Button no = rootDialog.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button ok = rootDialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //TODO Intent to Detail Here


            }
        });


    }


}

