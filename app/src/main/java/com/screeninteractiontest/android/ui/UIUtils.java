package com.screeninteractiontest.android.ui;

import android.os.Looper;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public class UIUtils {

    public static Boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
