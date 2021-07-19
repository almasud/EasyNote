package com.github.almasud.NotePad.models;

import java.io.Serializable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * The entity model class of {@link Note}.
 * Automatically generate SQLite database depend on of each annotation.
 * @author Abdullah Almasud
 */

@Keep
@Entity(tableName = "tbl_note")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "col_note_id")
    private int id;
    @ColumnInfo(name = "col_note_title")
    @NonNull
    private String title;
    @ColumnInfo(name = "col_note_details")
    @NonNull
    private String details;
    @ColumnInfo(name = "col_note_date")
    @NonNull
    private String date;
    @ColumnInfo(name = "col_note_color")
    @NonNull
    private int color;
    @ColumnInfo(name = "col_note_favorite")
    @NonNull
    private boolean favorite;

    @Ignore
    public Note() {
    }

    public Note(@NonNull String title, @NonNull String details, @NonNull String date,
                int color, boolean favorite) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.color = color;
        this.favorite = favorite;
    }

    @Ignore
    public Note(int id, @NonNull String title, @NonNull String details,
                @NonNull String date, int color, boolean favorite) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.date = date;
        this.color = color;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getDetails() {
        return details;
    }

    public void setDetails(@NonNull String details) {
        this.details = details;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
