package com.tpt.borne.tpt.settings;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.positions.PositionsMr;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Jovan Milutinović on 5.6.2016..
 */
public class SettingsPreferedPositions extends AppCompatActivity {



    ImageView checkBoxCbm;
    ImageView checkBoxDwt;
    ImageView checkBoxLoa;
    ImageView checkBoxLast;
    ImageView checkBoxSire;
    ImageView checkBoxtema;


    RelativeLayout cbmRel;
    RelativeLayout dwtRel;
    RelativeLayout loaRel;
    RelativeLayout lastRel;
    RelativeLayout sireRel;
    RelativeLayout temaRel;

    ArrayList<RelativeLayout> relativeViews;
    ArrayList<ImageView> imageViews;

    RelativeLayout saveButt;
    SharedPreferences.Editor editor;

    ArrayList<String> selected;
    ArrayList<String> unselected;

    String preferedSelected;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_prefered_view);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }


        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueMain)));


        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_hardware_keyboard_backspace);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        preferedSelected = sharedpreferences.getString("preferedSelected", "");



        selected = new ArrayList<>();
        unselected = new ArrayList<>();

// Image views
        checkBoxCbm = (ImageView) findViewById(R.id.checkBoxCbm);
        checkBoxDwt = (ImageView) findViewById(R.id.checkBoxDwt);
        checkBoxLoa = (ImageView) findViewById(R.id.checkBoxLoa);
        checkBoxLast = (ImageView) findViewById(R.id.checkBoxLast);
        checkBoxSire = (ImageView) findViewById(R.id.checkBoxSire);
        checkBoxtema = (ImageView) findViewById(R.id.checkBoxTema);


//Relative layoyts
        cbmRel = (RelativeLayout) findViewById(R.id.cmbRel);
        dwtRel = (RelativeLayout) findViewById(R.id.dwtRel);
        loaRel = (RelativeLayout) findViewById(R.id.loaRel);
        lastRel = (RelativeLayout) findViewById(R.id.lastRel);
        sireRel = (RelativeLayout) findViewById(R.id.sireRel);
        temaRel = (RelativeLayout) findViewById(R.id.temaRel);


        relativeViews = new ArrayList<>();
        relativeViews.add(cbmRel);
        relativeViews.add(dwtRel);
        relativeViews.add(loaRel);
        relativeViews.add(lastRel);
        relativeViews.add(sireRel);
        relativeViews.add(temaRel);

        cbmRel.setTag("cbm");
        dwtRel.setTag("dwt");
        loaRel.setTag("loa");
        lastRel.setTag("last");
        sireRel.setTag("sire");
        temaRel.setTag("tema");


        imageViews = new ArrayList<>();
        imageViews.add(checkBoxCbm);
        imageViews.add(checkBoxDwt);
        imageViews.add(checkBoxLoa);
        imageViews.add(checkBoxLast);
        imageViews.add(checkBoxSire);
        imageViews.add(checkBoxtema);

        checkBoxCbm.setTag("false");
        checkBoxDwt.setTag("false");
        checkBoxLoa.setTag("false");
        checkBoxLast.setTag("false");
        checkBoxSire.setTag("false");
        checkBoxtema.setTag("false");


// Loading saved prefer positions
        if(preferedSelected != null && !(preferedSelected.equals(""))){
                try {
                    JSONArray ar = new JSONArray(preferedSelected);
                    for(int j =0; j<ar.length(); j++){

                        String arSelected = (String) ar.get(j);

                        for(int i =0; i<imageViews.size(); i++) {

                            if(relativeViews.get(i).getTag().equals(arSelected)){
                                imageViews.get(i).setImageResource(R.drawable.check_box_checked);
                                imageViews.get(i).setTag("true");
                                selected.add((String) relativeViews.get(i).getTag());

                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }





        for(int  j = 0; j<relativeViews.size(); j++){

            final int finalJ = j;
            relativeViews.get(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(selected.size() >= 4){
                        if(imageViews.get(finalJ).getTag().equals("true")) {
                            imageViews.get(finalJ).setImageResource(R.drawable.check_box_empty);
                            imageViews.get(finalJ).setTag("false");
                            selected.remove((String) relativeViews.get(finalJ).getTag());

                            Log.e("selected List", selected.toString());

                        }else {
                            Toast.makeText(getBaseContext(), "Please choose exactly 4 fields.", Toast.LENGTH_SHORT).show();
                        }
                    }else {

                        if(imageViews.get(finalJ).getTag().equals("true")){
                            imageViews.get(finalJ).setImageResource(R.drawable.check_box_empty);
                            imageViews.get(finalJ).setTag("false");
                            selected.remove((String) relativeViews.get(finalJ).getTag());

                            Log.e("selected List", selected.toString());


                        }else {

                            imageViews.get(finalJ).setImageResource(R.drawable.check_box_checked);
                            imageViews.get(finalJ).setTag("true");
                            selected.add((String) relativeViews.get(finalJ).getTag());

                            Log.e("selected List", selected.toString());
//                            Log.e("SUM", String.valueOf(selected.size()));
//                            Log.e("TAG", String.valueOf(imageViews.get(finalJ).getTag()));
                        }
                    }

                }
            });



        }




        saveButt = (RelativeLayout) findViewById(R.id.linearLayout3);
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected.size() == 4){

                    JSONArray jsonArr = new JSONArray(selected);

                    for(int i = 0; i<relativeViews.size(); i++){
                        if(!(selected.contains(relativeViews.get(i).getTag()))){
                            unselected.add((String) relativeViews.get(i).getTag());

                        }
                    }

                    JSONArray arrUnselected = new JSONArray(unselected);

                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.putString("preferedSelected", String.valueOf(jsonArr));
                    editor.putString("preferedUnselected", String.valueOf(arrUnselected));

                    editor.commit();


                    Intent intent = new Intent(SettingsPreferedPositions.this, PositionsMr.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
                    getBaseContext().startActivity(intent);

                }else{
                    Toast.makeText(getBaseContext(), "Please select equals 4 fields.", Toast.LENGTH_SHORT).show();

                }
            }
        });



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Checking internet connection
        Statics s = new Statics(getBaseContext());
        s.loginUser();

        registerBroadcastReceiver();

    }

    public void registerBroadcastReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        receiver = new NetworkChangeReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean check = Statics.isNetworkAvailable(context);

                if(check == true){
                    placeholder.removeView(placeText);
                }else{
                    placeholder.removeView(placeText);
                    placeholder.addView(placeText);
                }
            }
        };
        registerReceiver(receiver, filter);

    }

    public void unregisterBroadcastReceiver() {
        this.unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterBroadcastReceiver();
    }





}
