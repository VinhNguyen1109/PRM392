package com.example.familynoteapp.feture.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.familynoteapp.dao.MemberInteractionCount;
import com.example.familynoteapp.db.AppDatabase;
import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.FamilyMember;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    private final AppDatabase db;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabaseSingleton.getInstance(application);
    }

    public LiveData<Integer> getTotalInteractionCount() {
        return db.interactionDao().getTotalCount();
    }

    public LiveData<String> getTopInteractedMemberName() {
        return db.interactionDao().getTopInteractedMemberName();
    }

    public LiveData<FamilyMember> getMemberById(int memberId) {
        return AppDatabaseSingleton.getInstance(getApplication())
                .familyMemberDao()
                .getMemberById(memberId);
    }

    public LiveData<List<MemberInteractionCount>> getInteractionCountsPerMember() {
        return AppDatabaseSingleton.getInstance(getApplication())
                .interactionDao()
                .getInteractionCountsPerMember();
    }

}
