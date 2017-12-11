package com.tpt.borne.tpt;

/**
 * Created by Borne on 6/20/2016.
 */

import android.app.Application;

import com.tpt.borne.tpt.parse.ParseUtils;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // register with parse
        ParseUtils.registerParse(this);


    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
}
