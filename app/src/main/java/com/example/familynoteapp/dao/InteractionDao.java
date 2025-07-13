package com.example.familynoteapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.familynoteapp.model.Interaction;

import java.util.List;

@Dao
public interface InteractionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Interaction interaction);

    @Update
    void update(Interaction interaction);

    @Delete
    void delete(Interaction interaction);

    @Query("SELECT * FROM interactions WHERE memberId = :memberId ORDER BY date DESC")
    LiveData<List<Interaction>> getInteractionsForMember(int memberId);

    @Query("SELECT * FROM interactions ORDER BY date DESC")
    LiveData<List<Interaction>> getAllInteractions();

    @Query("SELECT * FROM interactions WHERE id = :id")
    Interaction getById(int id);
    @Query("SELECT * FROM interactions WHERE id = :id")
    LiveData<Interaction> getById2(int id);

    @Query("SELECT COUNT(*) FROM interactions")
    LiveData<Integer> getTotalCount();

    @Query("SELECT family_members.name FROM family_members " +
            "INNER JOIN interactions ON family_members.id = interactions.memberId " +
            "GROUP BY interactions.memberId " +
            "ORDER BY COUNT(interactions.id) DESC LIMIT 1")
    LiveData<String> getTopInteractedMemberName();

    @Query("SELECT family_members.name AS name, COUNT(interactions.id) AS count " +
            "FROM interactions INNER JOIN family_members ON interactions.memberId = family_members.id " +
            "GROUP BY memberId")
    LiveData<List<MemberInteractionCount>> getInteractionCountPerMember();


    @Query("SELECT family_members.name AS name, COUNT(*) AS count FROM" +
            " interactions INNER JOIN family_members ON interactions.memberId = family_members.id GROUP BY memberId")
    LiveData<List<MemberInteractionCount>> getInteractionCountsPerMember();
}
