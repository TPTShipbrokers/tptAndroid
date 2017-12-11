package com.tpt.borne.tpt.positions;

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
import com.tpt.borne.tpt.adapters.DrawerAdapter;
import com.tpt.borne.tpt.adapters.PositionsAdapter;
import com.tpt.borne.tpt.chartering.Chartering;
import com.tpt.borne.tpt.login.Login;
import com.tpt.borne.tpt.models.Ship;
import com.tpt.borne.tpt.mreports.MarketReports;
import com.tpt.borne.tpt.postchartering.PostChartering;
import com.tpt.borne.tpt.settings.Settings;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.ParseInstallation;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jovan Milutinović on 12.10.2016..
 */
public class PositionsLr extends AppCompatActivity implements AdapterView.OnItemClickListener {


    String accessToken;

    NetworkChangeReceiver receiver;

    FrameLayout placeholder;
    TextView placeText;

    private DrawerLayout drawerLayout;
    private ListView lista;
    private ActionBarDrawerToggle drawerListener;
    DrawerAdapter adapter;


    RelativeLayout logOut;
    TextView userFullName;
    String name;
    String surname;
    ProgressDialog progDailog;

    List<Ship> listDataHeader;
    HashMap<Ship, List<Ship>> listDataChild;

    ExpandableListView list;
    ArrayList<Ship> postChartsList;
    PositionsAdapter adapterRec;

    int totalHeight;

    RelativeLayout positionsMr;
    RelativeLayout positionsSmall;

    RelativeLayout filterGroup;

    String urlWeSend = "http://borne.io/tpt/api/vessels/all/?size=lr";


    String sireValue;
    String temaValue;
    String ageValue;
    String gradeValue;
    String locationValue;

    String filtersYes;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.positions_lr);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }


        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);

        Statics.drawerIndicator = 0;


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
        userEmail = sharedpreferences.getString("userEmail", "");
        Log.e("user email", userEmail);
        filtersYes = sharedpreferences.getString("filtersYes", "");


        if(filtersYes.equals("true")){
            RelativeLayout filterGroup = (RelativeLayout) findViewById(R.id.filterGroup);
            filterGroup.setBackgroundResource(R.drawable.filter_shape_green);
        }



        sireValue = sharedpreferences.getString("shireValue", "");
        temaValue = sharedpreferences.getString("temaValue", "");
        ageValue = sharedpreferences.getString("ageValue", "");
        gradeValue = sharedpreferences.getString("gradeValue", "");
        locationValue = sharedpreferences.getString("locationValue", "");


// Connecting user to parse accont
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("email", userEmail);
        installation.saveInBackground();


        positionsMr = (RelativeLayout) findViewById(R.id.activitiesButt);
        positionsSmall = (RelativeLayout) findViewById(R.id.activitiesButtSec);
        filterGroup = (RelativeLayout) findViewById(R.id.filterGroup);



        positionsMr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getBaseContext(), PositionsMr.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        positionsSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics conn = new Statics(getBaseContext());
                if (conn.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), R.string.checkConnection, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getBaseContext(), PositionsSmall.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        filterGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), Filters.class);
                i.putExtra("from", "lr");
                startActivity(i);
            }
        });



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
                Intent intent = new Intent(PositionsLr.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
                getBaseContext().startActivity(intent);
                Toast.makeText(getBaseContext(), "You have been Logged out", Toast.LENGTH_SHORT).show();            }
        });

        userFullName = (TextView) findViewById(R.id.nameSurname);
        userFullName.setText(name+" "+surname);



        if(!(sireValue.equals("")) && sireValue != null){
            urlWeSend = urlWeSend+"&sire=yes";
        }
        if(!(temaValue.equals("")) && temaValue != null){
            urlWeSend =urlWeSend+"&tema_suitable=yes";
        }
        if((!ageValue.equals("")) && ageValue != null){
            urlWeSend = urlWeSend+"&age="+ageValue;
        }

        if((!gradeValue.equals(""))&& gradeValue != null){
            urlWeSend = urlWeSend+"&grade="+gradeValue;
        }
        if(!(locationValue.equals("")) && locationValue != null){
            urlWeSend = urlWeSend+"&location="+locationValue;
        }

        new HttpAsyncPositions().execute(urlWeSend);

        Log.e("URL WE SEND ", urlWeSend);


