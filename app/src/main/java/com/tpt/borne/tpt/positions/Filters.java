package com.tpt.borne.tpt.positions;

import android.app.Activity;
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
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.adapters.DrawerAdapter;
import com.tpt.borne.tpt.chartering.Chartering;
import com.tpt.borne.tpt.login.Login;
import com.tpt.borne.tpt.mreports.MarketReports;
import com.tpt.borne.tpt.postchartering.PostChartering;
import com.tpt.borne.tpt.settings.Settings;
import com.tpt.borne.tpt.statics.NetworkChangeReceiver;
import com.tpt.borne.tpt.statics.Statics;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tpt.borne.tpt.contact.Contact;

import java.util.ArrayList;

/**
 * Created by Borne on 6/17/2016.
 */
public class Filters extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String accessToken;

    private DrawerLayout drawerLayout;
    private ListView lista;
    private ActionBarDrawerToggle drawerListener;
    DrawerAdapter adapter;

    FrameLayout placeholder;
    TextView placeText;
    NetworkChangeReceiver receiver;

    String name;
    String surname;
    RelativeLayout logOut;
    TextView userFullName;
    RelativeLayout reset;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    String from;

    public static final int BACK_CODE = 888;

    boolean resetFilters = false;

    ImageView locationIcon;

//SIRE
    RelativeLayout sire;
    boolean shireClicked = false;
    String shireValue;


// TEMA SUITABLE
    RelativeLayout temaSuitable;
    boolean isTemaSuitable = false;
    String temaValue;


//SAVE BUITT
    RelativeLayout saveButt;


// YEARS GROUP
    RelativeLayout check5;
    RelativeLayout check10;
    RelativeLayout check15;
    RelativeLayout check20;
    RelativeLayout check25;
    RelativeLayout check30;

    ImageView img5;
    ImageView img10;
    ImageView img15;
    ImageView img20;
    ImageView img25;
    ImageView img30;


// GRADE SETTINGS
    RelativeLayout cpp;
    RelativeLayout dpp;

    ImageView cppImg;
    ImageView dppImg;


    ArrayList<RelativeLayout> relativeViews;
    ArrayList<RelativeLayout> gradeViews;
    ArrayList<ImageView> imageViews;
    ArrayList<ImageView> gradeImageViews;

    boolean groupClicked = false;
    boolean gradeClicked = false;
    String ageValue;
    String gradeValue;

    String locationValue;

    ImageView passwordImage;
    ImageView passwordImage1;


    RelativeLayout setLocationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_filters);


        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blueMain));
        }


        Statics.drawerIndicator = 0;

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



        locationIcon = (ImageView) findViewById(R.id.locationIcon);

// Getting from clicke
        Intent i = getIntent();
        from = i.getStringExtra("from");


        SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
        accessToken = sharedpreferences.getString("accessToken", "");
        final String profilePicture = sharedpreferences.getString("profile_picture", "");
        name = sharedpreferences.getString("firstName", "");
        surname = sharedpreferences.getString("lastName", "");
        locationValue = sharedpreferences.getString("locationValue", "");

        if(locationValue.equals("")){
            locationIcon.setVisibility(View.INVISIBLE);
        }

        temaValue = sharedpreferences.getString("temaValue", "");
        shireValue = sharedpreferences.getString("shireValue", "");
        ageValue = sharedpreferences.getString("ageValue", "");
        gradeValue = sharedpreferences.getString("gradeValue", "");

        Log.e("GRDAE VALU", gradeValue);



        setLocationLayout = (RelativeLayout) findViewById(R.id.setLocationLay);
        setLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), Locations.class);
                startActivityForResult(i, BACK_CODE);
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
                Intent intent = new Intent(Filters.this, Login.class);
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


// SIRE
        reset = (RelativeLayout) findViewById(R.id.linearLayout4);

        sire = (RelativeLayout) findViewById(R.id.passwordProfile);

        if(shireValue.equals("yes")){
            passwordImage1 = (ImageView) findViewById(R.id.passwordImg);
            passwordImage1.setImageResource(R.drawable.check_box_checked);
            shireClicked = true;
        }else{
            passwordImage1 = (ImageView) findViewById(R.id.passwordImg);
            passwordImage1.setImageResource(R.drawable.check_box_empty);
        }

        sire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(shireClicked == false){
                    passwordImage1 = (ImageView) findViewById(R.id.passwordImg);
                    passwordImage1.setImageResource(R.drawable.check_box_checked);
                    shireClicked = true;
                    shireValue = "yes";
                }else{
                    passwordImage1 = (ImageView) findViewById(R.id.passwordImg);
                    passwordImage1.setImageResource(R.drawable.check_box_empty);
                    shireClicked = false;
                    shireValue="";
                }



            }
        });


