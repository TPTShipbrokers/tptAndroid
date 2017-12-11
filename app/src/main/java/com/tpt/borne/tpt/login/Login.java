package com.tpt.borne.tpt.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.models.User;
import com.tpt.borne.tpt.positions.PositionsMr;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Login extends AppCompatActivity {

    String p;

    EditText insertEmail;
    EditText insertPassword;

    String loginPost;
    User user;
    ProgressDialog progDailog;

    String mail;
    String password;

    RelativeLayout loginButt;
    String accessToken;
    String refreshToken;
    String tokenDate;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String recordedMail;

    String profile_picture;
    RelativeLayout registerAccount;
    String userMail;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginButt = (RelativeLayout) findViewById(R.id.linearLayout4);

        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute("http://borne.io/tpt/api/oauth2/token");
            }
        });


        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);


        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        recordedMail = sharedpreferences.getString("userEmail", "");
        userMail = sharedpreferences.getString("email", "");


        registerAccount = (RelativeLayout) findViewById(R.id.linearLayout2);
        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "admin@tunept.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TPT registration request");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });

        if(!(recordedMail.equals(""))){
            insertEmail = (EditText) findViewById(R.id.insertEmail);
            insertEmail.setText(recordedMail);
            insertEmail.setCursorVisible(true);
            insertPassword = (EditText) findViewById(R.id.insertPassword);
            insertPassword.requestFocus();
        }

        insertPassword = (EditText) findViewById(R.id.insertPassword);
        insertPassword.setTypeface(Typeface.DEFAULT);
        insertPassword.setTransformationMethod(new PasswordTransformationMethod());

        insertPassword.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            Statics conn = new Statics(getBaseContext());
                            if (conn.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                            } else {
                                new HttpAsyncTask().execute("http://borne.io/tpt/api/oauth2/token");
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

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


    // LOGIN USER
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(Login.this);
            progDailog.setMessage("Loading data...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

            // Fields for user and password
            insertEmail = (EditText) findViewById(R.id.insertEmail);
            insertPassword = (EditText) findViewById(R.id.insertPassword);
            mail = insertEmail.getText().toString();
            password = insertPassword.getText().toString();


            Log.e("Saljemo", mail+" "+password);

        }

        @Override
        protected String doInBackground(String... urls) {

            user = new User();
            user.setUsermail(mail);
            user.setUserPAssword(password);

            return POST(urls[0],user);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Uzimamao Json podatke o Useru
            try {

                JSONObject mainObject = new JSONObject(result);


                Log.e("OBJEKAT 1", mainObject.toString());
                //Message returnig from server
                if (mainObject.has("error")){
                    String getMessage = mainObject.getString("error");
                    String messageDescription = mainObject.getString("error_description");
                    if(!(getMessage.equals(""))){
                        Toast.makeText(getBaseContext(), messageDescription,
                                Toast.LENGTH_LONG).show();
                    }

                    progDailog.dismiss();

                }else {
                    //Getting data about user
                    accessToken = mainObject.getString("access_token");
                    refreshToken = mainObject.getString("refresh_token");
                    tokenDate = mainObject.getString("issued");

                    // Setting password i shared preferences
                    sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.putString("userPassword", user.getUserPAssword());
                    editor.putString("userEmail", user.getUsermail());
                    editor.putString("accessToken", accessToken);
                    editor.putString("refreshToken", refreshToken);
                    String dateTokenToZone = convertTimeZone(tokenDate);
                    editor.putString("dateToken", dateTokenToZone);
                    editor.commit();

                    Log.e("TOKEN DATE", tokenDate);

                    new HttpAsyncTaskDetails().execute("http://borne.io/tpt/api/user");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    // USER DETAILS
    private class HttpAsyncTaskDetails extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


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
                JSONObject userData = mainObject.getJSONObject("data");

                Log.e("OBJEKAT 2", mainObject.toString());

                String userId = userData.getString("user_id");
                String firstName = userData.getString("first_name");
                String lastName = userData.getString("last_name");
                String email = userData.getString("email");
                String email2 = userData.getString("email2");
                String position = userData.getString("position");
                String phone2 = userData.getString("phone2");
                String companyId = userData.getString("company_id");
                profile_picture = userData.getString("profile_picture");
                String phone = userData.getString("phone");
                String marketReportAccessLevel = userData.getString("market_report_access_level");
                String role = userData.getString("role");
                String api_endpoint = userData.getString("api_endpoint");


                JSONObject userDataNotifications = userData.getJSONObject("notification_settings");

                String notification_settings_id = userDataNotifications.getString("notification_settings_id");
                String outstanding_claims = userDataNotifications.getString("outstanding_claims");
                String subsdue = userDataNotifications.getString("subs_due");
                String live_position_updates = userDataNotifications.getString("live_position_updates");


                // Setting password i shared preferences
                sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.putString("userId", userId);
                editor.putString("firstName", firstName);
                editor.putString("lastName", lastName);
                editor.putString("email", email);
                editor.putString("email2", email2);
                editor.putString("position", position);
                editor.putString("phone", phone);
                editor.putString("phone2", phone2);
                editor.putString("companyId", companyId);
                editor.putString("profile_picture", profile_picture);
                editor.putString("marketReportAccessLevel", marketReportAccessLevel);
                editor.putString("role", role);
                editor.putString("api_endpoint", api_endpoint);


                editor.putString("notification_settings_id", notification_settings_id);
                editor.putString("outstanding_claims", outstanding_claims);
                editor.putString("subsdue", subsdue);
                editor.putString("live_position_updates", live_position_updates);
                editor.commit();


                Intent i = new Intent(getBaseContext(), PositionsMr.class);
                startActivity(i);
                finish();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            progDailog.dismiss();


        }
    }



    // Send and get results
    public String POST(String url, User user){

        InputStream inputStream = null;
        String result = "";

        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            loginPost = "grant_type=password&username="+user.getUsermail()+"&password="+user.getUserPAssword()+
                    "&client_id=mobileapp&client_secret=mobileappsecret";

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(loginPost);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.e("MESSAGE", result);
                Log.e("POSTOVANO", loginPost);

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


    public String convertTimeZone(String time){

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsed=null;
        try {
            parsed = sourceFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(tz);
        String ourDate = simpleDateFormat.format(parsed);
        return ourDate;
    }


}
