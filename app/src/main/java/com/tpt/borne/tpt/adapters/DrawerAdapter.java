package com.tpt.borne.tpt.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.statics.Statics;


/**
 * Created by JM on 2/23/2016.
 */
public class DrawerAdapter extends BaseAdapter {


    String planete[];
    String artist[] = {"Start point","Chech fishing data","Edit sessions","Catches gallery",
            "Bait management", "Fishing stats", "Users interaction"};
    Context context;
    int slike[] = {R.drawable.drawer_positions,R.drawable.drawer_chartering, R.drawable.drawer_postchartering,
            R.drawable.drawer_market_report, R.drawable.drawer_settings, R.drawable.drawer_contact};

    public DrawerAdapter(Context context) {
        planete = context.getResources().getStringArray(R.array.services);
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return planete.length;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return planete[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = null;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_drawer, parent, false);
        }else{
            row = convertView;
        }

        TextView t = (TextView) row.findViewById(R.id.title);
        ImageView i = (ImageView) row.findViewById(R.id.list_image);

        t.setText(planete[position]);
        t.setTextColor(Color.parseColor("#a4a4a4"));
        i.setImageResource(slike[position]);

        if(Statics.drawerIndicator ==0 && position == 0) {
            t.setTextColor(Color.parseColor("#02539c"));
            slike[0] = R.drawable.drawer_positions_blue;
        }else if(Statics.drawerIndicator ==1 && position == 1){
            t.setTextColor(Color.parseColor("#02539c"));
            slike[1] = R.drawable.drawer_chartering_blue;
        }else if(Statics.drawerIndicator ==2 && position == 2){
            t.setTextColor(Color.parseColor("#02539c"));
            slike[2] = R.drawable.drawer_postchartering_blue;
        }else if(Statics.drawerIndicator ==3 && position == 3){
            t.setTextColor(Color.parseColor("#02539c"));
            slike[3] = R.drawable.drawer_market_report_blue;
        }else if(Statics.drawerIndicator ==4 && position == 4){
            t.setTextColor(Color.parseColor("#02539c"));
            slike[4] = R.drawable.drawer_settings_blue;
        }else if(Statics.drawerIndicator ==5 && position == 5){
            t.setTextColor(Color.parseColor("#02539c"));
            slike[5] = R.drawable.drawer_contact_blue;
        }




        return row;
    }

}
