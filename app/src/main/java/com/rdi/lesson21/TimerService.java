package com.rdi.lesson21;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerService extends Service {

    private static final String CHANNEL_1_ID = "Chanal_1";
    private static final String CHANNEL_2_ID = "Chanal_2";
    private static final String CHANNEL_3_ID = "Chanal_3";
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ID_2 = 2;
    private int maxTimerLenght = 3_000;
    private int periodForTimer = 1_000;

    CountDownTimer mCountDownTimer;

    private static final String ACTION_CLOSE = "TimerClose";

    private static final String TAG = "TimerService";


    @Override
    public void onCreate() {
        createNotificationChanel();
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

    }

    private void startCountDownTimer(long time, long period) {
        mCountDownTimer = new CountDownTimer(time, period) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + millisUntilFinished/1000);
                updateNotification(millisUntilFinished / 1000 + "");
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFinish() {

                stopForeground(true);

                Log.d(TAG, "onFinish: ");
            }
        };
        mCountDownTimer.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            if (ACTION_CLOSE.equals(intent.getAction())) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
                stopSelf();
            }
        }

        startCountDownTimer(maxTimerLenght, periodForTimer);
        startForeground(NOTIFICATION_ID, createNotification(String.valueOf(maxTimerLenght)));
        startForeground(NOTIFICATION_ID_2, createNotificationForSecondChannal(String.valueOf(maxTimerLenght)));


        return START_NOT_STICKY;
    }



    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel 1 name", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Channel 1 description");

            NotificationChannel notificationChannelSecond = new NotificationChannel(CHANNEL_2_ID,
                    "Channel 2 name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannelSecond.setDescription("Channel 2 description");

            NotificationChannel notificationChannelTherd = new NotificationChannel(CHANNEL_3_ID,
                    "Channel 3 name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannelTherd.setDescription("Channel 3 description");


        NotificationManager notificationManager = getSystemService((NotificationManager.class));
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.createNotificationChannel(notificationChannelSecond);
        notificationManager.createNotificationChannel(notificationChannelTherd);
        }
    }


    private void updateNotification(String time) {
        Notification notification = createNotification(time);
        Notification createNotificationForSecondChannal = createNotificationForSecondChannal(time);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        notificationManagerCompat.notify(NOTIFICATION_ID_2, createNotificationForSecondChannal);
    }

    private Notification createNotification(String timer) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent closeServiseIntent = new Intent(this, TimerService.class);
        closeServiseIntent.setAction(ACTION_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeServiseIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
        .setSmallIcon(R.mipmap.ic_launcher_round)
                .addAction(R.drawable.ic_delete_black_24dp,"Stop service", closePendingIntent)
                .setContentTitle("Timer Service")
                .setContentText("Осталось " + timer)
                .setProgress(maxTimerLenght/periodForTimer,
                        maxTimerLenght/periodForTimer - Integer.parseInt(timer),
                        false)
                .setContentIntent(pendingIntent);

        return builder.build();
    }


    private Notification createNotificationForSecondChannal(String timer) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent closeServiseIntent = new Intent(this, TimerService.class);
        closeServiseIntent.setAction(ACTION_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeServiseIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .addAction(R.drawable.ic_delete_black_24dp,"Stop service", closePendingIntent)
                .setContentTitle("Timer Service")
                .setContentText("Осталось " + timer)
                .setProgress(maxTimerLenght/periodForTimer,
                         Integer.parseInt(timer),
                        false)
                .setColor(Color.GREEN)
                .setTicker("Важно")
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
