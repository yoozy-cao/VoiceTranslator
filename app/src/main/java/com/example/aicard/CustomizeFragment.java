package com.example.aicard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CustomizeFragment extends Fragment {
    public final String TAG = this.getClass().getSimpleName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Log.v(TAG, "onCreateView...");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView...");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate...");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart...");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume...");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause...");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy...");
    }

}
