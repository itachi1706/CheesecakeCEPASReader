/*
 * FareBotPreferenceActivity.kt
 *
 * This file is part of FareBot.
 * Learn more at: https://codebutler.github.io/farebot/
 *
 * Copyright (C) 2017 Eric Butler <eric@codebutler.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itachi1706.cepaslib.app.feature.prefs

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.feature.bg.BackgroundTagActivity

class FareBotPreferenceActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, FareBotPreferenceActivity::class.java)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, FareBotPreferenceFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class FareBotPreferenceFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceChangeListener {

        private lateinit var preferenceLaunchFromBackground: SwitchPreference
        private lateinit var preferenceDarkMode: ListPreference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.prefs, rootKey)

            setDarkMode(
                PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getString("pref_dark_mode", "default")
            )

            preferenceLaunchFromBackground = findPreference("pref_launch_from_background")!!
            preferenceLaunchFromBackground.isChecked = launchFromBgEnabled
            preferenceLaunchFromBackground.onPreferenceChangeListener = this

            preferenceDarkMode = findPreference("pref_dark_mode")!!
            preferenceDarkMode.onPreferenceChangeListener = this
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            if (preference === preferenceLaunchFromBackground) {
                launchFromBgEnabled = newValue as Boolean
                return true
            } else if (preference === preferenceDarkMode) {
                setDarkMode(newValue as String)
                return true
            }
            return false
        }

        private fun setDarkMode(newValue: String?) {
            when (newValue ?: "default") {
                "light" -> changeDarkModeTheme(AppCompatDelegate.MODE_NIGHT_NO, "Light")
                "dark" -> changeDarkModeTheme(AppCompatDelegate.MODE_NIGHT_YES, "Dark")
                else -> changeDarkModeTheme(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                    "default system"
                )
            }
        }

        private fun changeDarkModeTheme(newTheme: Int, themeName: String) {
            Log.i("AppThemeChanger", "Switching over to $themeName mode")
            AppCompatDelegate.setDefaultNightMode(newTheme)
        }

        private var launchFromBgEnabled: Boolean
            get() {
                val componentName =
                    ComponentName(requireContext(), BackgroundTagActivity::class.java)
                val componentEnabledSetting =
                    requireActivity().packageManager.getComponentEnabledSetting(componentName)
                return componentEnabledSetting == COMPONENT_ENABLED_STATE_ENABLED
            }
            set(enabled) {
                val componentName =
                    ComponentName(requireContext(), BackgroundTagActivity::class.java)
                val newState =
                    if (enabled) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED
                requireActivity().packageManager.setComponentEnabledSetting(
                    componentName,
                    newState,
                    PackageManager.DONT_KILL_APP
                )
            }
    }
}
