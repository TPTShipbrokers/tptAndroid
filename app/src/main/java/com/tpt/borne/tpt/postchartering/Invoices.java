package com.tpt.borne.tpt.postchartering;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.models.ShipDocument;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;
import com.tpt.borne.tpt.webview.WebView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Borne on 6/17/2016.
 */
public class Invoices extends AppCompatActivity {


    String accessToken;
    ProgressDialog progDailog;

    TextView shipNameText;
    TextView statusText;
    TextView dateText;
    TextView textShipCharter;

    RelativeLayout markAll;
    int count = 0;
    ArrayList<ImageView> imgViewList;
    ArrayList<ShipDocument> shipsList;

    JSONArray arr;

    int counterAsynName = 0;

    boolean selectedAll = false;
    boolean selectedSingle = false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    RelativeLayout downlaodDocuments;
    RelativeLayout emailDocumentation;
    ProgressDialog pDialog;


    // Setting lists for files and its names
    ArrayList<String> fileNames;
    ArrayList<String> files;

    int toastCounter = 0;
    String userMail;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chartering_ship_documentation);

        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        userMail = sharedpreferences.getString("email", "");


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


        markAll = (RelativeLayout) findViewById(R.id.markAllLAy);
        downlaodDocuments = (RelativeLayout) findViewById(R.id.linearLayout3);
        downlaodDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    accessToken = sharedpreferences.getString("accessToken", "");
                    if (verifyStoragePermissions(Invoices.this) == true) {

                        startDownload();
                        toastCounter = 0;
                    }
                }

            }
        });


        emailDocumentation = (RelativeLayout) findViewById(R.id.linearLayout4);
        emailDocumentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                    accessToken = sharedpreferences.getString("accessToken", "");
                    new HttpAsyncTaskSendMAil().execute("http://borne.io/tpt/api/default/send_email");
                }
            }
        });

        Intent i = getIntent();

        String status = i.getStringExtra("status");
        String sofComments = i.getStringExtra("sofComments");
        String date = i.getStringExtra("date");
        String vesselName = i.getStringExtra("vesselName");
        String charteringId = i.getStringExtra("charteringId");

        Log.e("Chartering id", charteringId);

        shipNameText = (TextView) findViewById(R.id.shipName);
        shipNameText.setText(vesselName);
        statusText = (TextView) findViewById(R.id.status);
        statusText.setText(status);
        dateText = (TextView) findViewById(R.id.date);
        dateText.setText(date);


        new HttpAsyncShipDocumentation().execute("http://borne.io/tpt/api/chartering/"+charteringId);


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
    private class HttpAsyncShipDocumentation extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(Invoices.this);
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

                TextView textDo = (TextView) findViewById(R.id.textTitle);

                JSONObject mainObject = new JSONObject(result);

                JSONObject party = mainObject.getJSONObject("data");

                final JSONArray shipDocumentation = party.getJSONArray("invoice_documentations");

                Log.e("Documentation", shipDocumentation.toString());

