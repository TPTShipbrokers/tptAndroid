package com.tpt.borne.tpt.chartering;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.adapters.CharteringAdapter;
import com.tpt.borne.tpt.adapters.DrawerAdapter;
import com.tpt.borne.tpt.email.EmailChartering;
import com.tpt.borne.tpt.login.Login;
import com.tpt.borne.tpt.models.PostCharteringModel;
import com.tpt.borne.tpt.mreports.MarketReports;
import com.tpt.borne.tpt.positions.PositionsMr;
import com.tpt.borne.tpt.postchartering.PostChartering;
import com.tpt.borne.tpt.settings.Settings;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tpt.borne.tpt.contact.Contact;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Borne on 6/13/2016.
 */
public class Chartering extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String accessToken;

    private DrawerLayout drawerLayout;
    private ListView lista;
    private ActionBarDrawerToggle drawerListener;
    DrawerAdapter adapter;

    RelativeLayout logOut;
    TextView userFullName;
    String name;
    String surname;
    ProgressDialog progDailog;

    List<PostCharteringModel> listDataHeader;
    HashMap<PostCharteringModel, List<PostCharteringModel>> listDataChild;

    ExpandableListView list;
    ArrayList<PostCharteringModel> postChartsList;
    CharteringAdapter adapterRec;

    int totalHeight;

    RelativeLayout callLay;
    RelativeLayout emailChartering;


    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chartering_main);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }


        Statics.drawerIndicator = 1;

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


        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_drawer_icon);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        callLay = (RelativeLayout) findViewById(R.id.linearLayout3);
        callLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "+44 20 37448041";
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+Uri.encode(number.trim())));

                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        emailChartering = (RelativeLayout) findViewById(R.id.linearLayout4);
        emailChartering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), EmailChartering.class);
                startActivity(i);
            }
        });




        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        final String profilePicture = sharedpreferences.getString("profile_picture", "");
        name = sharedpreferences.getString("firstName", "");
        surname = sharedpreferences.getString("lastName", "");

//
        String dateStart = "01/15/2015 08:29:58";
        String dateStop = "01/15/2015 11:31:48";

//HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//in milliseconds
        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        Log.e("razlika", diffDays+":"+diffHours+":"+diffMinutes+":"+diffSeconds);


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
                Intent intent = new Intent(Chartering.this, Login.class);
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

        new HttpAsyncChartering().execute("http://borne.io/tpt/api/chartering");

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
                    break;

                case 2:
                    Intent b = new Intent(this, PostChartering.class);
                    startActivity(b);
                    finish();
                    break;

                case 3:
                    Intent c = new Intent(this, MarketReports.class);
                    startActivity(c);
                    finish();
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


    // POST CHARTERI|NG
    private class HttpAsyncChartering extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(Chartering.this);
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
                    postChartsList = new ArrayList<>();



                    listDataHeader = new ArrayList<PostCharteringModel>();
                    listDataChild = new HashMap<PostCharteringModel, List<PostCharteringModel>>();

// GETTING ALL POST CHARTERING
                    for(int i =0; i<groups.length(); i++){

                        JSONObject c = groups.getJSONObject(i);
                        PostCharteringModel model = new PostCharteringModel();

                        model.setSofComments(c.getString("sof_comments"));
                        model.setCgarteringId(c.getString("chartering_id"));
                        model.setState(c.getString("state"));
                        model.setSubsDueDate(c.getString("subs_due"));

                        if(c.has("vessel_name") && !(c.getString("vessel_name").equals("null"))){
                            String vessel = c.getString("vessel_name");
                            model.setVesselName(vessel);
                        }



                        if(c.has("status") && !(c.getString("status").equals("null"))){

                            JSONObject status = c.getJSONObject("status");
                            String date = status.getString("datetime");
                            model.setFullDate(date);
                            model.setDateFromStatus(status.getString("datetime"));
                            model.setDate(date);
                            JSONObject statusDetails = status.getJSONObject("status");
                            model.setStatus(statusDetails.getString("description"));
                        }else if(c.has("status") && c.getString("status").equals("null")){
                            model.setStatus("STATUS NOT SET");
                        }

                        listDataHeader.add(model);

                        List<PostCharteringModel> comingSoon = new ArrayList<PostCharteringModel>();
                        comingSoon.add(model);

                        listDataChild.put(listDataHeader.get(i), comingSoon); // Header, Child data

//                        postChartsList.add(model);
                    }


                    list = (ExpandableListView) findViewById(R.id.listPostChartering);
                    adapterRec = new CharteringAdapter(getBaseContext(), listDataHeader, listDataChild);
                    list.setAdapter(adapterRec);

                    // Settin dynamically list height
                    ListAdapter listAdapter = list.getAdapter();
                    if (listAdapter == null) {
                    }
                    int desiredWidth = View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.AT_MOST);
                    totalHeight = 0;
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


                    list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v,
                                                    int position, long id) {


                            setListViewHeight(parent, position);

                            ViewGroup.LayoutParams params = list.getLayoutParams();
                            int height = totalHeight
                                    + (list.getDividerHeight() * (adapterRec.getGroupCount() - 1));
                            if (height < 10)
                                height = 200;
                            params.height = height;
                            list.setLayoutParams(params);
                            list.requestLayout();

                            ImageView groupIndicator = (ImageView) v.findViewById(R.id.flag);
                            if (parent.isGroupExpanded(position)) {
                                parent.collapseGroup(position);
                            } else {
                                parent.expandGroup(position);
                            }

                            return true;
                        }
                    });



                }else{
                    list = (ExpandableListView) findViewById(R.id.listPostChartering);
                    TextView listEm = (TextView) findViewById(R.id.listEmpty);
                    listEm.setText("There is no any Chartering yet.");
                    listEm.setTextColor(getResources().getColor(R.color.blueMain));
                    list.setEmptyView(listEm);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            progDailog.dismiss();

        }
    }



    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();
                }
            }
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
