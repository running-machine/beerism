package com.example.beerism;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beerism.VO.ChoiceBeerVO;

import java.util.List;

class choiceBeer extends ArrayAdapter<ChoiceBeerVO> {
    public choiceBeer(Context context, List<ChoiceBeerVO> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_choice, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        ChoiceBeerVO choiceBeerVO = getItem(position);
        vh.des.setText(choiceBeerVO.description);
        vh.amount.setText(choiceBeerVO.amount);
        return convertView;

    }


    private static final class ViewHolder {
        TextView des, amount;

        public ViewHolder(View v) {
            des = v.findViewById(R.id.description);
            amount = v.findViewById(R.id.amount);
        }
    }
}
