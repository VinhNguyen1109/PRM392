package com.example.familynoteapp.feture.interaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.familynoteapp.db.AppDatabaseSingleton;
import com.example.familynoteapp.model.FamilyMember;
import com.example.familynoteapp.model.Interaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InteractionViewModel extends AndroidViewModel {

    private final ExecutorService executorService;

    public InteractionViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Interaction>> getInteractionsForMember(int memberId) {
        return AppDatabaseSingleton.getInstance(getApplication())
                .interactionDao()
                .getInteractionsForMember(memberId);
    }

    public void insert(Interaction interaction) {
        executorService.execute(() -> AppDatabaseSingleton.getInstance(getApplication())
                .interactionDao().insert(interaction));
    }

    public void update(Interaction interaction) {
        executorService.execute(() -> AppDatabaseSingleton.getInstance(getApplication())
                .interactionDao().update(interaction));
    }

    public void delete(Interaction interaction) {
        executorService.execute(() -> AppDatabaseSingleton.getInstance(getApplication())
                .interactionDao().delete(interaction));
    }

    public LiveData<FamilyMember> getMemberById(int memberId) {
        return AppDatabaseSingleton.getInstance(getApplication())
                .familyMemberDao()
                .getMemberById(memberId);
    }


    public LiveData<Interaction> getInteractionById(int interactionId) {
        return AppDatabaseSingleton.getInstance(getApplication())
                .interactionDao()
                .getById2(interactionId);
    }
}
