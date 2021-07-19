package com.github.almasud.EasyNote.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.almasud.EasyNote.BaseApplication;
import com.github.almasud.EasyNote.R;
import com.github.almasud.EasyNote.databinding.FragmentHomeBinding;
import com.github.almasud.EasyNote.viewmodels.NoteVM;

import androidx.annotation.NonNull;
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
                mViewBinding.tvNotesCount.setText("0");
            }
        });
    }

    @Override
    public void onDestroyView() {
        mViewBinding = null;
        super.onDestroyView();
    }

}