package com.tpt.borne.tpt.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.email.EmailFields;
import com.tpt.borne.tpt.models.Ship;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Borne on 6/16/2016.
 */
public class PositionsAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Ship> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<Ship, List<Ship>> _listDataChild;

    String preferedSelected;
    String preferedUnselected;

    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;



    public PositionsAdapter(Context context, List<Ship> listDataHeader,
                             HashMap<Ship, List<Ship>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        sharedpreferences = context.getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        preferedSelected = sharedpreferences.getString("preferedSelected", "");
        preferedUnselected = sharedpreferences.getString("preferedUnselected", "");

        Log.e("SHAAAAAA 1", preferedSelected.toString());
        Log.e("SHAAAAAAA 2", preferedUnselected.toString());

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {

        final Ship childText = (Ship) getChild(groupPosition, childPosition);
        Ship headTit = (Ship) getGroup(groupPosition);



        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.positions_list_item, null);

        }


        TextView sireText = (TextView) convertView.findViewById(R.id.sireText);
        TextView temaText = (TextView) convertView.findViewById(R.id.temaText);
        TextView onSubsLabel = (TextView) convertView.findViewById(R.id.where);



        if(headTit.getStatus().equals("on_subs")) {
            onSubsLabel.setText("ON SUBS");
        }else{
            onSubsLabel.setText("");
        }



            TextView sireText1 = (TextView) convertView.findViewById(R.id.sireText1);
        TextView temaText1 = (TextView) convertView.findViewById(R.id.temaText1);
        TextView commentsText1 = (TextView) convertView.findViewById(R.id.commentsText1);
        TextView dateText1 = (TextView) convertView.findViewById(R.id.dateText1);



// Checking if shared preferences prefered strings are not NULL
        if(preferedSelected != null && !(preferedSelected.equals(""))){

            try {
                JSONArray arr = new JSONArray(preferedUnselected);

                sireText.setText(arr.get(0).toString());
                temaText.setText(arr.get(1).toString());


                if(sireText.getText().toString().equals("cbm")){
                    sireText1.setText(_listDataHeader.get(groupPosition).getCbm());
                }else if(sireText.getText().toString().equals("dwt")){
                    sireText1.setText(_listDataHeader.get(groupPosition).getDwt());
                }else if(sireText.getText().toString().equals("loa")){
                    sireText1.setText(_listDataHeader.get(groupPosition).getLoa());
                }else if(sireText.getText().toString().equals("last")){
                    sireText1.setText(_listDataHeader.get(groupPosition).getLast());
                }else if(sireText.getText().toString().equals("sire")){
                    sireText1.setText(_listDataHeader.get(groupPosition).getSire());
                }else if(sireText.getText().toString().equals("tema")){
                    sireText1.setText(_listDataHeader.get(groupPosition).getTemaSuitable());
                }


                if(temaText.getText().toString().equals("cbm")){
                    temaText1.setText(_listDataHeader.get(groupPosition).getCbm());
                }else if(temaText.getText().toString().equals("dwt")){
                    temaText1.setText(_listDataHeader.get(groupPosition).getDwt());
                }else if(temaText.getText().toString().equals("loa")){
                    temaText1.setText(_listDataHeader.get(groupPosition).getLoa());
                }else if(temaText.getText().toString().equals("last")){
                    temaText1.setText(_listDataHeader.get(groupPosition).getLast());
                }else if(temaText.getText().toString().equals("sire")){
                    temaText1.setText(_listDataHeader.get(groupPosition).getSire());
                }else if(temaText.getText().toString().equals("tema")){
                    temaText1.setText(_listDataHeader.get(groupPosition).getTemaSuitable());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }else{
            sireText1.setText(_listDataHeader.get(groupPosition).getSire());
            temaText1.setText(_listDataHeader.get(groupPosition).getTemaSuitable());
        }


        commentsText1.setText(_listDataHeader.get(groupPosition).getComment());
        String lastUpdate = _listDataHeader.get(groupPosition).getLastUpdate();
        lastUpdate = lastUpdate.substring(8,10)+"/"+lastUpdate.substring(5,7)+"/"+lastUpdate.substring(0,4);
        dateText1.setText(lastUpdate);



        RelativeLayout relFirst = (RelativeLayout) convertView.findViewById(R.id.callBroker);
        relFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = "+44 20 37448041";
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+Uri.encode(number.trim())));

                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(callIntent);

            }
        });



        RelativeLayout shipDocumentation = (RelativeLayout) convertView.findViewById(R.id.emailBroker);
        shipDocumentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(_context, EmailFields.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("what", _listDataHeader.get(groupPosition).getOpenDate());
                i.putExtra("name", _listDataHeader.get(groupPosition).getName());
                i.putExtra("shipNameyear", _listDataHeader.get(groupPosition).getName()+" ("+_listDataHeader.get(groupPosition).getBuilt()+")");
                _context.startActivity(i);
            }
        });





        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Ship headerTitle = (Ship) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.positions_list_group, null);
        }



        TextView shipName = (TextView) convertView.findViewById(R.id.reporTitle);
        shipName.setText(headerTitle.getName()+" ("+headerTitle.getBuilt()+")");

        TextView whereWas = (TextView) convertView.findViewById(R.id.where);

        TextView monthName = (TextView) convertView.findViewById(R.id.monthName);
        monthName.setText(getMonth(headerTitle.getOpenDate()));
