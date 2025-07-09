package com.example.familynoteapp.feture.schedule.service;

import android.content.Context;
import android.util.Log;

import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.ScheduleTask;
import com.example.familynoteapp.util.NotificationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleHelper {
    public static void checkAndNotify(Context context) {
        List<ScheduleTask> tasks = AppDatabaseSingleton.getInstance(context).scheduleDao().getAllSchedulesSnapshot();
        long now = System.currentTimeMillis();
        Log.d("Vinhnc", "Số lượng lịch trình: " + tasks.size());
        Log.d("vinhnc", "Time schedule " + now);

        for (ScheduleTask task : tasks) {
            if (!task.isCompleted()) {
                long taskTime = parseDateTime(task.getDateTime());
                if (taskTime <= now && taskTime >= now - 10 * 60 * 1000) {
                    NotificationUtils.sendNotification(context, task);
                }
            }
        }
    }

    private static long parseDateTime(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateTime);
            return date != null ? date.getTime() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

