package com.github.almasud.EasyNote.viewmodels;

import android.app.Application;
import android.util.Log;

import com.github.almasud.EasyNote.databases.NoteDao;
import com.github.almasud.EasyNote.databases.NoteDb;
import com.github.almasud.EasyNote.models.Note;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteVM extends AndroidViewModel {
    private static final String TAG = "NoteVM";
    private final NoteDao mNoteDao;
    private final MutableLiveData<Long> mInsertMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Long>> mInsertsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Note>> mNotesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Note> mNoteMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Note>> mFavoritesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Note> mFavoriteMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDeleteMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mNoteCountMutableLiveData = new MutableLiveData<>();
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public NoteVM(@NonNull Application application) {
        super(application);
        NoteDb noteDb = NoteDb.getInstance(application);
        mNoteDao = noteDb.getNoteDao();

        // Load notes
        loadNotes();
        loadFavoriteNotes();
        loadNotesCount();
    }

    public LiveData<Long> insert(Note note) {
        mNoteDao.insert(note).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NotNull Long insertId) {
                        mInsertMutableLiveData.setValue(insertId);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to insert data: " + e.getMessage());
                        mInsertMutableLiveData.setValue(null);
                    }
                });

        return mInsertMutableLiveData;
    }

    public LiveData<List<Long>> insert(Note... notes) {
        mNoteDao.insert(notes).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Long>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NotNull List<Long> insertIds) {
                        mInsertsMutableLiveData.setValue(insertIds);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to insert data: " + e.getMessage());
                        mInsertsMutableLiveData.setValue(null);
                    }
                });

        return mInsertsMutableLiveData;
    }

    public LiveData<Boolean> update(Note note) {
        mNoteDao.update(note).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mUpdateMutableLiveData.setValue(true);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to update data: " + e.getMessage());
                        mUpdateMutableLiveData.setValue(false);
                    }
                });

        return mUpdateMutableLiveData;
    }

    public LiveData<Note> getNote(int id) {
        mNoteDao.getNote(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Note>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NotNull Note note) {
                        mNoteMutableLiveData.setValue(note);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to retrieve data: " + e.getMessage());
                        mNoteMutableLiveData.setValue(null);
                    }
                });

        return mNoteMutableLiveData;
    }

    public LiveData<Note> getFavoriteNote(int id) {
        mNoteDao.getFavoriteNote(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Note>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NotNull Note note) {
                        mFavoriteMutableLiveData.setValue(note);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to retrieve data: " + e.getMessage());
                        mFavoriteMutableLiveData.setValue(null);
                    }
                });

        return mFavoriteMutableLiveData;
    }

    public LiveData<List<Note>> getNotes() {
        return mNotesMutableLiveData;
    }

    public LiveData<List<Note>> getFavoriteNotes() {
        return mFavoritesMutableLiveData;
    }

    public LiveData<Integer> getNotesSize() {
        return mNoteCountMutableLiveData;
    }

    public LiveData<Boolean> delete(Note note) {
        mNoteDao.delete(note).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDeleteMutableLiveData.setValue(true);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to delete data: " + e.getMessage());
                        mDeleteMutableLiveData.setValue(false);
                    }
                });

        return mDeleteMutableLiveData;
    }

    private void loadNotes() {
        mNoteDao.getNotes(false).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Note>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NotNull List<Note> notes) {
                        mNotesMutableLiveData.setValue(notes);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to load data: " + e.getMessage());
                        mNotesMutableLiveData.setValue(null);
                    }
                });
    }

    private void loadFavoriteNotes() {
        mNoteDao.getFavoriteNotes(false).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Note>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NotNull List<Note> notes) {
                        mFavoritesMutableLiveData.setValue(notes);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to load data: " + e.getMessage());
                        mFavoritesMutableLiveData.setValue(null);
                    }
                });
    }

    private void loadNotesCount() {
        mNoteDao.getNoteCount().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NotNull Integer totalNotes) {
                        mNoteCountMutableLiveData.setValue(totalNotes);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.w(TAG, "onError: Failed to load notes count: " + e.getMessage());
                        mNoteCountMutableLiveData.setValue(null);
                    }
                });
    }

    @Override
    protected void onCleared() {
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable.clear();
        }

        super.onCleared();
        Log.i(TAG, "View model destroyed");
    }
}