// LINEAR LAYOUT ZA MAIN In WHICH PUT OTHER LAYOUTS
                final LinearLayout layoutMain = (LinearLayout) findViewById(R.id.layoutMain);
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                int imgSize = convertPix(18);
                final LinearLayout.LayoutParams paramsImg = new LinearLayout.LayoutParams(imgSize, imgSize);
                int margin = convertPix(5);
                paramsImg.setMargins(margin,margin,margin,margin);

                final LinearLayout.LayoutParams paramsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int margintext = convertPix(5);
                paramsTextView.setMargins(margintext,margintext,margintext,margintext);

                layoutMain.setLayoutParams(paramsTextView);


                imgViewList = new ArrayList<>();

                fileNames = new ArrayList<>();
                files = new ArrayList<>();

                for (int i =0; i<shipDocumentation.length(); i++){

                    JSONObject c = (JSONObject) shipDocumentation.get(i);
                    final ShipDocument model = new ShipDocument();

                    model.setFile(c.getString("file"));

                    if(c.getString("filename").equals("null")){
                        model.setFileName("No invoice");
                    }else{
                        model.setFileName(c.getString("filename"));
                    }


                    final LinearLayout insideLay = new LinearLayout(getBaseContext());
                    insideLay.setGravity(Gravity.CENTER_VERTICAL);
                    insideLay.setOrientation(LinearLayout.HORIZONTAL);
                    insideLay.setBackgroundColor(Color.parseColor("#ffffff"));
                    int padding = convertPix(5);
                    insideLay.setPadding(padding,padding,padding,padding);
                    insideLay.setLayoutParams(params);

// Setting imageview
                    boolean ifSelected = ifFileExists(model.getFileName());

                    final ImageView img = new ImageView(getBaseContext());
                    if(ifSelected == true) {
                        img.setImageResource(R.drawable.check_box_checked);
                    }else{
                        img.setImageResource(R.drawable.check_box_empty);

                    }
                    img.setLayoutParams(paramsImg);
                    imgViewList.add(img);
                    img.setId(i);


// Setting TextView
                    TextView description = new TextView(getBaseContext());
                    description.setTextSize(14);
                    description.setGravity(Gravity.CENTER_VERTICAL);
                    description.setText(model.getFileName());
                    if(ifSelected == true){
                        description.setTextColor(getResources().getColor(R.color.blueMain));
                    }else{
                        description.setTextColor(getResources().getColor(R.color.greyHint));
                    }
                    description.setLayoutParams(paramsTextView);


                    if(ifSelected == true){
                        files.add(model.getFile());
                        fileNames.add(model.getFileName());
                    }


                    insideLay.addView(img);
                    insideLay.addView(description);

                    layoutMain.addView(insideLay);


// MARK ALL SECTION
                    markAll.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(selectedAll == false) {

                                fileNames.clear();
                                files.clear();

                                for (int j = 0; j < shipDocumentation.length(); j++) {
                                    imgViewList.get(j).setId(j);
                                    imgViewList.get(j).setImageResource(R.drawable.check_box_checked);

                                    selectedAll = true;
                                    TextView markAll = (TextView) findViewById(R.id.unmarkSign);
                                    markAll.setText("Unmark All");

                                    JSONObject c = null;
                                    try {
                                        c = (JSONObject) shipDocumentation.get(j);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    final ShipDocument model = new ShipDocument();

                                    try {

                                        model.setFile(c.getString("file"));
                                        if(c.getString("filename").equals("null")){
                                            model.setFileName("No invoice");
                                        }else{
                                            model.setFileName(c.getString("filename"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    fileNames.add(model.getFileName());
                                    files.add(model.getFile());

                                }
                            }else{

                                layoutMain.removeAllViews();

                                fileNames.clear();
                                files.clear();

                                imgViewList = new ArrayList<>();
                                selectedAll = false;


                                for (int j = 0; j < shipDocumentation.length(); j++) {

                                    JSONObject c = null;
                                    try {
                                        c = (JSONObject) shipDocumentation.get(j);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    final ShipDocument model = new ShipDocument();

                                    try {

                                        model.setFile(c.getString("file"));
                                        if(c.getString("filename").equals("null")){
                                            model.setFileName("No invoice");
                                        }else{
                                            model.setFileName(c.getString("filename"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    final LinearLayout insideLay = new LinearLayout(getBaseContext());
                                    insideLay.setGravity(Gravity.CENTER_VERTICAL);
                                    insideLay.setOrientation(LinearLayout.HORIZONTAL);
                                    insideLay.setBackgroundColor(Color.parseColor("#ffffff"));
                                    int padding = convertPix(5);
                                    insideLay.setPadding(padding,padding,padding,padding);
                                    insideLay.setLayoutParams(params);


// Setting TextView
                                    boolean ifSelected = ifFileExists(model.getFileName());

                                    TextView description = new TextView(getBaseContext());
                                    description.setTextSize(14);
                                    description.setGravity(Gravity.CENTER_VERTICAL);
                                    description.setText(model.getFileName());
                                    if(ifSelected == true){
                                        description.setTextColor(getResources().getColor(R.color.blueMain));
                                    }else{
                                        description.setTextColor(getResources().getColor(R.color.greyHint));
                                    }
                                    description.setLayoutParams(paramsTextView);


// Setting imageview
                                    final ImageView img = new ImageView(getBaseContext());
                                    if(ifSelected == true) {
                                        img.setImageResource(R.drawable.check_box_checked);
                                    }else{
                                        img.setImageResource(R.drawable.check_box_empty);
                                    }

                                    img.setLayoutParams(paramsImg);
                                    imgViewList.add(img);
                                    img.setId(j);


                                    insideLay.addView(img);
                                    insideLay.addView(description);
                                    layoutMain.addView(insideLay);


                                    if(ifSelected == true){
                                        files.add(model.getFile());
                                        fileNames.add(model.getFileName());
                                    }

                                    imgViewList.get(j).setId(j);
                                    if(ifSelected == true) {
                                        imgViewList.get(j).setImageResource(R.drawable.check_box_checked);
                                    }else{
                                        imgViewList.get(j).setImageResource(R.drawable.check_box_empty);
                                    }

                                    TextView markAll = (TextView) findViewById(R.id.unmarkSign);
                                    markAll.setText("Mark All");



                                    insideLay.setOnClickListener(new View.OnClickListener() {
                                        boolean clicked = false;
                                        @Override
                                        public void onClick(View v) {

                                            if(selectedAll == true){
                                                Toast.makeText(getBaseContext(), "Please unmark all items first.", Toast.LENGTH_SHORT).show();
                                            }else {

                                                boolean ifSelected = ifFileExists(model.getFileName());

                                                if (ifSelected == true) {
                                                    Statics conn = new Statics(getBaseContext());
                                                    if (conn.isConnected(getBaseContext()) == false) {
                                                        Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        Intent i = new Intent(getBaseContext(), WebView.class);
                                                        i.putExtra("title", model.getFileName());
                                                        i.putExtra("url", model.getFile());
                                                        startActivity(i);
                                                    }


                                                } else {
                                                    img.setImageResource(R.drawable.check_box_empty);

                                                    if (clicked == false) {
                                                        img.setImageResource(R.drawable.check_box_checked);
                                                        clicked = true;
                                                        Log.e("Obelezeno", String.valueOf(img.getId()) + " " + model.getFileName());
                                                        fileNames.add(model.getFileName());
                                                        files.add(model.getFile());
                                                    } else if (clicked == true) {
                                                        img.setImageResource(R.drawable.check_box_empty);
                                                        clicked = false;
                                                        Log.e("Neobelezeno", String.valueOf(img.getId()) + " " + model.getFileName());
                                                        fileNames.remove(model.getFileName());
                                                        files.remove(model.getFile());
                                                    }
                                                }
                                            }

                                        }
                                    });


                                }
                            }

                        }
                    });


                    insideLay.setOnClickListener(new View.OnClickListener() {
                        boolean clicked = false;
                        @Override
                        public void onClick(View v) {

                            if(selectedAll == true){
                                Toast.makeText(getBaseContext(), "Please unmark all items first.", Toast.LENGTH_SHORT).show();
                            }else {
                                boolean ifSelected = ifFileExists(model.getFileName());

                                if(ifSelected == true){
                                    Statics conn = new Statics(getBaseContext());
                                    if (conn.isConnected(getBaseContext()) == false) {
                                        Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent i = new Intent(getBaseContext(), WebView.class);
                                        i.putExtra("title", model.getFileName());
                                        i.putExtra("url", model.getFile());
                                        startActivity(i);
                                    }

                                }else {
                                    img.setImageResource(R.drawable.check_box_empty);

                                    if (clicked == false) {
                                        img.setImageResource(R.drawable.check_box_checked);
                                        clicked = true;
                                        Log.e("Obelezeno", String.valueOf(img.getId()) + " " + model.getFileName());
                                        fileNames.add(model.getFileName());
                                        files.add(model.getFile());
                                    } else if (clicked == true) {
                                        img.setImageResource(R.drawable.check_box_empty);
                                        clicked = false;
                                        Log.e("Neobelezeno", String.valueOf(img.getId()) + " " + model.getFileName());
                                        fileNames.remove(model.getFileName());
                                        files.remove(model.getFile());
                                    }
                                }
                            }

                        }
                    });
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

    // Converting pixels to dp
    public int convertPix(int paddingPixel){
        float density = getBaseContext().getResources().getDisplayMetrics().density;
        int paddingDp = (int)(paddingPixel * density);
        return paddingDp;
    }


    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

            return false;
        }else{
            return true;
        }
    }



    public void startDownload(){

        for (int i =0; i<fileNames.size(); i++){

            if(!(fileNames.get(i).equals("No invoice"))){
                Log.e("FILENAMES", fileNames.get(i));
                String filePath = files.get(i);
                filePath = filePath.replace(" ", "%20");

                String name = fileNames.get(i);

                ArrayList<String> parameters = new ArrayList<>();
                parameters.add("http://borne.io/tpt/"+filePath);
                parameters.add(name);

                new DownloadFileFromURL().execute(parameters);
            }

        }

        for (int i =0; i<files.size(); i++){
            Log.e("FILES", files.get(i));
        }

    }


    class DownloadFileFromURL extends AsyncTask<ArrayList<String>,String, ArrayList<String>> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/tpt/invoices");
            folder.mkdirs();


        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... f_url) {
            int count;
            try {
                ArrayList<String> lis = f_url[0];
                String p = lis.get(0);
                String name = lis.get(1);

                URL url = new URL(p);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                OutputStream output = null;

                output = new FileOutputStream(Environment.getExternalStorageDirectory()
                        + "/tpt/invoices/"+name);

                Log.e("NAM", name);



                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(ArrayList<String> file_url) {
            // dismiss the dialog after the file was downloaded

            if(toastCounter == 0){
                Toast.makeText(getBaseContext(), "You have successfully downloaded file/files.", Toast.LENGTH_SHORT).show();
            }
            toastCounter++;

        }
    }

    // Checking if file alreadu exists
    public boolean ifFileExists(String filename){
        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/tpt/invoices/"+filename);

        if(myFile.exists()){
            return  true;
        }else{
            return false;
        }
    }


    // ADD ACTIVITY
    private class HttpAsyncTaskSendMAil extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(Invoices.this);
            progDailog.setMessage("Loading data...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();


            arr = new JSONArray();

            for(int i =0; i <fileNames.size(); i++){
                JSONObject obj = new JSONObject();
                try {
                    obj.put("file", files.get(i));
                    obj.put("filename", fileNames.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arr.put(obj);

            }



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
            jsonObject.accumulate("subject", "POST CHARTERING / INVOICES");
            jsonObject.accumulate("body", "SHIP DOCUMENTATION");
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


}
