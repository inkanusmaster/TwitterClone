package com.example.twitterclone;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseACL;

public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("59e9bd65c9b09064c7a4c497852caaf79f636262")
                .clientKey("9ef06bb019c6c76c1dafe53dac25bcaa32c1f4e2") //masterkey in .js file
                .server("http://18.130.9.67:80/parse/")
                .build()
        );

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}