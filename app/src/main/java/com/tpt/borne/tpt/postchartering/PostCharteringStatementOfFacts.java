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
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
public class PostCharteringStatementOfFacts extends AppCompatActivity {


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

    String mainTextString;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postchartering_statement_facts);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }

        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        userMail = sharedpreferences.getString("email", "");

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


        emailCharterParty = (RelativeLayout) findViewById(R.id.linearLayout3);
        emailCharterParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    accessToken = sharedpreferences.getString("accessToken", "");
// Initializing array because of API
                    arr = new JSONArray();
                    new HttpAsyncTaskSendMAil().execute("http://borne.io/tpt/api/default/send_email");
                }
            }
        });


        Intent i = getIntent();

        String status = i.getStringExtra("status");
        String sofComments = i.getStringExtra("sofComments");
        String date = i.getStringExtra("date");
        String vesselName = i.getStringExtra("vesselName");
        String dateFromStatus = i.getStringExtra("dateFromStatus");



        shipNameText = (TextView) findViewById(R.id.shipName);
        shipNameText.setText(vesselName);
        statusText = (TextView) findViewById(R.id.status);
        statusText.setText(status);
        dateText = (TextView) findViewById(R.id.date);
        dateText.setText(date);
        textSofComment = (TextView) findViewById(R.id.textSofComment);
        textSofComment.setText(sofComments);
        titleText = (TextView) findViewById(R.id.textTitle);

        if(status!= null && dateFromStatus!= null){
            String dateFixed = dateFromStatus.substring(11,16);
            dateFixed = dateFixed.replace(":", "");
            titleText.setText(dateFixed+" "+"hrs"+" "+status.toUpperCase());
            mainTextString = titleText.getText().toString()+"<br/>"+textSofComment.getText().toString().replaceAll("(\r\n|\n)", "<br/>");
        }else {
            mainTextString = textSofComment.getText().toString().replaceAll("(\r\n|\n)", "<br/>");
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


    // ADD ACTIVITY
    private class HttpAsyncTaskSendMAil extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(PostCharteringStatementOfFacts.this);
            progDailog.setMessage("Loading data...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();


        }

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Uzimamao Json podatke o Useru
            try {

                JSONObject mainObject = new JSONObject(result);
                Log.e("Main Object", mainObject.toString());

                String res = mainObject.getString("result");

                if (res.equals("success")) {
                    Toast.makeText(getBaseContext(), "You have successfully sent email.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Error.", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            progDailog.dismiss();

        }
    }

    // Send and get results
    public String POST(String url){

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("email", userMail);
            jsonObject.accumulate("subject", "POST CHARTERING / STATEMENT OF FACT");
            jsonObject.accumulate("body", mainTextString);
            jsonObject.accumulate("files", arr);


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            Log.e("Saljemo", json.toString());

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "Bearer "+accessToken);

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.e("MESSAGE",result);

            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        // 11. return result
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
