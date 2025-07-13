package com.example.familynoteapp.worker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.familynoteapp.R;
import com.example.familynoteapp.db.AppDatabase;
import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.FamilyMember;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BirthdayCheckWorker extends Worker {

    public BirthdayCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        String today = new SimpleDateFormat("dd-MM", Locale.getDefault()).format(new Date());
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        // Truy vấn thành viên có sinh nhật hôm nay
        List<FamilyMember> members = db.familyMemberDao().getBirthdaysToday(today);

        if (!members.isEmpty()) {
            for (FamilyMember member : members) {
                sendBirthdayNotification(member.name);
            }
        }

        return Result.success();
    }


    // Gửi thông báo sinh nhật
    private void sendBirthdayNotification(String name) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "birthday_channel")
                .setSmallIcon(R.drawable.ic_birthday)  // Đảm bảo bạn có icon thông báo
                .setContentTitle("Chúc mừng sinh nhật!")
                .setContentText("Chúc mừng sinh nhật " + name + " 🎉")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(name.hashCode(), builder.build());
    }
}
