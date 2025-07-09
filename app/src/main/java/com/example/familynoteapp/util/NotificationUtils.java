package com.example.familynoteapp.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.familynoteapp.R;
import com.example.familynoteapp.model.ScheduleTask;

public class NotificationUtils {
    public static void sendNotification(Context context, ScheduleTask task) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "schedule_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Schedule Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Lịch trình tới hạn")
                .setContentText("Công việc: " + task.getTitle())
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true);
        manager.notify(task.getId(), builder.build());
    }
}