//        Log.e("OPEN DATE", getMonth(headerTitle.getOpenDate()));

        TextView openDay = (TextView) convertView.findViewById(R.id.openDay);
        openDay.setText(headerTitle.getOpenDate().substring(8,10));

        FrameLayout frame = (FrameLayout) convertView.findViewById(R.id.frame);


        if(headerTitle.getStatus().equals("on_subs")){
            whereWas.setText(headerTitle.getLocation());
            whereWas.setTextColor(_context.getResources().getColor(R.color.redMain));
            frame.setBackgroundResource(R.drawable.positions_calendar);
        }else{
            whereWas.setText(headerTitle.getLocation());
            whereWas.setTextColor(_context.getResources().getColor(R.color.textBlack));
            frame.setBackgroundResource(R.drawable.positions_calendar);
        }


        ImageView groupIndicator = (ImageView) convertView.findViewById(R.id.flag);
        if (isExpanded) {
            groupIndicator.setImageResource(R.drawable.post_chartering_arrow_up);
        } else {
            groupIndicator.setImageResource(R.drawable.post_chartering_arrow);
        }



// Checking if shared preferences prefered strings are not NULL


        TextView cbmText = (TextView) convertView.findViewById(R.id.cbmText);
        TextView dwtText = (TextView) convertView.findViewById(R.id.dwtText);
        TextView loatText = (TextView) convertView.findViewById(R.id.loaText);
        TextView lastText = (TextView) convertView.findViewById(R.id.lastText);

        TextView cbmText1 = (TextView) convertView.findViewById(R.id.cbmText1);
        TextView dwtText1 = (TextView) convertView.findViewById(R.id.dwtText1);
        TextView loatText1 = (TextView) convertView.findViewById(R.id.loaText1);
        TextView lastText1 = (TextView) convertView.findViewById(R.id.lastText1);

        if(preferedSelected != null && !(preferedSelected.equals(""))){

            try {
                JSONArray ar = new JSONArray(preferedSelected);

                    cbmText.setText(ar.get(0).toString());
                    dwtText.setText(ar.get(1).toString());
                    loatText.setText(ar.get(2).toString());
                    lastText.setText(ar.get(3).toString());

                if(cbmText.getText().toString().equals("cbm")){
                    cbmText1.setText(headerTitle.getCbm());
                }else if(cbmText.getText().toString().equals("dwt")){
                    cbmText1.setText(headerTitle.getDwt());
                }else if(cbmText.getText().toString().equals("loa")){
                    cbmText1.setText(headerTitle.getLoa());
                }else if(cbmText.getText().toString().equals("last")){
                    cbmText1.setText(headerTitle.getLast());
                }else if(cbmText.getText().toString().equals("sire")){
                    cbmText1.setText(headerTitle.getSire());
                }else if(cbmText.getText().toString().equals("tema")){
                    cbmText1.setText(headerTitle.getTemaSuitable());
                }

                if(dwtText.getText().toString().equals("cbm")){
                    dwtText1.setText(headerTitle.getCbm());
                }else if(dwtText.getText().toString().equals("dwt")){
                    dwtText1.setText(headerTitle.getDwt());
                }else if(dwtText.getText().toString().equals("loa")){
                    dwtText1.setText(headerTitle.getLoa());
                }else if(dwtText.getText().toString().equals("last")){
                    dwtText1.setText(headerTitle.getLast());
                }else if(dwtText.getText().toString().equals("sire")){
                    dwtText1.setText(headerTitle.getSire());
                }else if(dwtText.getText().toString().equals("tema")){
                    dwtText1.setText(headerTitle.getTemaSuitable());
                }


                if(loatText.getText().toString().equals("cbm")){
                    loatText1.setText(headerTitle.getCbm());
                }else if(loatText.getText().toString().equals("dwt")){
                    loatText1.setText(headerTitle.getDwt());
                }else if(loatText.getText().toString().equals("loa")){
                    loatText1.setText(headerTitle.getLoa());
                }else if(loatText.getText().toString().equals("last")){
                    loatText1.setText(headerTitle.getLast());
                }else if(loatText.getText().toString().equals("sire")){
                    loatText1.setText(headerTitle.getSire());
                }else if(loatText.getText().toString().equals("tema")){
                    loatText1.setText(headerTitle.getTemaSuitable());
                }

                if(lastText.getText().toString().equals("cbm")){
                    lastText1.setText(headerTitle.getCbm());
                }else if(lastText.getText().toString().equals("dwt")){
                    lastText1.setText(headerTitle.getDwt());
                }else if(lastText.getText().toString().equals("loa")){
                    lastText1.setText(headerTitle.getLoa());
                }else if(lastText.getText().toString().equals("last")){
                    lastText1.setText(headerTitle.getLast());
                }else if(lastText.getText().toString().equals("sire")){
                    lastText1.setText(headerTitle.getSire());
                }else if(lastText.getText().toString().equals("tema")){
                    lastText1.setText(headerTitle.getTemaSuitable());
                }



                } catch (JSONException e) {
                e.printStackTrace();
            }


        }else {
            cbmText1.setText(headerTitle.getCbm());
            dwtText1.setText(headerTitle.getDwt());
            loatText1.setText(headerTitle.getLoa());
            lastText1.setText(headerTitle.getLast());
        }



        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getMonth(String date){

        String monthGet = date.substring(5,7);
        int month = Integer.valueOf(monthGet);
        String finalMonth = null;


        switch (month){
            case 1:
                finalMonth = "January";
                break;
            case 2:
                finalMonth = "February";
                break;
            case 3:
                finalMonth = "March";
                break;
            case 4:
                finalMonth = "April";
                break;
            case 5:
                finalMonth = "May";
                break;
            case 6:
                finalMonth = "June";
                break;
            case 7:
                finalMonth = "July";
                break;
            case 8:
                finalMonth = "August";
                break;
            case 9:
                finalMonth = "September";
                break;
            case 10:
                finalMonth = "October";
                break;
            case 11:
                finalMonth = "November";
                break;
            case 12:
                finalMonth = "December";
                break;
        }


        return finalMonth;
    }


}
