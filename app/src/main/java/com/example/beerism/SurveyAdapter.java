package com.example.beerism;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.beerism.VO.SurveyOption;

import java.util.List;

class SurveyAdapter extends ArrayAdapter<SurveyOption> {
    public SurveyAdapter(Context context, List<SurveyOption> surveyOption) {
        super(context, 0, surveyOption);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.survey_option, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        SurveyOption option = getItem(position);

        vh.beer.setText(option.beer);

        return convertView;
    }

    private static final class ViewHolder {
        TextView beer;

        public ViewHolder(View v) {
            beer = (TextView) v.findViewById(R.id.beer);
        }
    }
}
