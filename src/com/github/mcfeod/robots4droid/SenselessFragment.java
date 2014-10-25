package com.github.mcfeod.robots4droid;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SenselessFragment extends Fragment {
    @Override
    // метод public, чтобы вызываться activity
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceBundle){
        View v = inflater.inflate(R.layout.fragment_senseless, parent, false);
        return v;
    }
}
