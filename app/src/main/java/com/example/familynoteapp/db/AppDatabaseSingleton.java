package com.example.familynoteapp.db;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseSingleton {
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "family_db")
                    .fallbackToDestructiveMigration() // Allows destructive migration if schema changes
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}