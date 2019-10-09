package com.example.beerism;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class SevenEleven_Fragment  extends Fragment {
    public static Fragment instantiate() {
        SevenEleven_Fragment fragment = new SevenEleven_Fragment();
        return fragment;
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seven_eleven_page, container, false);
        return view;
    }
}