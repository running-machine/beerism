package com.example.beerism.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.beerism.R;
import com.github.chrisbanes.photoview.PhotoView;

public class Cu_Fragment extends Fragment {

    public static Fragment instantiate() {
        return new Cu_Fragment();
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cu_page, container, false);
        PhotoView photoView = view.findViewById(R.id.cu_discount);
        photoView.setImageResource(R.drawable.dis_cu);
        return view;
    }
}
