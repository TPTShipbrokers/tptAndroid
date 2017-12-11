package com.tpt.borne.tpt.postchartering;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.email.EmailClaimDetails;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
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

/**
 * Created by Jovan Milutinović on 11.6.2016..
 */
public class OutstandingClaims extends AppCompatActivity {

    TextView shipNameText;
    TextView statusText;
    TextView dateText;
    TextView textSofComment;
    TextView titleText;


    ProgressDialog progDailog;
    String accessToken;

    RelativeLayout emailCharterParty;
    JSONArray arr;
    String userMail;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;


    String mainTextString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postchartering_uotstanding_claims);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }

        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);

        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        userMail = sharedpreferences.getString("email", "");


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueMain)));

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_hardware_keyboard_backspace);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        Intent i = getIntent();

        String status = i.getStringExtra("status");
        String sofComments = i.getStringExtra("sofComments");
        String date = i.getStringExtra("date");
        final String vesselName = i.getStringExtra("vesselName");
        String charteringId = i.getStringExtra("charteringId");




        shipNameText = (TextView) findViewById(R.id.shipName);
        shipNameText.setText(vesselName);
        statusText = (TextView) findViewById(R.id.status);
        statusText.setText(status);
        dateText = (TextView) findViewById(R.id.date);
        dateText.setText(date);
        textSofComment = (TextView) findViewById(R.id.textSofComment);
        titleText = (TextView) findViewById(R.id.textTitle);

        textSofComment.setText("\n No claims items reported");


        emailCharterParty = (RelativeLayout) findViewById(R.id.linearLayout3);
        emailCharterParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getBaseContext(), EmailClaimDetails.class);
                    i.putExtra("shipName", vesselName);
                    startActivity(i);
                }
            }
        });




        new HttpAsyncPostCharterParty().execute("http://borne.io/tpt/api/chartering/"+charteringId);

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
    private class HttpAsyncPostCharterParty extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(OutstandingClaims.this);
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

                JSONObject party = mainObject.getJSONObject("data");

                JSONArray charterPArty = party.getJSONArray("claims");

                String desc = "";

                if(!(charterPArty.length() == 0)){

                    for(int i =0; i<charterPArty.length(); i++){
                        JSONObject json = (JSONObject) charterPArty.get(i);
                        desc = desc+"\n"+json.getString("description");
                    }

                    textSofComment.setText(desc);
                }




                Log.e("Charter part", String.valueOf(charterPArty));



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
