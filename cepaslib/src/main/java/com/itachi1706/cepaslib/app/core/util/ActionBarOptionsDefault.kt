package com.itachi1706.cepaslib.app.core.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.ColorRes
import com.itachi1706.cepaslib.CEPASLibBuilder
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.ui.ActionBarOptions

/**
 * Created by Kenneth on 17/6/2019.
 * for com.itachi1706.cepaslib.app.core.util in farebot_cepas
 */
class ActionBarOptionsDefaults {
    companion object {
        @JvmStatic
        fun getActionBarOptionsDefault(
            @ColorRes backgroundColor: Int = CEPASLibBuilder.titleBarColor,
            @ColorRes textColorRes: Int = R.color.white,
            shadow: Boolean = true
        ): ActionBarOptions {
            return ActionBarOptions(
                backgroundColorRes = backgroundColor,
                textColorRes = textColorRes,
                shadow = shadow
            )
        }

        @JvmStatic
        fun isNightModeEnabled(context: Context): Boolean {
            val currentNightMode =
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode != Configuration.UI_MODE_NIGHT_NO
        }
    }
}
