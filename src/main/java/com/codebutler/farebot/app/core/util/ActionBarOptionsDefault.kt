package com.codebutler.farebot.app.core.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.ColorRes
import com.codebutler.farebot.R
import com.codebutler.farebot.app.core.ui.ActionBarOptions
import com.itachi1706.cepaslib.CEPASLibBuilder

/**
 * Created by Kenneth on 17/6/2019.
 * for com.codebutler.farebot.app.core.util in farebot_cepas
 */
class ActionBarOptionsDefaults {
    companion object {
        @JvmStatic
        fun getActionBarOptionsDefault(@ColorRes backgroundColor: Int = CEPASLibBuilder.titleBarColor,
                                       @ColorRes textColorRes: Int = R.color.white,
                                       shadow: Boolean = true): ActionBarOptions {
            return ActionBarOptions(
                    backgroundColorRes = backgroundColor,
                    textColorRes = textColorRes,
                    shadow = shadow
            )
        }

        @JvmStatic
        fun isNightModeEnabled(context: Context): Boolean {
            val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode != Configuration.UI_MODE_NIGHT_NO
        }
    }
}
