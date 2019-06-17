package com.itachi1706.cepaslib

import android.content.Context
import android.view.MenuItem
import androidx.annotation.ColorRes
import com.codebutler.farebot.R

/**
 * Created by Kenneth on 13/6/2019.
 * for com.itachi1706.cepaslib in CheesecakeUtilities
 */
object CEPASLibBuilder {

    var prefClass: Class<*>? = null
    var menuHandler: LibMenuItemHandler? = null
    var showAbout: Boolean = false
    @ColorRes var titleBarColor: Int = R.color.accent
    @ColorRes var accentColor: Int = R.color.accent
    @ColorRes var textColor: Int = R.color.white
    var customTitle: String? = null
    var homeScreenWithBackButton: Boolean = false

    var customTitleBarColor = false
    var customAccentColor = false

    fun setPreferenceClass(preferenceClass: Class<*>) {
        prefClass = preferenceClass
    }

    fun registerMenuHandler(menu: LibMenuItemHandler) {
        menuHandler = menu
    }

    fun unregisterMenuHandler() {
        menuHandler = null
    }

    fun shouldShowAboutMenuItem(should: Boolean) {
        showAbout = should
    }

    fun updateTitleBarColor(@ColorRes newTitleBarColor: Int) {
        titleBarColor = newTitleBarColor
        customTitleBarColor = true
    }

    fun updateAccentColor(@ColorRes newAccentColor: Int) {
        accentColor = newAccentColor
        customAccentColor = true
    }

    fun updateTextColor(@ColorRes newTextColor: Int) {
        textColor = newTextColor
    }

    fun processMenuItemsFurther(menuItem: MenuItem, context: Context) {
        if (menuHandler == null) return
        menuHandler!!.handleMenuItem(menuItem, context)
    }
}
