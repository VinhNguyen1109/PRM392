package com.example.familynoteapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.familynoteapp.dao.FamilyMemberDao;
import com.example.familynoteapp.dao.InteractionDao;
import com.example.familynoteapp.dao.ScheduleDao;
import com.example.familynoteapp.model.FamilyMember;
import com.example.familynoteapp.model.Interaction;
import com.example.familynoteapp.model.ScheduleTask;

@Database(entities = {FamilyMember.class, Interaction.class, ScheduleTask.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract FamilyMemberDao familyMemberDao();
    public abstract InteractionDao interactionDao();
    public abstract ScheduleDao scheduleDao();

}
