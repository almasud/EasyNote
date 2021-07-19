package com.github.almasud.NotePad.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.almasud.NotePad.BaseApplication;
import com.github.almasud.NotePad.R;
import com.github.almasud.NotePad.databinding.FragmentNoteDetailsBinding;
import com.github.almasud.NotePad.models.Note;
import com.github.almasud.NotePad.viewmodels.NoteVM;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class NoteDetailsScreen extends Fragment {
    private static final String TAG = "NoteDetailsScreen";
    private FragmentNoteDetailsBinding mViewBinding;
    private NoteVM mNoteVM;
    private Note mNote;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private SimpleDateFormat mDateFormatDay = new SimpleDateFormat(
            "dd", Locale.getDefault()
    );
    private SimpleDateFormat mDateFormatMonth = new SimpleDateFormat(
            "MMM", Locale.getDefault()
    );

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mViewBinding = FragmentNoteDetailsBinding.inflate(inflater, container, false);
        // Get the NoteVM
        mNoteVM = new NoteVM(getActivity().getApplication());
        // Set listeners for action buttons click events
        mViewBinding.ivButtonFavorite.setOnClickListener(v -> setFavoriteNoteIntoDb());
        mViewBinding.ivButtonEdit.setOnClickListener(v -> editNote());
        mViewBinding.ivButtonDelete.setOnClickListener(v -> deleteNoteFromDb());

        return mViewBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set Note into UI
        setNoteToUI(NoteDetailsScreenArgs.fromBundle(getArguments()).getNoteId());
    }

    void setNoteToUI(int noteId) {
        // Show the progressbar layout and disable the UI
        mViewBinding.noteFormScreenProgressBar.progressBarText.setText(getString(R.string.note_retrieving_wait_message));
        mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.VISIBLE);
        if (isAdded())
            BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), false);

        // Get the note from VM
        mNoteVM.getNote(noteId).observe(getViewLifecycleOwner(), note -> {
            // Hide the progressbar layout and enable the UI
            mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.GONE);
            if (isAdded())
                BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), true);
            if (note != null) {
                Log.d(TAG, "setNoteToUI: note observer is called and note.getId(): " + note.getId());
                // Set the reference of mNote
                mNote = note;

                // Set the color of note date section
                mViewBinding.tvDateDay.setTextColor(note.getColor());
                mViewBinding.tvDateMonth.setTextColor(note.getColor());
                mViewBinding.viewSeparator.setBackgroundColor(note.getColor());

                // Try to set the Note date into UI
                try {
                    Log.d(TAG, "setNoteToUI: date: " + note.getDate());
                    mViewBinding.tvDateDay.setText(mDateFormatDay.format(
                            mDateFormat.parse(note.getDate()))
                    );
                    mViewBinding.tvDateMonth.setText(mDateFormatMonth.format(
                            mDateFormat.parse(note.getDate()))
                    );
                } catch (ParseException e) {
                    Log.w(TAG, "setNoteToUI: Error: " + e.getMessage());
                    e.printStackTrace();
                }
                // Set the Note title and description into UI
                mViewBinding.tvNoteTitle.setText(note.getTitle());
                mViewBinding.tvNoteDetails.setText(note.getDetails());
                Log.d(TAG, "setNoteToUI: note is favorite: " + note.isFavorite());
                int favoriteIcon = note.isFavorite() ?
                        R.drawable.ic_heart_fill : R.drawable.ic_heart;

                // Set favorite note icon if the note is favorite otherwise set not favorite
                mViewBinding.ivButtonFavorite.setImageDrawable(
                        ResourcesCompat.getDrawable(getResources(), favoriteIcon, null));
            } else {
                Snackbar snackbar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Couldn't found the note!", Snackbar.LENGTH_SHORT
                );
                snackbar.getView().setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.colorFailed)
                );
                snackbar.show();
            }
        });
    }

    private void setFavoriteNoteIntoDb() {
        // Show the progressbar layout and disable the UI
        mViewBinding.noteFormScreenProgressBar.progressBarText.setText(getString(R.string.note_save_favorite_wait_message));
        mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.VISIBLE);
        if (isAdded())
            BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), false);

        // Try to save the note
        if (mNote != null) {
            boolean notFavorite = !mNote.isFavorite();
            mNoteVM.update(new Note(mNote.getId(), mNote.getTitle(), mNote.getDetails(),
                    mNote.getDate(), mNote.getColor(), notFavorite)).observe(
                            getViewLifecycleOwner(), isUpdated -> {
                        // Hide the progressbar layout and enable the UI
                        mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.GONE);
                        if (isAdded())
                            BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), true);

                        // Show the success or failure message
                        Snackbar snackbar;
                        if (isUpdated) {
                            // Update the UI
                            setNoteToUI(mNote.getId());
                            // Change the favorite icon
//                            mViewBinding.ivButtonFavorite.setImageDrawable(ResourcesCompat.getDrawable(
//                                    getResources(), notFavorite ?
//                                            R.drawable.ic_heart_fill : R.drawable.ic_heart,
//                                    null));

                            // Show a success message
                            String successMessage = notFavorite ?
                                    "This note is marked as favorite!" : "This note is unmarked as favorite!";
                            snackbar = Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    successMessage, Snackbar.LENGTH_SHORT
                            );
                            snackbar.getView().setBackgroundColor(
                                    ContextCompat.getColor(requireContext(), R.color.colorSuccess)
                            );
                        } else {
                            snackbar = Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    "Couldn't marked the note as favorite!", Snackbar.LENGTH_SHORT
                            );
                            snackbar.getView().setBackgroundColor(
                                    ContextCompat.getColor(requireContext(), R.color.colorFailed)
                            );
                        }
                        snackbar.show();
            });
        }
    }

    private void editNote() {
        if (mNote != null) {
            NoteDetailsScreenDirections.ActionNavNoteDetailsToNavNoteForm action =
                    NoteDetailsScreenDirections.actionNavNoteDetailsToNavNoteForm();
            action.setNote(mNote);
            action.setTitle("Edit Note");
            Navigation.findNavController(mViewBinding.getRoot()).navigate(action);
        }
    }

    private void deleteNoteFromDb() {
        BaseApplication.setAlertDialog(
                getContext(), null, "Delete", R.drawable.ic_delete,
                "Are you sure want to delete this note?", (BaseApplication.OnSingleAction) () -> {
                    // Show the progressbar layout and disable the UI
                    mViewBinding.noteFormScreenProgressBar.progressBarText.setText(getString(R.string.note_delete_wait_message));
                    mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.VISIBLE);
                    if (isAdded())
                        BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), false);

                    // Try to delete the note
                    if (mNote != null) {
                        mNote.setFavorite(true);
                        mNoteVM.delete(mNote).observe(getViewLifecycleOwner(), isDeleted -> {
                            // Hide the progressbar layout and enable the UI
                            mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.GONE);
                            if (isAdded())
                                BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), true);
                            Snackbar snackbar;
                            if (isDeleted) {
                                snackbar = Snackbar.make(
                                        requireActivity().findViewById(android.R.id.content),
                                        "This note is successfully deleted!",
                                        Snackbar.LENGTH_SHORT
                                );
                                snackbar.getView().setBackgroundColor(
                                        ContextCompat.getColor(requireContext(), R.color.colorSuccess)
                                );

                                // Exit the screen after successfully deleted
                                new Handler().postDelayed(() ->
                                                Navigation.findNavController(mViewBinding.getRoot()).popBackStack(),
                                        2000);
                            } else {
                                snackbar = Snackbar.make(
                                        requireActivity().findViewById(android.R.id.content),
                                        "Couldn't delete the note!",
                                        Snackbar.LENGTH_SHORT
                                );
                                snackbar.getView().setBackgroundColor(
                                        ContextCompat.getColor(requireContext(), R.color.colorFailed)
                                );
                            }
                            snackbar.show();
                        });
                    }
                }, null, () -> { }, null
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewBinding = null;
    }
}