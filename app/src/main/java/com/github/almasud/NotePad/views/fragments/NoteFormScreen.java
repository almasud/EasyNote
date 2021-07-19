package com.github.almasud.NotePad.views.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.almasud.NotePad.BaseApplication;
import com.github.almasud.NotePad.R;
import com.github.almasud.NotePad.databinding.FragmentNoteFormBinding;
import com.github.almasud.NotePad.models.Note;
import com.github.almasud.NotePad.viewmodels.NoteVM;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import yuku.ambilwarna.AmbilWarnaDialog;

public class NoteFormScreen extends Fragment {
    private static final String TAG = "NoteFormScreen";
    private FragmentNoteFormBinding mViewBinding;
    private NoteVM mNoteVM;
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
            "dd-MM-yyyy", Locale.getDefault()
    );
    private Note mNote;
    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int mInitialColor;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mViewBinding = FragmentNoteFormBinding.inflate(inflater, container, false);
        // Get note from argument
        if (NoteFormScreenArgs.fromBundle(getArguments()).getNote() != null) {
            mNote = NoteFormScreenArgs.fromBundle(getArguments()).getNote();
        }
        // Initialize the UI
        initUI();

        // Submit button click event listener
        mViewBinding.buttonSubmit.setOnClickListener(v -> {
            saveNoteIntoDb();
        });

        return mViewBinding.getRoot();
    }

    private void initUI() {
        // Set initial color
        mInitialColor = getResources().getColor(R.color.colorSecondary);

        // Listener for date picker
        mDateSetListener = (view, year, month, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Set the picked date into UI
            mViewBinding.etDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        };

        // Listener for click event of date field
        mViewBinding.etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(), mDateSetListener, mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)
            );

            // Show the dialog
            datePickerDialog.show();
            datePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    getResources().getColor(R.color.colorSecondary)
            );
            datePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    getResources().getColor(R.color.colorSecondary)
            );
        });

        // Listener for click event of color field
        mViewBinding.etColor.setOnClickListener(v -> {
            AmbilWarnaDialog dialog = new AmbilWarnaDialog(
                    getContext(), mInitialColor,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {

                        }

                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            // Assign the initial color
                            mInitialColor = color;
                            mViewBinding.etColor.setBackgroundColor(mInitialColor);
                        }
                    }
            );

            // Show the dialog
            dialog.show();
            dialog.getDialog().getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    getResources().getColor(R.color.colorSecondary)
            );
            dialog.getDialog().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    getResources().getColor(R.color.colorSecondary)
            );
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the VM
        mNoteVM = new NoteVM(getActivity().getApplication());

        if (mNote != null) {
            // Change the button text create to update
            mViewBinding.buttonSubmit.setText(getString(R.string.update));
            // Set the note into UI
            setNoteToUI(mNote);
        }

    }

    private void setNoteToUI(Note note) {
        // Try to set the saved date into calendar
        try {
            mCalendar.setTime(mSimpleDateFormat.parse(note.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Set saved note data into UI
        mViewBinding.etTitle.setText(note.getTitle());
        mViewBinding.etDetails.setText(note.getDetails());
        mViewBinding.etDate.setText(note.getDate());
        mViewBinding.etColor.setBackgroundColor(note.getColor());
    }

    private void saveNoteIntoDb() {
        String title = mViewBinding.etTitle.getText().toString();
        String details = mViewBinding.etDetails.getText().toString();
        String date = mViewBinding.etDate.getText().toString();
        // Get selected color
        int color = Color.TRANSPARENT;
        Drawable background = mViewBinding.etColor.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();

        // Validate the form fields
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(details)
                || TextUtils.isEmpty(date)) {
            Log.d(TAG, "updateProfile: Data is not valid yet!");
            // Validate the fields individually
            if (TextUtils.isEmpty(title)) {
                mViewBinding.etTitle.setError("Title is required");
                Log.d(TAG, "saveNoteIntoDb: Title is required");
            }
            if (TextUtils.isEmpty(details)) {
                mViewBinding.etDetails.setError("Details is required");
                Log.d(TAG, "saveNoteIntoDb: Details is required");
            }
            if (TextUtils.isEmpty(date)) {
                mViewBinding.etDate.setError("Date is required");
                Log.d(TAG, "saveNoteIntoDb: Date is required");
            }
        } else {
            // Show the progressbar layout and disable the UI
            mViewBinding.noteFormScreenProgressBar.progressBarText.setText(getString(R.string.note_save_wait_message));
            mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.VISIBLE);
            if (isAdded())
                BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), false);

            // Try to save the note
            if (mNote != null) {
                mNoteVM.update(new Note(
                        mNote.getId(), title, details, date, color, mNote.isFavorite()
                        )).observe(getViewLifecycleOwner(), isUpdated -> {
                    // Hide the progressbar layout and enable the UI
                    mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.GONE);
                    if (isAdded())
                        BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), true);
                    Snackbar snackbar;
                    if (isUpdated) {
                        snackbar = Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Note successfully updated!",
                                Snackbar.LENGTH_SHORT
                        );
                        snackbar.getView().setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.colorSuccess)
                        );

                        // Exit the screen after successfully saved
                        new Handler().postDelayed(() ->
                                        Navigation.findNavController(mViewBinding.getRoot()).popBackStack(),
                                2000);
                    } else {
                        snackbar = Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Couldn't update the note!",
                                Snackbar.LENGTH_SHORT
                        );
                        snackbar.getView().setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.colorFailed)
                        );
                    }
                    snackbar.show();
                });
            } else {
                mNoteVM.insert(new Note(
                        title, details, date, color, false
                        )).observe(getViewLifecycleOwner(), insertedId -> {
                    // Hide the progressbar layout and enable the UI
                    mViewBinding.noteFormScreenProgressBar.getRoot().setVisibility(View.GONE);
                    Snackbar snackbar;
                    if (insertedId != null) {
                        snackbar = Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Note successfully saved!",
                                Snackbar.LENGTH_SHORT
                        );
                        snackbar.getView().setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.colorSuccess)
                        );

                        // Exit the screen after successfully saved
                        new Handler().postDelayed(() ->
                                Navigation.findNavController(mViewBinding.getRoot()).popBackStack(),
                                2000);
                    } else {
                        snackbar = Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Couldn't save the note!",
                                Snackbar.LENGTH_SHORT
                        );
                        snackbar.getView().setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.colorFailed)
                        );
                    }
                    snackbar.show();
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewBinding = null;
    }

}