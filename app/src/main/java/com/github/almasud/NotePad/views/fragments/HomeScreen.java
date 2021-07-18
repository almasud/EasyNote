package com.github.almasud.NotePad.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.almasud.NotePad.databinding.FragmentHomeBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HomeScreen extends Fragment {

    private FragmentHomeBinding mViewBinding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        mViewBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return mViewBinding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewBinding = null;
    }

}