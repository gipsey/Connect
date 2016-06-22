package org.davidd.connect.component.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.davidd.connect.R;
import org.davidd.connect.manager.PreferencesManager;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getName();

    private static final boolean BOOT_DEFAULT = true;
    private static final boolean NOTIFICATION_DEFAULT = true;
    private static final boolean LOCATION_DEFAULT = true;

    public static final String BOOT_KEY = "boot";
    public static final String NOTIFICATION_KEY = "notification";
    public static final String LOCATION_KEY = "location";

    public static final String[] KEYS = new String[]{BOOT_KEY, NOTIFICATION_KEY, LOCATION_KEY};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);

        Preference bootPreference = findPreference(BOOT_KEY);
        Preference notificationPreference = findPreference(NOTIFICATION_KEY);
        Preference locationPreference = findPreference(LOCATION_KEY);

//        bootPreference.setDefaultValue(PreferencesManager.instance().getSettingsValue(BOOT_KEY, BOOT_DEFAULT));
//        notificationPreference.setDefaultValue(PreferencesManager.instance().getSettingsValue(NOTIFICATION_KEY, NOTIFICATION_DEFAULT));
//        locationPreference.setDefaultValue(PreferencesManager.instance().getSettingsValue(LOCATION_KEY, LOCATION_DEFAULT));

        bootPreference.setDefaultValue(true);
        notificationPreference.setDefaultValue(true);
        locationPreference.setDefaultValue(true);

        bootPreference.setOnPreferenceChangeListener(this);
        notificationPreference.setOnPreferenceChangeListener(this);
        locationPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case BOOT_KEY:
                PreferencesManager.instance().setSettingsValue(BOOT_KEY, (Boolean) newValue);
                return true;
            case NOTIFICATION_KEY:
                PreferencesManager.instance().setSettingsValue(NOTIFICATION_KEY, (Boolean) newValue);
                return true;
            case LOCATION_KEY:
                PreferencesManager.instance().setSettingsValue(LOCATION_KEY, (Boolean) newValue);
                return true;
        }

        return false;
    }
}