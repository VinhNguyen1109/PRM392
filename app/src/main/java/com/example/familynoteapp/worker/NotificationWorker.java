// File: com/example/familynoteapp/worker/NotificationWorker.java
package com.example.familynoteapp.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.familynoteapp.R;
import com.example.familynoteapp.feture.schedule.ScheduleActivity;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString("title");
        String desc = getInputData().getString("desc");
        Log.d("NotificationWorker", "🔥 NotificationWorker is running");
        showNotification(title, desc);
        return Result.success();
    }

    private void showNotification(String title, String desc) {
        String channelId = "schedule_channel";
        Context context = getApplicationContext();

        // 🔁 Tạo intent để mở app (ScheduleActivity)
        Intent intent = new Intent(context, ScheduleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                        PendingIntent.FLAG_IMMUTABLE :
                        0
        );

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Lịch nhắc", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // đổi icon nếu cần
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)  // ✅ gắn intent
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("NotificationWorker", "🟢 Gửi thông báo: " + title);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }


}