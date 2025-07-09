package com.example.familynoteapp.feture.schedule.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.example.familynoteapp.R;

public class ScheduleForegroundService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = "schedule_service_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Schedule Service", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("FamilyNoteApp")
                .setContentText("Đang quét lịch trình...")
                .setSmallIcon(R.drawable.ic_notification)
                .build();
        startForeground(1, notification);

        new Thread(() -> {
            while (true) {
                ScheduleHelper.checkAndNotify(getApplicationContext());
                try { Thread.sleep(60000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
