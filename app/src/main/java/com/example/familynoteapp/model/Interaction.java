package com.example.familynoteapp.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
@Entity(tableName = "interactions",
        foreignKeys = @ForeignKey(entity = FamilyMember.class,
                parentColumns = "id",
                childColumns = "memberId",
                onDelete = CASCADE))
public class Interaction implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int memberId;
    public String type; // call, meet, gift
    public String note;
    public String photoUri;
    public Date date;
}
