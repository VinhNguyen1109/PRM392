package com.example.familynoteapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.familynoteapp.db.AppDatabase;
import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.FamilyMember;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<FamilyMember>> allFamily;
    private final AppDatabase db;

    public MainViewModel(@NonNull Application application) {
        super(application);
         db = AppDatabaseSingleton.getInstance(application);
        allFamily = db.familyMemberDao().getAll();
//        for (FamilyMember member : allFamily.getValue()) {
//            android.util.Log.d("MainViewModel", "name: "+member.name+" date : "+member.birthday);
//        }
        allFamily.observeForever(list -> {
            android.util.Log.d("MainViewModel", "Tổng số người thân: " + (list != null ? list.size() : 0));
        });
    }

    public LiveData<List<FamilyMember>> getAllFamily() {
        return allFamily;
    }

    public void deleteMember(FamilyMember member) {
        db.familyMemberDao().delete(member);
    }

    public void updateMember(FamilyMember member) {
        db.familyMemberDao().update(member);
    }
}