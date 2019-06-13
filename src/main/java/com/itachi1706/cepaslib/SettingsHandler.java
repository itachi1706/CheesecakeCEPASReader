package com.itachi1706.cepaslib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

import com.codebutler.farebot.app.feature.bg.BackgroundTagActivity;
import com.codebutler.farebot.app.feature.prefs.FareBotPreferenceActivity;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

/**
 * Created by Kenneth on 21/2/2019.
 * for com.itachi1706.cepaslib in CheesecakeUtilities
 */
public class SettingsHandler {

    private Activity activity;

    public SettingsHandler(Activity activity) {
        this.activity = activity;
    }

    public static Intent launchSettings(Context context) {
        if (CEPASLibBuilder.INSTANCE.getPrefClass() != null)
            return new Intent(context, CEPASLibBuilder.INSTANCE.getPrefClass());
        Log.e("SettingsHandler", "No Preference Class defined. Using default preference. Initialize your own preference through CEPASLibBuilder");
        return FareBotPreferenceActivity.Companion.newIntent(context);
    }

    public void initSettings(final PreferenceFragment fragment) {
        fragment.addPreferencesFromResource(com.codebutler.farebot.R.xml.prefs);
        SwitchPreference mPreferenceLaunchFromBackground = (SwitchPreference) fragment.findPreference("pref_launch_from_background");
        mPreferenceLaunchFromBackground.setChecked(isLaunchFromBgEnabled());
        mPreferenceLaunchFromBackground.setOnPreferenceChangeListener((preference, newValue) -> {
            setLaunchFromBgEnabled((Boolean) newValue);
            return true;
        });
    }

    private boolean isLaunchFromBgEnabled() {
        ComponentName componentName = new ComponentName(activity, BackgroundTagActivity.class);
        PackageManager packageManager = activity.getPackageManager();
        int componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName);
        return componentEnabledSetting == COMPONENT_ENABLED_STATE_ENABLED;
    }

    private void setLaunchFromBgEnabled(boolean enabled) {
        ComponentName componentName = new ComponentName(activity, BackgroundTagActivity.class);
        PackageManager packageManager = activity.getPackageManager();
        int newState = enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
        packageManager.setComponentEnabledSetting(componentName, newState, PackageManager.DONT_KILL_APP);
    }
}