//        // DRAWER END
//        if((sireValue.equals("") || sireValue == null) && (temaValue.equals("") || temaValue == null) && (ageValue.equals("") || ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr");
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr");
//        }else if(!(sireValue.equals("") && sireValue == null) && (temaValue.equals("") || temaValue == null) && (ageValue.equals("") || ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes");
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes");
//        }else if((sireValue.equals("") || sireValue == null) && !(temaValue.equals("") && temaValue == null) && (ageValue.equals("") || ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr&tema_suitable=yes");
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr&tema_suitable=yes");
//        }else if((sireValue.equals("") || sireValue == null) && (temaValue.equals("") || temaValue == null) && !(ageValue.equals("") && ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr&age="+ageValue);
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr&age="+ageValue);
//        }else if(!(sireValue.equals("") && sireValue == null) && !(temaValue.equals("") && temaValue == null) && (ageValue.equals("") || ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes&tema_suitable=yes");
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes&tema_suitable=yes");
//        }else if((sireValue.equals("") || sireValue == null) && !(temaValue.equals("") && temaValue == null) && !(ageValue.equals("") && ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr&tema_suitable=yes&age="+ageValue);
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr&tema_suitable=yes&age="+ageValue);
//        }else if(!(sireValue.equals("") && sireValue == null) && (temaValue.equals("") || temaValue == null) && !(ageValue.equals("") && ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes&age="+ageValue);
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes&age="+ageValue);
//        }else if(!(sireValue.equals("") && sireValue == null) && !(temaValue.equals("") && temaValue == null) && !(ageValue.equals("") && ageValue == null)){
//            new HttpAsyncPositions().execute("http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes&tema_suitable=yes&age="+ageValue);
//            Log.e("GADJAMO ", "http://borne.io/tpt/api/vessels/all/?size=mr&sire=yes&tema_suitable=yes&age="+ageValue);
//        }

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




    // Set up back button as home
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
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
                    break;

                case 1:
                    Intent a = new Intent(this, Chartering.class);
                    startActivity(a);
                    break;

                case 2:
                    Intent b = new Intent(this, PostChartering.class);
                    startActivity(b);
                    break;

                case 3:
                    Intent c = new Intent(this, MarketReports.class);
                    startActivity(c);
                    break;

                case 4:
                    Intent d = new Intent(this, Settings.class);
                    startActivity(d);
                    break;

                case 5:
                    Intent e = new Intent(this, Contact.class);
                    startActivity(e);
                    break;


                default:
                    break;
            }
        }
    }


    // POST CHARTERI|NG
    private class HttpAsyncPositions extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(PositionsLr.this);
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

                    listDataHeader = new ArrayList<Ship>();
                    listDataChild = new HashMap<Ship, List<Ship>>();

//                    Log.e("groups", String.valueOf(groups));

// GETTING ALL POST CHARTERING
                    for(int i =0; i<groups.length(); i++){

                        JSONObject c = groups.getJSONObject(i);
                        Ship model = new Ship();

//                        Log.e("Objekat", groups.get(i).toString());
//                        Log.e("grups length", String.valueOf(groups.length()));

                        if(c.has("name")){
                            model.setName(c.getString("name"));
                        }

                        if(c.has("cbm")){
                            DecimalFormat df = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.US));
                            String resultStr = df.format(Double.valueOf(c.getString("cbm")));
                            model.setCbm(resultStr);
                        }

                        if(c.has("dwt")){
                            DecimalFormat df = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.US));
                            String resultStr = df.format(Double.valueOf(c.getString("dwt")));
                            model.setDwt(resultStr);
                        }

                        if(c.has("loa")){
                            double loaNumber = Double.valueOf(c.getString("loa"));
                            DecimalFormat df = new DecimalFormat("#.#");
                            String finalLoa = df.format(loaNumber);
                            model.setLoa(finalLoa+" M");                        }

                        if(c.has("last")){
                            if(c.getString("last").equals("")){
                                model.setLast("N/A");
                            }else{
                                model.setLast(c.getString("last"));
                            }
                        }

                        if(c.has("built")){
                            model.setBuilt(c.getString("built"));
                        }

                        if(c.has("location")){
                            model.setLocation(c.getString("location"));
                        }

                        if(c.has("open_date")){
                            model.setOpenDate(c.getString("open_date"));
                        }

                        if(c.has("status")){
                            model.setStatus(c.getString("status"));
                        }

                        if(c.has("comments")){
                            model.setComment(c.getString("comments"));
                        }

                        if(c.has("tema_suitable")){
                            model.setTemaSuitable(c.getString("tema_suitable"));
                        }

                        if(c.has("sire")){
                            model.setSire(c.getString("sire"));
                        }

                        if(c.has("last_update")){
                            model.setLastUpdate(c.getString("last_update"));
                        }


                        listDataHeader.add(model);

                        List<Ship> comingSoon = new ArrayList<Ship>();
                        comingSoon.add(model);

                        listDataChild.put(listDataHeader.get(i), comingSoon); // Header, Child data

//                        postChartsList.add(model);
                    }


                    list = (ExpandableListView) findViewById(R.id.listPositions);
                    adapterRec = new PositionsAdapter(getApplicationContext(), listDataHeader, listDataChild);
                    list.setAdapter(adapterRec);

//                    // Settin dynamically list height
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
                    final ViewGroup.LayoutParams params = list.getLayoutParams();
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
                    list = (ExpandableListView) findViewById(R.id.listPositions);
                    TextView listEm = (TextView) findViewById(R.id.listEmpty);
                    listEm.setText("There is no any Position yet.");
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
