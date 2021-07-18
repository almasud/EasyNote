package com.github.almasud.NotePad.databases;

import android.content.Context;

import com.github.almasud.NotePad.models.Note;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDb extends RoomDatabase {
    private static volatile NoteDb database;

    public abstract NoteDao getNoteDao();

    public static NoteDb getInstance(final Context context) {
        synchronized (NoteDb.class) {
            if (database == null) {
                database = Room.databaseBuilder(context, NoteDb.class, "note_db")
                        .build();
            }
        }
        return database;
    }
}
