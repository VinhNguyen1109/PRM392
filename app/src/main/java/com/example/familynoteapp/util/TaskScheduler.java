package com.example.familynoteapp.util;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.familynoteapp.model.ScheduleTask;
import com.example.familynoteapp.worker.NotificationWorker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {

    public static void scheduleTaskNotification(Context context, ScheduleTask task) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(task.getDateTime());
            long delay = date.getTime() - System.currentTimeMillis();

            if (delay <= 0) return;

            Data inputData = new Data.Builder()
                    .putString("title", "Sự kiện: " + task.getTitle())
                    .putString("desc", task.getDescription())
                    .build();

            String tag = "schedule_" + task.getId();

            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(tag)
                    .build();

            WorkManager workManager = WorkManager.getInstance(context);
            workManager.cancelAllWorkByTag(tag);  // ❌ Xoá trước
            workManager.enqueue(request);         // ✅ Sau đó enqueue lại

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
