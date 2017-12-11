package com.tpt.borne.tpt.settings;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.adapters.DrawerAdapter;
import com.tpt.borne.tpt.chartering.Chartering;
import com.tpt.borne.tpt.login.Login;
import com.tpt.borne.tpt.mreports.MarketReports;
import com.tpt.borne.tpt.positions.PositionsMr;
import com.tpt.borne.tpt.postchartering.PostChartering;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;


/**
 * Created by Borne on 6/3/2016.
 */
public class Settings extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ListView lista;
    private ActionBarDrawerToggle drawerListener;
    DrawerAdapter adapter;

    RelativeLayout logOut;
    TextView userFullName;


    String accessToken;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    RelativeLayout password;
    RelativeLayout prefered;
    RelativeLayout push;

    String name;
    String surname;

    TextView mailText;
    String mail;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }

        Statics.drawerIndicator = 4;
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



        sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        final String profilePicture = sharedpreferences.getString("profile_picture", "");
        accessToken = sharedpreferences.getString("accessToken", "");
        name = sharedpreferences.getString("firstName", "");
        surname = sharedpreferences.getString("lastName", "");
        mail = sharedpreferences.getString("userEmail", "");



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
                Intent intent = new Intent(Settings.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
                getBaseContext().startActivity(intent);
                Toast.makeText(getBaseContext(), "You have been Logged out", Toast.LENGTH_SHORT).show();
            }
        });


        userFullName = (TextView) findViewById(R.id.nameSurname);
        userFullName.setText(name+" "+surname);

        // DRAWER END


        mailText = (TextView) findViewById(R.id.mailtext);
        mailText.setText(mail);


        password = (RelativeLayout) findViewById(R.id.mailProfile);
        prefered = (RelativeLayout) findViewById(R.id.passwordProfile);
        push = (RelativeLayout) findViewById(R.id.telProfile);

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), Password.class);
                startActivity(i);
            }
        });

        prefered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SettingsPreferedPositions.class);
                startActivity(i);
            }
        });

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SettingsNotifications.class);
                startActivity(i);
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
    protected void onPause() {
        super.onPause();
        drawerLayout.closeDrawer(Gravity.LEFT);

        unregisterBroadcastReceiver();
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
                    break;

                case 5:
                    Intent e = new Intent(this, com.tpt.borne.tpt.contact.Contact.class);
                    startActivity(e);
                    finish();
                    break;


                default:
                    break;
            }
        }
    }


}
