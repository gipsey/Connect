package org.davidd.connect.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.model.User;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class PreferencesManager {

    private static final String MAIN_PREFERENCES = "MainPreferences";
    private static final String CURRENT_USER = "CurrentUser";

    private static PreferencesManager preferencesManager;

    private PreferencesManager() {
    }

    public static PreferencesManager instance() {
        if (preferencesManager == null) {
            preferencesManager = new PreferencesManager();
        }
        return preferencesManager;
    }

    @Nullable
    public User getUser() {
        SharedPreferences preferences = getMainPreferences();
        String userAsString = preferences.getString(CURRENT_USER, null);
        return createGsonWithExcludedFields().fromJson(userAsString, User.class);
    }

    public void saveUser(@NonNull User user) {
        SharedPreferences.Editor editor = getMainPreferences().edit();
        editor.putString(CURRENT_USER, createGsonWithExcludedFields().toJson(user));
        editor.apply();
    }

    public void clearUser() {
        getMainPreferences().edit().remove(CURRENT_USER).apply();
    }

    private SharedPreferences getMainPreferences() {
        return ConnectApp.instance().getApplicationContext().
                getSharedPreferences(MAIN_PREFERENCES, Context.MODE_PRIVATE);
    }
}
