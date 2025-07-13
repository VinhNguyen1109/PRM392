package com.example.familynoteapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.familynoteapp.model.FamilyMember;

import java.util.List;

@Dao
public interface FamilyMemberDao {
    @Query("SELECT * FROM family_members ORDER BY name")
    LiveData<List<FamilyMember>> getAll();

    @Insert
    long insert(FamilyMember member);

    @Update
    void update(FamilyMember member);

    @Delete
    void delete(FamilyMember member);

    @Query("SELECT * FROM family_members WHERE id = :id LIMIT 1")
    LiveData<FamilyMember> getMemberById(int id);


    @Query("SELECT * FROM family_members WHERE strftime('%d-%m', birthday) = :today")
    List<FamilyMember> getBirthdaysToday(String today);
}
