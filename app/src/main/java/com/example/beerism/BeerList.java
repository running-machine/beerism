package com.example.beerism;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerism.Adapter.BeerAdapter;
import com.example.beerism.VO.BeerVO;
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
    private ArrayList<BeerVO> beerVOAllArrayList;
    private ArrayList<BeerVO> beerVOArrayList;

    private EditText searchTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_list);

        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.beerRec);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        searchTxt = findViewById(R.id.search_txt);
        beerVOAllArrayList = new ArrayList<>();
        beerVOArrayList = new ArrayList<>();

        initBeerList();

        adapter = new BeerAdapter(beerVOArrayList,this);
        recyclerView.setAdapter(adapter);

        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateBeerList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        updateBeerList("");
    }

    private void initBeerList() {
        //파이어베이스 데이터 가져오기
        firebaseFirestore.collection("beer").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("파이어베이스 DB", "로드 실패", e);
                    return;
                }

                for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) {
                    if (documentSnapshots.get("name_en") != null) {
                        beerVOAllArrayList.add(0, new BeerVO(
                                documentSnapshots.getString("img"),
                                documentSnapshots.getString("name_ko"),
                                documentSnapshots.getString("name_en"),
                                documentSnapshots.getString("alc"),
                                documentSnapshots.getString("category"),
                                documentSnapshots.getString("country"),
                                documentSnapshots.getString("homepage"),
                                documentSnapshots.getString("ad")
                        ));
                    }
                }

                beerVOArrayList.addAll(beerVOAllArrayList);

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateBeerList(String search) {
        final String searchStr = search.toLowerCase();

        beerVOArrayList.clear();
        for (BeerVO beer : beerVOAllArrayList) {
            String korean = beer.getName_ko().toLowerCase();
            String english = beer.getName_en().toLowerCase();
            String category = beer.getCategory().toLowerCase();
            String country = beer.getCountry().toLowerCase();
            if (korean.contains(searchStr) || english.contains(searchStr) || category.contains(searchStr) || country.contains(searchStr)) {
                beerVOArrayList.add(beer);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
