package com.screeninteractiontest.jorge.io.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public final class PreferenceAssistant {

    public static final String PREF_SORT_TYPE = "PREF_SORT_TYPE";

    private PreferenceAssistant() {
        throw new UnsupportedOperationException("Do not instantiate " + getClass().getName());
    }

    public static void writeSharedInteger(final Context ctx, @NonNull final String settingName, final Integer settingValue) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(settingName, settingValue);
        editor.apply();
    }

    public static Integer readSharedInteger(final Context ctx, @NonNull final String settingName, final Integer defaultValue) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getInt(settingName, defaultValue);
    }
}
