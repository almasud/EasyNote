package com.github.almasud.NotePad.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.almasud.NotePad.BaseApplication;
import com.github.almasud.NotePad.R;
import com.github.almasud.NotePad.databinding.FragmentHomeBinding;
import com.github.almasud.NotePad.viewmodels.NoteVM;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class HomeScreen extends Fragment {
    private static final String TAG = "HomeScreen";
    private FragmentHomeBinding mViewBinding;
    private NoteVM mNoteVM;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mViewBinding = FragmentHomeBinding.inflate(inflater, container, false);
        // Initialize the VM
        mNoteVM = new NoteVM(requireActivity().getApplication());

        return mViewBinding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show the progressbar layout and disable the UI
        mViewBinding.homeScreenProgressBar.progressBarText.setText(getString(R.string.notes_fetching_wait_message));
        mViewBinding.homeScreenProgressBar.getRoot().setVisibility(View.VISIBLE);
        if (isAdded())
            BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), false);
        mNoteVM.getNotesSize().observe(getViewLifecycleOwner(), notesSize -> {
            // Hide the progressbar layout and enable the UI
            mViewBinding.homeScreenProgressBar.getRoot().setVisibility(View.GONE);
            if (isAdded())
                BaseApplication.enableDisableViewGroup(mViewBinding.getRoot(), true);
            if (notesSize != null && notesSize > 0) {
                Log.d(TAG, "onViewCreated: notesSize: " + notesSize);
                mViewBinding.tvNotesCount.setText(notesSize.toString());
            } else {
                Snackbar snackbar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Couldn't fetch the notes count!",
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
    public void onDestroyView() {
        mViewBinding = null;
        super.onDestroyView();
    }

}