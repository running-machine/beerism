package com.example.beerism;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.beerism.Adapter.BeerAdapter;
import com.example.beerism.Adapter.SearchAdapter;
import com.example.beerism.VO.BeerVO;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class search extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BeerVO> beerVOS;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void intitView(){
        getIntentDate();
        recyclerView = findViewById(R.id.notice_search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
<<<<<<< HEAD
=======
//        adapter = new BeerAdapter(this,)
>>>>>>> 34d4142510766733d78daa13e1378f954fea2e3a
    }

    private void getIntentDate() {
        Intent intent =getIntent();
<<<<<<< HEAD

=======
//        context = intent.getExtras().getSerializable(context.NO)
>>>>>>> 34d4142510766733d78daa13e1378f954fea2e3a

    }
}