//TEMA SUITABLE
        temaSuitable = (RelativeLayout) findViewById(R.id.temaSuitable);

        if(temaValue.equals("yes")){
            passwordImage = (ImageView) findViewById(R.id.notifiactionsDesc);
            passwordImage.setImageResource(R.drawable.check_box_checked);
            isTemaSuitable = true;
        }else{
            passwordImage = (ImageView) findViewById(R.id.notifiactionsDesc);
            passwordImage.setImageResource(R.drawable.check_box_empty);
        }

        temaSuitable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTemaSuitable == false){
                    passwordImage = (ImageView) findViewById(R.id.notifiactionsDesc);
                    passwordImage.setImageResource(R.drawable.check_box_checked);
                    isTemaSuitable = true;
                    temaValue = "yes";
                }else{
                    passwordImage = (ImageView) findViewById(R.id.notifiactionsDesc);
                    passwordImage.setImageResource(R.drawable.check_box_empty);
                    isTemaSuitable = false;
                    temaValue="";
                }
            }
        });


// GRADE SELECTION
        cpp = (RelativeLayout) findViewById(R.id.conditionCpp);
        dpp = (RelativeLayout) findViewById(R.id.conditionDpp);

        cpp.setTag("cpp");
        dpp.setTag("dpp");

        gradeViews = new ArrayList<>();
        gradeViews.add(cpp);
        gradeViews.add(dpp);

        cppImg = (ImageView) findViewById(R.id.conditionCppImg);
        dppImg = (ImageView) findViewById(R.id.conditionDppImg);

        if(gradeValue.equals("cpp")){
            cppImg.setImageResource(R.drawable.check_box_checked);
            gradeClicked = true;
        }else if (gradeValue.equals("dpp")){
            dppImg.setImageResource(R.drawable.check_box_checked);
            gradeClicked = true;
        }


        gradeImageViews = new ArrayList<>();
        gradeImageViews.add(cppImg);
        gradeImageViews.add(dppImg);


// Checking if it is selected
        for(int  j = 0; j<gradeViews.size(); j++){

            final int finalJ = j;
            gradeViews.get(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(gradeClicked == true){
                        for(int k =0; k<gradeImageViews.size(); k++){
                            gradeImageViews.get(k).setImageResource(R.drawable.check_box_empty);
                        }

                        gradeImageViews.get(finalJ).setImageResource(R.drawable.check_box_checked);
                        gradeClicked = true;
                        gradeValue = String.valueOf(gradeViews.get(finalJ).getTag());

                    }else{
                        gradeImageViews.get(finalJ).setImageResource(R.drawable.check_box_checked);
                        gradeClicked = true;
                        gradeValue = String.valueOf(gradeViews.get(finalJ).getTag());
                    }
                }
            });

        }




