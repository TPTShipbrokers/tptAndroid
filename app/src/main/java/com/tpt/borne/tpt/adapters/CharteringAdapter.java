package com.tpt.borne.tpt.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.chartering.CharterParty;
import com.tpt.borne.tpt.chartering.CharteringInvoices;
import com.tpt.borne.tpt.chartering.CharteringStatementOfFacts;
import com.tpt.borne.tpt.chartering.ShipDocumentation;
import com.tpt.borne.tpt.models.PostCharteringModel;
import com.tpt.borne.tpt.statics.Statics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Borne on 6/13/2016.
 */
public class CharteringAdapter extends BaseExpandableListAdapter {


    private Context _context;
    private List<PostCharteringModel> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<PostCharteringModel, List<PostCharteringModel>> _listDataChild;

    public CharteringAdapter(Context context, List<PostCharteringModel> listDataHeader,
                                 HashMap<PostCharteringModel, List<PostCharteringModel>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
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

        final PostCharteringModel childText = (PostCharteringModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.chartering_list_item, null);
        }


        RelativeLayout relFirst = (RelativeLayout) convertView.findViewById(R.id.charteringParty);
        relFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(_context);
                if (conn.isConnected(_context) == false) {
                    Toast.makeText(_context, R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(_context, CharterParty.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("status", _listDataHeader.get(groupPosition).getStatus());
                    i.putExtra("sofComments", _listDataHeader.get(groupPosition).getSofComments());
                    i.putExtra("date", _listDataHeader.get(groupPosition).getDate());
                    i.putExtra("vesselName", _listDataHeader.get(groupPosition).getVesselName());
                    i.putExtra("charteringId", _listDataHeader.get(groupPosition).getCgarteringId());
                    _context.startActivity(i);
                }
            }
        });



        RelativeLayout shipDocumentation = (RelativeLayout) convertView.findViewById(R.id.shipDocumentation);
        shipDocumentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(_context);
                if (conn.isConnected(_context) == false) {
                    Toast.makeText(_context, R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(_context, ShipDocumentation.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("status", _listDataHeader.get(groupPosition).getStatus());
                    i.putExtra("sofComments", _listDataHeader.get(groupPosition).getSofComments());
                    i.putExtra("date", _listDataHeader.get(groupPosition).getDate());
                    i.putExtra("vesselName", _listDataHeader.get(groupPosition).getVesselName());
                    i.putExtra("dateFromStatus", _listDataHeader.get(groupPosition).getDateFromStatus());
                    i.putExtra("charteringId", _listDataHeader.get(groupPosition).getCgarteringId());
                    _context.startActivity(i);
                }
            }
        });

        RelativeLayout relThird = (RelativeLayout) convertView.findViewById(R.id.invoice);
        relThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(_context);
                if (conn.isConnected(_context) == false) {
                    Toast.makeText(_context, R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(_context, CharteringInvoices.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("status", _listDataHeader.get(groupPosition).getStatus());
                    i.putExtra("sofComments", _listDataHeader.get(groupPosition).getSofComments());
                    i.putExtra("date", _listDataHeader.get(groupPosition).getDate());
                    i.putExtra("vesselName", _listDataHeader.get(groupPosition).getVesselName());
                    i.putExtra("charteringId", _listDataHeader.get(groupPosition).getCgarteringId());
                    _context.startActivity(i);
                }

            }
        });


        RelativeLayout relFourth = (RelativeLayout) convertView.findViewById(R.id.statementOfFact);
        relFourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(_context);
                if (conn.isConnected(_context) == false) {
                    Toast.makeText(_context, R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(_context, CharteringStatementOfFacts.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("status", _listDataHeader.get(groupPosition).getStatus());
                    i.putExtra("sofComments", _listDataHeader.get(groupPosition).getSofComments());
                    i.putExtra("date", _listDataHeader.get(groupPosition).getDate());
                    i.putExtra("vesselName", _listDataHeader.get(groupPosition).getVesselName());
                    i.putExtra("dateFromStatus", _listDataHeader.get(groupPosition).getDateFromStatus());
                    _context.startActivity(i);
                }
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
        PostCharteringModel headerTitle = (PostCharteringModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.chartering_list_group, null);


        }

            TextView name = (TextView) convertView
                    .findViewById(R.id.reporTitle);
            name.setText(headerTitle.getVesselName());

            AutofitTextView status = (AutofitTextView) convertView.findViewById(R.id.reportDate);
            status.setText(headerTitle.getStatus());

        AutofitTextView date = (AutofitTextView) convertView.findViewById(R.id.date);

            ImageView imageSmallIcon = (ImageView) convertView.findViewById(R.id.watch);
            ImageView imageState = (ImageView) convertView.findViewById(R.id.imageLeft);

            if (_listDataHeader.get(groupPosition).getState().equals("live")) {



            if(_listDataHeader.get(groupPosition).getFullDate() != null || !(_listDataHeader.get(groupPosition).getFullDate().equals("null"))){

                String dateFrom = _listDataHeader.get(groupPosition).getFullDate();

                String dateModify = dateFrom.substring(0,10);
                date.setText("Clean Fixed - CPD "+dateModify);
            }else{
                date.setText("Clean Fixed - CPD");
            }

            imageState.setImageResource(R.drawable.chartering_blue_ship);
            imageSmallIcon.setImageResource(R.drawable.post_chartering_grey_clock);

            } else if (_listDataHeader.get(groupPosition).getState().equals("subs_due")) {


                SimpleDateFormat dateFormatdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateGet = null;
                Date dateToday = new Date();
                try {
                    dateGet = dateFormatdate.parse(headerTitle.getSubsDueDate());


                } catch (ParseException e) {
                    e.printStackTrace();
                }


                long diff = dateGet.getTime() - dateToday.getTime();

                int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                diff = diff - diffDays * (24 * 60 * 60 * 1000);

                int diffhours = (int) (diff / (60 * 60 * 1000));
                diff = diff - diffhours * (60 * 60 * 1000);

                int diffmin = (int) (diff / (60 * 1000));
                diff = diff - diffmin * (60 * 1000);

                int diffsec = (int) (diff / (1000));

// IF DATE IS GOING TO MINUS IF IT IS <0
                if(diffDays < 0 || diffhours <0 || diffsec <0){
                    date.setText("SUBS DUE");
                    date.setTextColor(_context.getResources().getColor(R.color.redMain));
                }else{
                    date.setText(diffDays + " DAYS " + diffhours + " HRS " + diffmin + " MINS");
                    date.setTextColor(_context.getResources().getColor(R.color.textBlack));
                }


                imageState.setImageResource(R.drawable.chartering_blue_clock);
                imageSmallIcon.setImageResource(R.drawable.chartering_red_clock);


            } else {
                date.setText("SUBS FAILED");
                date.setTextColor(_context.getResources().getColor(R.color.redMain));
                imageState.setImageResource(R.drawable.chartering_blue_clock);
                imageSmallIcon.setImageResource(R.drawable.chartering_red_clock);
            }

            ImageView groupIndicator = (ImageView) convertView.findViewById(R.id.flag);
            if (isExpanded) {
                groupIndicator.setImageResource(R.drawable.post_chartering_arrow_up);
            } else {
                groupIndicator.setImageResource(R.drawable.post_chartering_arrow);
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds () {
            return false;
        }

        @Override
        public boolean isChildSelectable ( int groupPosition, int childPosition){
            return true;
        }


}
