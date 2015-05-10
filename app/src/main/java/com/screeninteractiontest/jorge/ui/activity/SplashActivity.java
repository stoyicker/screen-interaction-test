package com.screeninteractiontest.jorge.ui.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.io.db.SQLiteDAO;
import com.screeninteractiontest.jorge.ui.activity.base.IcedActivity;

import java.util.concurrent.Executors;

import icepick.Icicle;

public final class SplashActivity extends IcedActivity {

    private static final Long SPLASH_DURATION_MILLIS = 2000L;
    @Icicle
    private Boolean isTimerStarted = Boolean.FALSE;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Context context = getApplicationContext();

        initDatabase(context);
    }

    private void initDatabase(Context context) {
        SQLiteDAO.setup(context);
    }

    @Override
    protected synchronized void onResume() {
        super.onResume();
        final Context appContext = getApplicationContext();

        /**
         * In a real setup I would probably use this screen to download some information (and of
         * course not using an anonymous AsyncTask for that) like the contacts, but from the
         * wireframe I understand that the download must happen in the list screen while I show a
         * circular loading progressbar, so I'm using this activity just as a dummy to show the
         * splash screen */
        if (!isTimerStarted) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    isTimerStarted = Boolean.TRUE;
                }

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
        }
    }

    private void start(final Context context) {
        final Intent intent = new Intent(context, ContactListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.finishAfterTransition(this);
        //noinspection unchecked
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
