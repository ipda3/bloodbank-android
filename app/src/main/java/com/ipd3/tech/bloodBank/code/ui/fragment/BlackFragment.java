package com.ipd3.tech.bloodBank.code.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipd3.tech.bloodBank.code.R;

public class BlackFragment extends Fragment {

    public BlackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_forget_password, container, false );

        return view;
    }
}




