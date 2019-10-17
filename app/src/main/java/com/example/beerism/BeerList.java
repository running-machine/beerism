package com.example.beerism;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerism.Adapter.beerAdapter;
import com.example.beerism.VO.beerVO;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BeerList extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<beerVO> beerVOArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_list);

        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.beerRec);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        beerVOArrayList = new ArrayList<>();

        //파이어베이스 데이터 가져오기
        firebaseFirestore.collection("beer").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("파이어베이스 DB", "로드 실패", e);
                    return;
                }
                int count = queryDocumentSnapshots.size();
                beerVOArrayList.clear();//일딴 초기화 해줘야 한다. 안 그럼 기존 데이터에 반복해서 뒤에 추가된다.

                for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) {
                    if (documentSnapshots.get("name_en") != null){
                        beerVOArrayList.add(0,new beerVO(documentSnapshots.getId(),
                                documentSnapshots.getString("ad"),
                                documentSnapshots.getString("alc"),
                                documentSnapshots.getString("category"),
                                documentSnapshots.getString("country"),
                                documentSnapshots.getString("homepage"),
                                documentSnapshots.getString("img"),
                                documentSnapshots.getString("name_en"),
                                documentSnapshots.getString("name_ko")));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new beerAdapter(beerVOArrayList,this);
        recyclerView.setAdapter(adapter);
    }
}
