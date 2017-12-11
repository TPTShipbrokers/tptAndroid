package com.tpt.borne.tpt.positions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.models.HelperClass;
import com.tpt.borne.tpt.statics.Statics;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Borne on 10/11/2016.
 */
public class Locations extends AppCompatActivity {

    FrameLayout placeholder;
    TextView placeText;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String accessToken;

    ProgressDialog progDailog;

    ArrayList<LinearLayout> layoutList;
    ArrayList<ImageView> imageList;
    String locationValue;
    boolean locationClicked = false;
    RelativeLayout saveButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.positions_location);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueMain)));

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_hardware_keyboard_backspace);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        final SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        locationValue = sharedpreferences.getString("locationValue", "");
        Log.e("Locatin value", locationValue);



        Statics.drawerIndicator = 0;

        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);


        saveButt = (RelativeLayout) findViewById(R.id.linearLayout3);
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("locationValue", locationValue);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        });

        new HttpAsyncLocations().execute("http://borne.io/tpt/api/vessels/locations");


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


    // POST CHARTERI|NG
    private class HttpAsyncLocations extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(Locations.this);
            progDailog.setMessage("Loading data...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Uzimamao Json podatke o Useru
            try {

                JSONObject mainObject = new JSONObject(result);

                if(mainObject.has("data")) {

                    JSONArray locations = mainObject.getJSONArray("data");


                    if(locations.length()>0){

                        layoutList = new ArrayList<>();
                        imageList = new ArrayList<>();

                        for(int i = 0; i<locations.length(); i++){

                            JSONObject obj = (JSONObject) locations.get(i);
                            String locationName = (String) obj.get("location");

                            LinearLayout mainLinearLay = (LinearLayout) findViewById(R.id.mainLinearLay);

                            // ADDING NEW LAYOUT
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            LinearLayout newLayout = new LinearLayout(getBaseContext());
                            params.setMargins(0,0,0,HelperClass.convertDpToPixel(5, getBaseContext()));
                            newLayout.setOrientation(LinearLayout.HORIZONTAL);
                            newLayout.setPadding(0,HelperClass.convertDpToPixel(3, getBaseContext()), 0, HelperClass.convertDpToPixel(3, getBaseContext()));
                            newLayout.setLayoutParams(params);
                            newLayout.setTag(locationName);

                            // SETTING TEXT
                            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(HelperClass.convertDpToPixel(18, getBaseContext()), HelperClass.convertDpToPixel(18, getBaseContext()));
                            ImageView img = new ImageView(getBaseContext());
                            imageParams.setMargins(HelperClass.convertDpToPixel(30, getBaseContext()), 0, 0, 0);
                            img.setBackgroundResource(R.drawable.check_box_empty);
                            img.setLayoutParams(imageParams);

                            newLayout.addView(img);

                            // SETTING IMAGE
                            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            TextView t = new TextView(getBaseContext());
                            textParams.setMargins(HelperClass.convertDpToPixel(14, getBaseContext()),0,0,0);
                            t.setLayoutParams(textParams);
                            t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            t.setTextColor(getResources().getColor(R.color.darkGrey));
                            t.setText(locationName);

                            newLayout.addView(t);


                            // ADDING ALL TO MAIN LAYOUT
                            mainLinearLay.addView(newLayout);


                            // ADDING LAYOUT TO LIST
                            layoutList.add(newLayout);
                            imageList.add(img);


                            if(locationValue.equals(layoutList.get(i).getTag())){
                                imageList.get(i).setImageResource(R.drawable.check_box_checked);
                                locationClicked = true;
                            }


// Checking if it is selected
                            for(int  j = 0; j<layoutList.size(); j++){

                                final int finalJ = j;
                                layoutList.get(j).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(locationClicked == true){
                                            for(int k =0; k<imageList.size(); k++){
                                                imageList.get(k).setImageResource(R.drawable.check_box_empty);
                                            }

                                            imageList.get(finalJ).setImageResource(R.drawable.check_box_checked);
                                            locationClicked = true;
                                            locationValue = String.valueOf(layoutList.get(finalJ).getTag());

                                        }else{
                                            imageList.get(finalJ).setImageResource(R.drawable.check_box_checked);
                                            locationClicked = true;
                                            locationValue = String.valueOf(layoutList.get(finalJ).getTag());
                                        }
                                    }
                                });

                            }











                        }
                    }


                }else{

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            progDailog.dismiss();

        }
    }


    // Method to parse JSON sent request by token
    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-type", "application/json");
            httpGet.setHeader("Authorization", "Bearer "+accessToken);


            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGet);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }



    // Covert results to string
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


}
