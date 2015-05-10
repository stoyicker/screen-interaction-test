package com.screeninteractiontest.jorge.ui.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import icepick.Icepick;

/**
 * Base class that generates boilerplate for state handling on configuration change.
 * The splash activity requires using a non-compat theme, which means that it needs a non-appcompat version of IcedAppCompatActivity.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public abstract class IcedActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
