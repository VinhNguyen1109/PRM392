package com.example.familynoteapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(
        tableName = "schedule_tasks",
        foreignKeys = @ForeignKey(
                entity = FamilyMember.class,
                parentColumns = "id",
                childColumns = "memberId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = "memberId")
)
public class ScheduleTask implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int memberId;

    public String title;
    public String description;
    public String dateTime; // bạn có thể dùng Date và TypeConverter

    public boolean completed = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}

