package com.tpt.borne.tpt.mreports;

import android.annotation.TargetApi;
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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.adapters.DrawerAdapter;
import com.tpt.borne.tpt.adapters.MarketReportsAdapter;
import com.tpt.borne.tpt.chartering.Chartering;
import com.tpt.borne.tpt.contact.Contact;
import com.tpt.borne.tpt.login.Login;
import com.tpt.borne.tpt.models.MarketReport;
import com.tpt.borne.tpt.positions.PositionsMr;
import com.tpt.borne.tpt.postchartering.PostChartering;
import com.tpt.borne.tpt.settings.Settings;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

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
 * Created by Borne on 6/10/2016.
 */
public class MarketReports extends AppCompatActivity implements AdapterView.OnItemClickListener {


    String accessToken;

    private DrawerLayout drawerLayout;
    private ListView lista;
    private ActionBarDrawerToggle drawerListener;
    DrawerAdapter adapter;

    RelativeLayout logOut;
    TextView userFullName;
    ProgressDialog progDailog;

    ArrayList<MarketReport> marketList;
    MarketReportsAdapter adapterRec;
    ListView list;

    String name;
    String surname;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_report);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }

        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);

        Statics.drawerIndicator = 3;


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueMain)));


        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_drawer_icon);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        final String profilePicture = sharedpreferences.getString("profile_picture", "");
        name = sharedpreferences.getString("firstName", "");
        surname = sharedpreferences.getString("lastName", "");


        // ACTION BAR DRAWER
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lista = (ListView) findViewById(R.id.left_drawer);

        RoundedImageView imagePlace = (RoundedImageView) findViewById(R.id.imgProfile);
        Picasso.with(getBaseContext())
                .load("http://borne.io/tpt/" + profilePicture)
                .placeholder(R.drawable.mentor_default_image)
                .fit().centerCrop()
                .into(imagePlace);

        // set a custom shadow that overlays the main content when the drawer opens
        adapter = new DrawerAdapter(this);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(this);

        // podesavanje drawer-a
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.menuicon,
                R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                // TODO Auto-generated method stub
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // TODO Auto-generated method stub
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerListener);

        logOut = (RelativeLayout) findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketReports.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
                getBaseContext().startActivity(intent);
                Toast.makeText(getBaseContext(), "You have been Logged out", Toast.LENGTH_SHORT).show();            }
        });

        userFullName = (TextView) findViewById(R.id.nameSurname);
        userFullName.setText(name+" "+surname);
        // DRAWER END


        new HttpAsyncGetContacts().execute("http://borne.io/tpt/api/market_reports");

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
        drawerLayout.closeDrawer(Gravity.LEFT);

        unregisterBroadcastReceiver();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Statics conn = new Statics(getBaseContext());
        if (conn.isConnected(getBaseContext()) == false) {
            Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
        } else {
            switch (position) {
                case 0:
                    Intent i = new Intent(this, PositionsMr.class);
                    startActivity(i);
                    finish();
                    break;

                case 1:
                    Intent a = new Intent(this, Chartering.class);
                    startActivity(a);
                    finish();
                    break;

                case 2:
                    Intent b = new Intent(this, PostChartering.class);
                    startActivity(b);
                    finish();
                    break;

                case 3:
                    break;

                case 4:
                    Intent d = new Intent(this, Settings.class);
                    startActivity(d);
                    finish();
                    break;

                case 5:
                    Intent e = new Intent(this, Contact.class);
                    startActivity(e);
                    finish();
                    break;


                default:
                    break;
            }
        }
    }




    // GET EXEERCISES
    private class HttpAsyncGetContacts extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(MarketReports.this);
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

                    JSONArray groups = mainObject.getJSONArray("data");
                    marketList = new ArrayList<>();


                    for(int i =0; i<groups.length(); i++){


                        JSONObject c = groups.getJSONObject(i);
                        MarketReport model = new MarketReport();

                        model.setTitle(c.getString("title"));
                        model.setDate(c.getString("date"));
                        model.setFile(c.getString("file"));
                        model.setNewsletter_id(c.getString("newsletter_id"));

                        Log.e("Model", String.valueOf(model.getTitle()));

                        marketList.add(model);
                    }


                    Log.e("CONTACT SAVED", groups.toString());


                    list = (ListView) findViewById(R.id.listReports);
                    adapterRec = new MarketReportsAdapter(getBaseContext(), R.layout.row_market_report, marketList);
                    list.setAdapter(adapterRec);

                    // Settin dynamically list height
                    ListAdapter listAdapter = list.getAdapter();
                    if (listAdapter == null) {
                    }
                    int desiredWidth = View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.AT_MOST);
                    int totalHeight = 0;
                    View view = null;
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        view = listAdapter.getView(i, view, list);
                        if (i == 0) {
                            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, GridLayout.LayoutParams.WRAP_CONTENT));
                        }
                        view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                        totalHeight += view.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = list.getLayoutParams();
                    params.height = totalHeight + (list.getDividerHeight() * (listAdapter.getCount() - 1));
                    list.setLayoutParams(params);
                    list.requestLayout();
                    list.setFocusable(false);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Statics conn = new Statics(getBaseContext());
                            if (conn.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                            } else {

                                MarketReport model = (MarketReport) parent.getItemAtPosition(position);
                                String title = model.getTitle();
                                String url = model.getFile();

                                Intent i = new Intent(getBaseContext(), PDFparser.class);
                                i.putExtra("title", title);
                                i.putExtra("url", url);
                                startActivity(i);
                            }

                        }


                    });


                }else{
                    list = (ListView) findViewById(R.id.listContact);
                    TextView listEm = (TextView) findViewById(R.id.listEmpty);
                    listEm.setText("There are no contacts yet.");
                    listEm.setTextColor(getResources().getColor(R.color.blueMain));
                    list.setEmptyView(listEm);

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
