package com.tpt.borne.tpt.contact;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.tpt.borne.tpt.adapters.ContactListAdapter;
import com.tpt.borne.tpt.adapters.DrawerAdapter;
import com.tpt.borne.tpt.chartering.Chartering;
import com.tpt.borne.tpt.login.Login;
import com.tpt.borne.tpt.mreports.MarketReports;
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
 * Created by Borne on 6/9/2016.
 */
public class Contact extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    ProgressDialog progDailog;
    String accessToken;

    public static  final  int PICK_CONTACT = 100;

    ContactListAdapter adapterRec;

    static final int READ_CONTACT = 1;
    static final int WRITE_CONTACT = 2;

    private DrawerLayout drawerLayout;
    private ListView lista;
    private ActionBarDrawerToggle drawerListener;
    DrawerAdapter adapter;

    RelativeLayout logOut;
    TextView userFullName;

    String name;
    String surname;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    String cNumber;
    String cNumber2;
    String cName;
    String cEmail;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;


    ArrayList<com.tpt.borne.tpt.models.Contact> contactList;

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }

        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        placeText = (TextView) findViewById(R.id.textPLace);
        placeholder.removeView(placeText);


        Statics.drawerIndicator = 5;


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
                Intent intent = new Intent(Contact.this, Login.class);
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




        new HttpAsyncGetExercises().execute("http://borne.io/tpt/api/user/team");

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
                    break;


                default:
                    break;
            }
        }
    }



    // GET EXEERCISES
    private class HttpAsyncGetExercises extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(Contact.this);
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
                    contactList = new ArrayList<>();


                    for(int i =0; i<groups.length(); i++){


                        JSONObject c = groups.getJSONObject(i);
                        com.tpt.borne.tpt.models.Contact model = new com.tpt.borne.tpt.models.Contact();

                        if(c.has("first_name")){
                            model.setFirstName(c.getString("first_name"));
                        }
                        if(c.has("last_name")){
                            model.setLastName(c.getString("last_name"));
                        }
                        if(c.has("email")){
                            model.setMail(c.getString("email"));
                        }

                        if(c.has("position")){
                            model.setPosition(c.getString("position"));
                        }
                        if(c.has("phone")){
                            model.setPhone(c.getString("phone"));
                        }
                        if(c.has("phone2")){
                            model.setPhone2(c.getString("phone2"));
                        }

                        if(c.has("position")){
                            model.setRole(c.getString("position"));
                        }

// Predict if there is any frree white spcae between words
                        if(c.has("profile_picture")){
                            String profPicture = c.getString("profile_picture");
                            if(profPicture.contains(" ")){
                                profPicture = profPicture.replace(" ", "%20");
                            }
                            model.setProfilePicture(profPicture);

                        }

                        contactList.add(model);
                    }


                        Log.e("CONTACT SAVED", groups.toString());


                    list = (ListView) findViewById(R.id.listContact);
                    adapterRec = new ContactListAdapter(getBaseContext(), R.layout.row_contact_list, contactList);
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

                            Vibrator vibe = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(50);

                            com.tpt.borne.tpt.models.Contact contact = (com.tpt.borne.tpt.models.Contact) parent.getItemAtPosition(position);

                            cNumber = contact.getPhone();
                            cName = contact.getFirstName()+" "+contact.getLastName();
                            cEmail = contact.getMail();
                            cNumber2 = contact.getPhone2();



                            if (ContextCompat.checkSelfPermission(Contact.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                               boolean b = contactExists(getBaseContext(), cNumber);
                                if(b == true){

                                    Log.e("POSTOJI U IMENIKU","POSTOJI U IMENIKU");

//                                    Intent callIntent = new Intent(Intent.ACTION_EDIT);
//                                    callIntent.setData(Uri.parse("tel:"+Uri.encode(cNumber.trim())));
//
//                                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(callIntent);

//                                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION,ContactsContract.Contacts.CONTENT_URI);
//                                    intent.putExtra(ContactsContract.Intents.Insert.NAME, cName);
//                                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, cNumber);
//                                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, cEmail);
//                                    startActivity(intent);
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cNumber));
                                    startActivity(intent);


                                }else{


                                    Log.e("NE POSTOJI U IMENIKU","NE POSTOJI U IMENIKU");

                                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION,ContactsContract.Contacts.CONTENT_URI);
                                    intent.putExtra(ContactsContract.Intents.Insert.NAME, cName);
                                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, cNumber);
                                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, cEmail);

                                    startActivity(intent);
                                }
                            } else {
                                Log.d("por", "Current app does not have READ_PHONE_STATE permission");
                                ActivityCompat.requestPermissions(Contact.this,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        READ_CONTACT);
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





    // permisisons granted for camera and write storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case READ_CONTACT: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("permissio cool", "Permission Granted");
                    //Proceed to next steps
                } else {
                    Log.e("permision not ok", "Permission Denied");
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    public boolean contactExists(Context context, String number) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
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
