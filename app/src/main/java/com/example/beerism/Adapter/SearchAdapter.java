package com.example.beerism.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerism.R;
import com.example.beerism.VO.BeerVO;
import com.google.android.gms.vision.L;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final ArrayList<BeerVO> values;
    private Context context;

    public SearchAdapter(ArrayList<BeerVO> values, Context context) {
        this.values = values;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beerlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = values.get(position);
        holder.titleview.setText(values.get(position).getName_ko());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    // 검색 리스트 보여주기 필터
    public void setFilter(ArrayList<BeerVO> items) {
        values.clear();
        values.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView titleview;

        public BeerVO mItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            titleview = itemView.findViewById(R.id.beer_ko);
        }
    }
}
