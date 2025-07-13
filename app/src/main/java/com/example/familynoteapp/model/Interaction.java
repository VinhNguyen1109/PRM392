package com.example.familynoteapp.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.familynoteapp.util.Converters;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(tableName = "interactions",
        foreignKeys = @ForeignKey(entity = FamilyMember.class,
                parentColumns = "id",
                childColumns = "memberId",
                onDelete = CASCADE))
@TypeConverters({Converters.class})
public class Interaction implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int memberId;
    public String type; // call, meet, gift
    public String note;
    public String photoUri;  // Ảnh đại diện

    public Date date;

    public List<String> extraPhotoUris; // Ảnh bổ sung

    // --- Constructors ---

    public Interaction() { }

    public Interaction(int memberId, String type, String note, String photoUri, Date date, List<String> extraPhotoUris) {
        this.memberId = memberId;
        this.type = type;
        this.note = note;
        this.photoUri = photoUri;
        this.date = date;
        this.extraPhotoUris = extraPhotoUris;
    }

    // --- Getter & Setter (có thể generate tự động) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getExtraPhotoUris() {
        return extraPhotoUris;
    }

    public void setExtraPhotoUris(List<String> extraPhotoUris) {
        this.extraPhotoUris = extraPhotoUris;
    }
}
