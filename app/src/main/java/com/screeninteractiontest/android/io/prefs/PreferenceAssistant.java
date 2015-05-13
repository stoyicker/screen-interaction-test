package com.screeninteractiontest.android.io.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Used to ease preference handling.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public abstract class PreferenceAssistant {

    public static final String PREF_SORT_MODE = "PREF_SORT_MODE";

    /**
     * Writes an Integer preference. It is overwritten if it already exists.
     *
     * @param ctx          {@link Context} Context
     * @param settingName  {@link String} The name of the preference
     * @param settingValue {@link Integer} The desired value for the preference
     */
    public static void writeSharedInteger(final Context ctx, @NonNull final String settingName, final Integer settingValue) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(settingName, settingValue);
        editor.apply();
    }

    /**
     * Reads an Integer preference.
     *
     * @param ctx          {@link Context} Context
     * @param settingName  {@link String} The name of the preference
     * @param defaultValue {@link Integer} The default value to return if the preference is not
     *                     found
     * @return {@link Integer} The value for the preference, or the default one provided if the
     * preference is not found
     */
    public static Integer readSharedInteger(final Context ctx, @NonNull final String settingName, final Integer defaultValue) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getInt(settingName, defaultValue);
    }
}
