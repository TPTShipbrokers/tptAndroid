package com.tpt.borne.tpt.settings;

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
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jovan Milutinović on 5.6.2016..
 */
public class SettingsNotifications extends AppCompatActivity {

    String changeVariable;
    String changeValueSubsDue;
    String changeValuePositions;
    String outstanding_claims;
    String subsdue;
    String live_position_updates;

    SwitchCompat switchSubs;
    SwitchCompat livePosition;


    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    String accessToken;

    RelativeLayout saveBut;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_notifications);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }

        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);


        sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        outstanding_claims = sharedpreferences.getString("outstanding_claims", "");
        subsdue = sharedpreferences.getString("subsdue", "");
        live_position_updates = sharedpreferences.getString("live_position_updates", "");


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueMain)));


        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_hardware_keyboard_backspace);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);



        switchSubs = (SwitchCompat) findViewById(R.id.switchSubs);
        if(subsdue.equals("1")){
            switchSubs.setChecked(true);
        }else{
            switchSubs.setChecked(false);
        }
        switchSubs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    accessToken = sharedpreferences.getString("accessToken", "");
                    // do something, the isChecked will be
                    // true if the switch is in the On position
                    changeVariable = "Subs due";
                    if (isChecked) {
                        changeValueSubsDue = "1";
                    } else {
                        changeValueSubsDue = "0";
                    }
                    new HttpAsyncTaskSwitch().execute("http://borne.io/tpt/api/user/change_notification_settings");

                }
            }
        });


        livePosition = (SwitchCompat) findViewById(R.id.livePosition);
        if(live_position_updates.equals("1")){
            livePosition.setChecked(true);
        }else{
            livePosition.setChecked(false);
        }
        livePosition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    accessToken = sharedpreferences.getString("accessToken", "");
                    // do something, the isChecked will be
                    // true if the switch is in the On position
                    changeVariable = "Live position updates";
                    if (isChecked) {
                        changeValuePositions = "1";
                    } else {
                        changeValuePositions = "0";
                    }
                    new HttpAsyncTaskSwitch().execute("http://borne.io/tpt/api/user/change_notification_settings");

                }
            }
        });


        saveBut = (RelativeLayout) findViewById(R.id.linearLayout3);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    accessToken = sharedpreferences.getString("accessToken", "");
                    finish();
                }
            }
        });



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



    // CHANGE EVERY FIELD
    private class HttpAsyncTaskSwitch extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            return PUT(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Uzimamao Json podatke o Useru
            try {

                JSONObject json = new JSONObject(result);


                String res = json.getString("result");

                Log.e("RESSSSSS", json.toString());

                if(res.equals("success")){
                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();

                    if(changeVariable.equals("Subs due")){
                        editor.putString("subsdue", changeValueSubsDue);
                    }else if(changeVariable.equals("Live position updates")){
                        editor.putString("live_position_updates", changeValuePositions);
                    }

                    editor.commit();

                    Toast.makeText(getBaseContext(), "You have changed notifications for "+changeVariable, Toast.LENGTH_SHORT).show();
                    Log.e("RESULTATI", res);
                }else{
                    String error = json.getString("message");
                    Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    // Send and get results
    public String PUT(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPut httpPut = new HttpPut(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("subs_due", changeValueSubsDue);
            jsonObject.accumulate("live_position_updates", changeValuePositions);


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            Log.e("JSON", json);

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPut.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPut.setHeader("Content-type", "application/json");
            httpPut.setHeader("Authorization", "Bearer "+accessToken);


            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPut);

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
