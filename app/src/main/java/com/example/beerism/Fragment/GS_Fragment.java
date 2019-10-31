package com.example.beerism.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.beerism.R;
import com.github.chrisbanes.photoview.PhotoView;


public class GS_Fragment  extends Fragment {
    public static Fragment instantiate() {
        return new GS_Fragment();
    }

    //    // Store instance variables
//    private String title;
//    private int page;
//
//    // newInstance constructor for creating fragment with arguments
//    public static GS_Fragment newInstance(int page, String title) {
//        GS_Fragment fragment = new GS_Fragment();
//        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        args.putString("someTitle", title);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    // Store instance variables based on arguments passed
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        page = getArguments().getInt("someInt", 0);
//        title = getArguments().getString("someTitle");
//    }
//
//    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gs_page, container, false);
        PhotoView photoView = view.findViewById(R.id.gs_discount);
        photoView.setImageResource(R.drawable.gs25);
        return view;
    }
}