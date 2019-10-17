package com.example.beerism.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beerism.R;
import com.example.beerism.VO.beerVO;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class beerAdapter extends RecyclerView.Adapter<beerAdapter.BeerHolder> {
    FirebaseFirestore firebaseFirestore;
    private ArrayList<beerVO> beerVOArrayList;
    private Context context;

    public beerAdapter(ArrayList<beerVO> beerVOArrayList, Context context) {
        this.beerVOArrayList = beerVOArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BeerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beerlist_item, parent, false);

        return new BeerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeerHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(beerVOArrayList.get(position).getBeer_image())
                .into(holder.beerImage);
        holder.beerKo.setText(beerVOArrayList.get(position).getName_ko());
        holder.beerEn.setText(beerVOArrayList.get(position).getName_en());
        holder.beerAd.setText(beerVOArrayList.get(position).getAd());
        holder.beerHomepage.setText(beerVOArrayList.get(position).getHomepage());
        holder.beerCountry.setText(beerVOArrayList.get(position).getCountry());
        holder.beerCategory.setText(beerVOArrayList.get(position).getCategory());
        holder.beerAlc.setText(beerVOArrayList.get(position).getAlc());

    }

    @Override
    public int getItemCount() {
        return (beerVOArrayList != null ? beerVOArrayList.size() : 0);
    }

    public class BeerHolder extends RecyclerView.ViewHolder {
        ImageView beerImage;
        TextView beerAlc, beerCategory, beerCountry, beerHomepage, beerAd, beerKo, beerEn;


        public BeerHolder(@NonNull View itemView) {
            super(itemView);

            this.beerImage = itemView.findViewById(R.id.beer_img);
            this.beerAlc = itemView.findViewById(R.id.beer_alc);
            this.beerCategory = itemView.findViewById(R.id.beer_category);
            this.beerCountry = itemView.findViewById(R.id.beer_country);
            this.beerHomepage = itemView.findViewById(R.id.beer_homepage);
            this.beerAd = itemView.findViewById(R.id.beer_ad);
            this.beerKo = itemView.findViewById(R.id.beer_ko);
            this.beerEn = itemView.findViewById(R.id.beer_en);
        }
    }
}
