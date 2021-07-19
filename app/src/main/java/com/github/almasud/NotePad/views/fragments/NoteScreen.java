package com.github.almasud.NotePad.views.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.almasud.NotePad.BaseApplication;
import com.github.almasud.NotePad.R;
import com.github.almasud.NotePad.databinding.FragmentNoteBinding;
import com.github.almasud.NotePad.models.Note;
import com.github.almasud.NotePad.viewmodels.NoteVM;
import com.github.almasud.NotePad.views.adapters.NoteRVAdapter;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NoteScreen extends Fragment implements NoteRVAdapter.SetOnNoteClickListener {
    private static final String TAG = "NoteScreen";
    private FragmentNoteBinding mViewBinding;
    private NoteRVAdapter mNoteRVAdapter;
    private NoteVM mNoteVM;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView: is called");
        mViewBinding = FragmentNoteBinding.inflate(inflater, container, false);
        // Set recycler view, layout manager and adapter
        mNoteRVAdapter = new NoteRVAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = mViewBinding.rvNotes;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mNoteRVAdapter);

        // Set the listener of click event for add button
        mViewBinding.ivAdd.setOnClickListener(v -> {
            // Navigate to Note create screen
            NoteScreenDirections.ActionNavNoteToNavNoteForm action = NoteScreenDirections.actionNavNoteToNavNoteForm();
            Navigation.findNavController(v).navigate(action);
        });

        return mViewBinding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: is called");
        mNoteVM = new NoteVM(requireActivity().getApplication());

        // Show the progressbar layout and disable the UI
        mViewBinding.noteScreenProgressBar.progressBarText.setText(getString(R.string.notes_fetching_wait_message));
        mViewBinding.noteScreenProgressBar.getRoot().setVisibility(View.VISIBLE);
        if (isAdded())
            BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), false);
        mNoteVM.getNotes().observe(getViewLifecycleOwner(), notes -> {
            // Hide the progressbar layout and enable the UI
            mViewBinding.noteScreenProgressBar.getRoot().setVisibility(View.GONE);
            if (isAdded())
                BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), true);
            if (notes != null) {
                if (!notes.isEmpty()) {
                    mViewBinding.tvNoData.setVisibility(View.GONE);
                    mViewBinding.rvNotes.setVisibility(View.VISIBLE);
                    // Set the notes into Recyclerview
                    mNoteRVAdapter.setNotes(notes);

                    // Set the listener of click event for search button
                    mViewBinding.etSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (TextUtils.isEmpty(s)) {
                                mNoteRVAdapter.getFilter().filter("");
                            } else {
                                mNoteRVAdapter.getFilter().filter(s);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } else {
                    mViewBinding.rvNotes.setVisibility(View.GONE);
                    mViewBinding.tvNoData.setVisibility(View.VISIBLE);
                    mViewBinding.tvNoData.setText(R.string.no_note_yet);
                }
            } else {
                Snackbar snackbar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Couldn't fetch the notes!",
                        Snackbar.LENGTH_SHORT
                );
                snackbar.getView().setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.colorFailed)
                );
                snackbar.show();
            }
        });
    }

    @Override
    public void onNoteClick(Note note) {
        // Navigate to Note details screen
        NoteScreenDirections.ActionNavNoteToNavNoteDetails action = NoteScreenDirections.actionNavNoteToNavNoteDetails(note.getId());
        Navigation.findNavController(mViewBinding.getRoot()).navigate(action);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: is called");
        mViewBinding = null;
        super.onDestroyView();
    }
}