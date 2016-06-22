package org.davidd.connect.component.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.davidd.connect.R;
import org.davidd.connect.manager.PreferencesManager;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getName();

    public static final boolean BOOT_DEFAULT = true;
    public static final boolean NOTIFICATION_DEFAULT = true;
    public static final boolean LOCATION_DEFAULT = true;

    public static final String BOOT_KEY = "boot";
    public static final String NOTIFICATION_KEY = "notification";
    public static final String LOCATION_KEY = "location";

    public static final String[] KEYS = new String[]{BOOT_KEY, NOTIFICATION_KEY, LOCATION_KEY};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);

        CheckBoxPreference bootPreference = (CheckBoxPreference) findPreference(BOOT_KEY);
        CheckBoxPreference notificationPreference = (CheckBoxPreference) findPreference(NOTIFICATION_KEY);
        CheckBoxPreference locationPreference = (CheckBoxPreference) findPreference(LOCATION_KEY);

        bootPreference.setChecked(PreferencesManager.instance().getSettingsValue(BOOT_KEY, BOOT_DEFAULT));
        notificationPreference.setChecked(PreferencesManager.instance().getSettingsValue(NOTIFICATION_KEY, NOTIFICATION_DEFAULT));
        locationPreference.setChecked(PreferencesManager.instance().getSettingsValue(LOCATION_KEY, LOCATION_DEFAULT));

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