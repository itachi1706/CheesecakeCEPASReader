package com.itachi1706.cepaslib

import android.content.Context
import android.view.MenuItem

/**
 * Created by Kenneth on 13/6/2019.
 * for com.itachi1706.cepaslib in CheesecakeUtilities
 */
object CEPASLibBuilder {

    var prefClass: Class<*>? = null
    var menuHandler: LibMenuItemHandler? = null
    var showAbout: Boolean = false

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

    fun processMenuItemsFurther(menuItem: MenuItem, context: Context) {
        if (menuHandler == null) return
        menuHandler!!.handleMenuItem(menuItem, context)
    }
}
