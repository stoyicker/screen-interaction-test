package com.screeninteractiontest.android.ui;

import android.os.Looper;

/**
 * General UI-related utilities.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public class UIUtils {

    /**
     * Checks if the caller thread is the UI thread.
     *
     * @return {@link Boolean} <value>Boolean.TRUE</value> if the caller and the UI threads are the same; <value>Boolean.FALSE</value> otherwise
     */
    public static Boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
