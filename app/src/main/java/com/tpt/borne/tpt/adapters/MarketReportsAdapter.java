package com.tpt.borne.tpt.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.models.MarketReport;

import java.util.List;

/**
 * Created by Borne on 6/10/2016.
 */
public class MarketReportsAdapter extends ArrayAdapter<MarketReport> {

    Context context;


    public MarketReportsAdapter(Context context, int resourceId,
                              List<MarketReport> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView reportTitle;
        TextView reportDate;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        MarketReport rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_market_report, parent, false);
            holder = new ViewHolder();

            holder.reportTitle = (TextView) convertView.findViewById(R.id.reporTitle);
            holder.reportDate = (TextView) convertView.findViewById(R.id.reportDate);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.reportTitle.setText(rowItem.getTitle());
        holder.reportDate.setText(rowItem.getDate());


        return convertView;
    }

}
