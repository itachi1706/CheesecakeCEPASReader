package com.itachi1706.cepaslib

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.util.Log
import com.codebutler.farebot.app.feature.bg.BackgroundTagActivity
import com.codebutler.farebot.app.feature.prefs.FareBotPreferenceActivity

/**
 * Created by Kenneth on 21/2/2019.
 * for com.itachi1706.cepaslib in CheesecakeUtilities
 */
class SettingsHandler(private val activity: Activity) {

    private var isLaunchFromBgEnabled: Boolean
        get() {
            val componentName = ComponentName(activity, BackgroundTagActivity::class.java)
            val packageManager = activity.packageManager
            val componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName)
            return componentEnabledSetting == COMPONENT_ENABLED_STATE_ENABLED
        }
        set(enabled) {
            val componentName = ComponentName(activity, BackgroundTagActivity::class.java)
            val packageManager = activity.packageManager
            val newState = if (enabled) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED
            packageManager.setComponentEnabledSetting(componentName, newState, PackageManager.DONT_KILL_APP)
        }

    fun initSettings(fragment: PreferenceFragment) {
        fragment.addPreferencesFromResource(com.codebutler.farebot.R.xml.prefs)
        val mPreferenceLaunchFromBackground = fragment.findPreference("pref_launch_from_background") as SwitchPreference
        mPreferenceLaunchFromBackground.isChecked = isLaunchFromBgEnabled
        mPreferenceLaunchFromBackground.setOnPreferenceChangeListener { _, newValue ->
            isLaunchFromBgEnabled = newValue as Boolean
            true
        }
        // Hide dark mode toggle (we will use the main thing instead)
        (fragment.preferenceManager.findPreference("cepas_cat") as PreferenceCategory).removePreference(fragment.findPreference("pref_dark_mode"))
    }

    companion object {

        fun launchSettings(context: Context): Intent {
            if (CEPASLibBuilder.prefClass != null)
                return Intent(context, CEPASLibBuilder.prefClass)
            Log.e("SettingsHandler", "No Preference Class defined. Using default preference. Initialize your own preference through CEPASLibBuilder")
            return FareBotPreferenceActivity.newIntent(context)
        }
    }
}