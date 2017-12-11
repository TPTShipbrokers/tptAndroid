package com.tpt.borne.tpt.settings;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.models.User;
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
 * Created by Jovan Milutinović on 4.6.2016..
 */
public class Password extends AppCompatActivity {

    String accessToken;
    String password;

    RelativeLayout savePassword;
    EditText typeOld;
    EditText typePassword;
    EditText retypePassword;

    String newPasswordText;
    ProgressDialog progDailog;
    SharedPreferences.Editor editor;
    String userMail;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_password);


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
        accessToken = sharedpreferences.getString("accessToken", "");
        password = sharedpreferences.getString("userPassword", "");
        userMail = sharedpreferences.getString("userEmail", "");



        typeOld = (EditText) findViewById(R.id.typeOld);
        typePassword = (EditText) findViewById(R.id.typePassword);
        retypePassword = (EditText) findViewById(R.id.reTypePassword);


        savePassword = (RelativeLayout) findViewById(R.id.linearLayout3);
        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    accessToken = sharedpreferences.getString("accessToken", "");

                    if (!(typeOld.getText().toString().equals(password))) {
                        Toast.makeText(getBaseContext(), "Please enter correct password.", Toast.LENGTH_SHORT).show();
                    } else if ((typePassword.getText().toString().equals(""))
                            || (retypePassword.getText().toString().equals(""))) {
                        Toast.makeText(getBaseContext(), "Password must not be empty.", Toast.LENGTH_SHORT).show();

                    } else if (!(typePassword.getText().toString().equals(retypePassword.getText().toString()))) {
                        Toast.makeText(getBaseContext(), "Your passwords don't match.", Toast.LENGTH_SHORT).show();

                    } else {
                        newPasswordText = typePassword.getText().toString();
                        new HttpAsyncTaskPassword().execute("http://borne.io/tpt/api/user");
                    }
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
                InputMethodManager inputManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(Password.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // CHANGE PASSWORD
    private class HttpAsyncTaskPassword extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(Password.this);
            progDailog.setMessage("Loading data...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            User u = new User();
            u.setUserPAssword(newPasswordText);
            u.setUsermail(userMail);

            return PUT(urls[0], u);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Uzimamao Json podatke o Useru
            try {

                JSONObject json = new JSONObject(result);

                String res = json.getString("result");

                if(res.equals("success")){

                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.putString("userPassword", newPasswordText);
                    editor.commit();

                    finish();
                    InputMethodManager inputManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(Password.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    Toast.makeText(getBaseContext(), "You have successfully changed password.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "Password field can not be empty.", Toast.LENGTH_SHORT).show();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            progDailog.dismiss();


        }
    }




    // Send and get results
    public String PUT(String url, User user){
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
            jsonObject.accumulate("email", user.getUsermail());
            jsonObject.accumulate("password", user.getUserPAssword());

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
