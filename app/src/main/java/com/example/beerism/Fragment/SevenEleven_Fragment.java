package com.example.beerism.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.beerism.R;
import com.github.chrisbanes.photoview.PhotoView;


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
        PhotoView photoView = view.findViewById(R.id.seven_discount);
        photoView.setImageResource(R.drawable.dum_sale);
        return view;
    }
}