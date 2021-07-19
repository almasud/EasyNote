package com.github.almasud.EasyNote.databases;

import com.github.almasud.EasyNote.models.Note;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * DAO object for {@link NoteDb}.
 * @author Abdullah Almasud
 */

@Dao
public interface NoteDao {
    @Insert
    Single<Long> insert(Note note);

    @Insert
    Single<List<Long>> insert(Note... notes);

    @Query("SELECT * FROM tbl_note ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN col_note_id END ASC," +
            "CASE WHEN :isAsc = 0 THEN col_note_id END DESC")
    Single<List<Note>> getNotes(boolean isAsc);

    @Query("SELECT * FROM tbl_note WHERE col_note_id=:id")
    Single<Note> getNote(int id);

    @Query("SELECT COUNT(col_note_title) FROM tbl_note")
    Single<Integer> getNoteCount();

    @Query("SELECT * FROM tbl_note WHERE col_note_favorite=1 ORDER BY " +
                       "CASE WHEN :isAsc = 1 THEN col_note_id END ASC," +
                       "CASE WHEN :isAsc = 0 THEN col_note_id END DESC")
    Single<List<Note>> getFavoriteNotes(boolean isAsc);

    @Query("SELECT * FROM tbl_note WHERE col_note_id=:id AND col_note_favorite=1")
    Single<Note> getFavoriteNote(int id);

    @Update
    Completable update(Note note);

    @Delete
    Completable delete(Note note);
}
