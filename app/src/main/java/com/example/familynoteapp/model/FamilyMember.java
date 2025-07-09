package com.example.familynoteapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "family_members")
public class FamilyMember implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String relationship;
    public String birthday;
    public String photoUri;
}