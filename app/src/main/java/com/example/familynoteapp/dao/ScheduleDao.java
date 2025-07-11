package com.example.familynoteapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.familynoteapp.model.ScheduleTask;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Query("SELECT * FROM schedule_tasks WHERE memberId = :memberId ORDER BY dateTime")
    LiveData<List<ScheduleTask>> getTasksForMember(int memberId);

    @Insert
    long insertAndReturnId(ScheduleTask task);

    @Update
    void update(ScheduleTask task);

    @Delete
    void delete(ScheduleTask task);
    @Query("SELECT * FROM schedule_tasks ORDER BY dateTime ASC")
    LiveData<List<ScheduleTask>> getAllSchedules();

    @Query("SELECT * FROM schedule_tasks ORDER BY dateTime ASC")
    List<ScheduleTask> getAllSchedulesSnapshot();
}

