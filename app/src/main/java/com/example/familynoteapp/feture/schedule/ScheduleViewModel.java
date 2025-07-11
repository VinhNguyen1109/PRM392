package com.example.familynoteapp.feture.schedule;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.familynoteapp.dao.ScheduleDao;
import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.ScheduleTask;
import com.example.familynoteapp.util.TaskScheduler;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScheduleViewModel extends AndroidViewModel {
    private final ScheduleDao scheduleDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        scheduleDao = AppDatabaseSingleton.getInstance(application).scheduleDao();
    }

    public LiveData<List<ScheduleTask>> getTasksForMember(int memberId) {
        return scheduleDao.getTasksForMember(memberId);
    }

    public void insert(ScheduleTask task) {
        executor.execute(() -> scheduleDao.insertAndReturnId(task));
    }

    public void insertAndSchedule(ScheduleTask task, Context context) {
        executor.execute(() -> {
            // Lấy ID trả về sau khi insert
            long id = AppDatabaseSingleton.getInstance(context).scheduleDao().insertAndReturnId(task);
            task.setId((int) id);
            TaskScheduler.scheduleTaskNotification(context, task);
        });
    }

    public void update(ScheduleTask task) {
        executor.execute(() -> scheduleDao.update(task));
    }

    public void delete(ScheduleTask task) {
        executor.execute(() -> scheduleDao.delete(task));
    }

    public LiveData<List<ScheduleTask>> getAllSchedules() {
        return scheduleDao.getAllSchedules();
    }
}
