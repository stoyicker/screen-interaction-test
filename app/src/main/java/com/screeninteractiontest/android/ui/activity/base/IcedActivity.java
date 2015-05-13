package com.screeninteractiontest.android.ui.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import icepick.Icepick;

/**
 * Base class that generates boilerplate code for saving data upon configuration changes. The splash
 * activity shall use a non-compat theme, which means that it needs a non-appcompat version
 * of IcedAppCompatActivity.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public abstract class IcedActivity extends Activity {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSaveInstanceState(final @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