// AGE GROUP
        check5 = (RelativeLayout) findViewById(R.id.rel5);
        check10 = (RelativeLayout) findViewById(R.id.rel10);
        check15 = (RelativeLayout) findViewById(R.id.rel15);
        check20 = (RelativeLayout) findViewById(R.id.rel20);
        check25 = (RelativeLayout) findViewById(R.id.rel25);
        check30 = (RelativeLayout) findViewById(R.id.rel30);

        check5.setTag(5);
        check10.setTag(10);
        check15.setTag(15);
        check20.setTag(20);
        check25.setTag(25);
        check30.setTag(30);


        relativeViews = new ArrayList<>();
        relativeViews.add(check5);
        relativeViews.add(check10);
        relativeViews.add(check15);
        relativeViews.add(check20);
        relativeViews.add(check25);
        relativeViews.add(check30);


        img5 = (ImageView) findViewById(R.id.checkBox5);
        img10 = (ImageView) findViewById(R.id.checkBox10);
        img15 = (ImageView) findViewById(R.id.checkBox15);
        img20 = (ImageView) findViewById(R.id.checkBox20);
        img25 = (ImageView) findViewById(R.id.checkBox25);
        img30 = (ImageView) findViewById(R.id.checkBox30);

        if(ageValue.equals("5")){
            img5.setImageResource(R.drawable.check_box_checked);
            groupClicked = true;
        }else if(ageValue.equals("10")){
            img10.setImageResource(R.drawable.check_box_checked);
            groupClicked = true;
        }else if(ageValue.equals("15")){
            img15.setImageResource(R.drawable.check_box_checked);
            groupClicked = true;
        }else if(ageValue.equals("20")){
            img20.setImageResource(R.drawable.check_box_checked);
            groupClicked = true;
        }else if(ageValue.equals("25")){
            img25.setImageResource(R.drawable.check_box_checked);
            groupClicked = true;
        }else if(ageValue.equals("30")){
            img30.setImageResource(R.drawable.check_box_checked);
            groupClicked = true;
        }

        imageViews = new ArrayList<>();
        imageViews.add(img5);
        imageViews.add(img10);
        imageViews.add(img15);
        imageViews.add(img20);
        imageViews.add(img25);
        imageViews.add(img30);



        for(int  j = 0; j<relativeViews.size(); j++){

            final int finalJ = j;
            relativeViews.get(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(groupClicked == true){
                        for(int k =0; k<imageViews.size(); k++){
                            imageViews.get(k).setImageResource(R.drawable.check_box_empty);
                        }

                        imageViews.get(finalJ).setImageResource(R.drawable.check_box_checked);
                        groupClicked = true;
                        ageValue = String.valueOf(relativeViews.get(finalJ).getTag());

                    }else{
                        imageViews.get(finalJ).setImageResource(R.drawable.check_box_checked);
                        groupClicked = true;
                        ageValue = String.valueOf(relativeViews.get(finalJ).getTag());
                    }
                }
            });


        }


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int  j = 0; j<relativeViews.size(); j++) {
                    imageViews.get(j).setImageResource(R.drawable.check_box_empty);
                }

                for(int k = 0; k<gradeImageViews.size(); k++){
                    gradeImageViews.get(k).setImageResource(R.drawable.check_box_empty);
                }

                groupClicked = false;
                gradeClicked = false;
                temaValue = "";
                shireValue = "";
                locationValue = "";
                SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.putString("locationValue", "");
                editor.commit();
                passwordImage.setImageResource(R.drawable.check_box_empty);
                passwordImage1.setImageResource(R.drawable.check_box_empty);
                gradeValue = "";
                ageValue = "";

                locationIcon.setVisibility(View.INVISIBLE);

            }
        });




// SAVE SETTTINGS
        saveButt = (RelativeLayout) findViewById(R.id.linearLayout3);
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting password i shared preferences
                if(temaValue.equals("") && shireValue.equals("") && groupClicked == false && locationValue.equals("") && gradeClicked == false){
                    resetFilters = true;
                }else{
                    resetFilters = false;
                }


                SharedPreferences sharedpreferences = getSharedPreferences("com.borne.tpt", Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                if(resetFilters == true){
                    editor.putString("temaValue", "");
                    editor.putString("shireValue", "");
                    editor.putString("ageValue", "");
                    editor.putString("gradeValue", "");
                    editor.putString("filtersYes", "false");
                    editor.putString("locationValue", "");
                    editor.commit();
                }else {
                    editor.putString("temaValue", temaValue);
                    editor.putString("shireValue", shireValue);
                    editor.putString("ageValue", ageValue);
                    editor.putString("gradeValue", gradeValue);
                    editor.putString("filtersYes", "true");
                    editor.putString("locationValue", locationValue);
                    editor.commit();
                }

                if(from.equals("small")){
                Intent i = new Intent(getBaseContext(), PositionsSmall.class);
                startActivity(i);
                finish();
                }else if(from.equals("mr")){
                    Intent i = new Intent(getBaseContext(), PositionsMr.class);
                    startActivity(i);
                    finish();
                }else if(from.equals("lr")){
                    Intent i = new Intent(getBaseContext(), PositionsLr.class);
                    startActivity(i);
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
                    Intent e = new Intent(this, Contact.class);
                    startActivity(e);
                    finish();
                    break;


                default:
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BACK_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                locationValue = data.getStringExtra("locationValue");
                locationIcon.setVisibility(View.VISIBLE);
            }

            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }


}
