package com.tpt.borne.tpt.statics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.tpt.borne.tpt.models.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

/**
 * Created by Borne on 6/3/2016.
 */
public class Statics {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    User user;
    String accessToken;
    String refreshToken;
    String tokenDate;

    private Context context;
    public static int drawerIndicator =101;

    private static boolean isConnected = false;

    String mail;
    String password;
    String loginPost;

    public static final String tptMail = "chartering@tunept.com";
//        public static final String tptMail = "jovan.milutinovic84@gmail.com";



    public Statics(Context context){
        this.context = context;
    }


    public boolean isConnected(Context c){
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;

    }


    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            isConnected = true;
                        }
                        return true;
                    }
                }
            }
        }else
            isConnected = false;
        return false;


    }




    public void loginUser(){

        // taking date from preferences
        SharedPreferences sharedpreferences = context.getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        String dateToken = sharedpreferences.getString("dateToken", "Nothing found");

        Log.e("datetoken", dateToken);

// Taking token date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tokenDate=null;
        try {
            tokenDate = format.parse(dateToken);
        } catch (ParseException e) {
            e.printStackTrace();
        }



// Taking date from now
        Date dateNow = new Date();
        Log.e("Rzlika je ", String.valueOf(dateNow.getTime() - tokenDate.getTime()));
        Log.e("TOKENIIII", String.valueOf(tokenDate)+" "+String.valueOf(dateNow));

//82800000 - vrednost koj iznosi 23 sata
        if((dateNow.getTime() - tokenDate.getTime()) >= 82800000){

            new HttpAsyncTask().execute("http://borne.io/tpt/api/oauth2/token");
            Log.e("Logujemo usera ponovo", "Logovanje ide");

        }else{
            Log.e("ne radimo logovanje", "Logovanje ne ide");
        }



    }



    // LOGIN USER
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences sharedpreferences = context.getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
            mail = sharedpreferences.getString("userEmail", "");
            password = sharedpreferences.getString("userPassword", "");


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

                    //Getting data about user
                    accessToken = mainObject.getString("access_token");
                    refreshToken = mainObject.getString("refresh_token");
                    tokenDate = mainObject.getString("issued");

                    // Setting password i shared preferences
                    sharedpreferences = context.getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    editor = sharedpreferences.edit();
                    editor.putString("userPassword", user.getUserPAssword());
                    editor.putString("userEmail", user.getUsermail());
                    editor.putString("accessToken", accessToken);
                    editor.putString("refreshToken", refreshToken);
                    String dateTokenToZone = convertTimeZone(tokenDate);
                    editor.putString("dateToken", dateTokenToZone);
                    editor.commit();

                    Log.e("TOKEN DATE", tokenDate);



            } catch (JSONException e) {
                e.printStackTrace();
            }


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
