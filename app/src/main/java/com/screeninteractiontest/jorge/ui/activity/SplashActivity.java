package com.screeninteractiontest.jorge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.View;

import com.screeninteractiontest.jorge.R;

import java.util.concurrent.Executors;

public final class SplashActivity extends IcedActivity {

    private static final Long SPLASH_DURATION_MILLIS = 3000L;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        final Context appContext = getApplicationContext();

        /**
         * In a real setup I would probably use this screen to download some information (and of
         * course not using an anonymous AsyncTask for that) like the contacts, but from the
         * wireframe I understand that the download must happen in the list screen while I show a
         * circular loading progressbar, so I'm using this activity just as a dummy to show the
         * splash screen */
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try {
                    Thread.sleep(SPLASH_DURATION_MILLIS);
                } catch (final InterruptedException ex) {
                    ex.printStackTrace(System.err);
                    System.exit(-1);
                }
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                SplashActivity.this.start(appContext);
            }
        }.executeOnExecutor(Executors.newSingleThreadExecutor());

        return super.onCreateView(name, context, attrs);
    }

    private void start(final Context context) {
        final Intent intent = new Intent(context, MedarbetareActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.finishAfterTransition(this);
        //noinspection unchecked
        startActivity(intent);
    }
}
