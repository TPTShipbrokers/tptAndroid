package com.tpt.borne.tpt.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.tpt.borne.tpt.R;


public class Splash extends AppCompatActivity implements Animation.AnimationListener {

    private static int VREME = 1000;
    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // EMBEDD COLOR STATUS BAR 5.0 VERSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.backgroundColor));
        }


        ImageView back = (ImageView) findViewById(R.id.logosplash);

        new Handler().postDelayed(new Runnable(){
            public void run(){

                Intent i = new Intent(Splash.this, Login.class);
                startActivity(i);

                finish();

            }
        }, VREME);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


}
