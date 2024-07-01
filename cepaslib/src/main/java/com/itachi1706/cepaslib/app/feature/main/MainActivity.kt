/*
 * MainActivity.kt
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

package com.itachi1706.cepaslib.app.feature.main

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import com.itachi1706.cepaslib.CEPASLibBuilder
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.activity.ActivityOperations
import com.itachi1706.cepaslib.app.core.activity.ActivityResult
import com.itachi1706.cepaslib.app.core.activity.RequestPermissionsResult
import com.itachi1706.cepaslib.app.core.app.FareBotApplication
import com.itachi1706.cepaslib.app.core.app.FareBotApplicationComponent
import com.itachi1706.cepaslib.app.core.inject.ActivityScope
import com.itachi1706.cepaslib.app.core.kotlin.adjustAlpha
import com.itachi1706.cepaslib.app.core.kotlin.bindView
import com.itachi1706.cepaslib.app.core.kotlin.getColor
import com.itachi1706.cepaslib.app.core.nfc.NfcStream
import com.itachi1706.cepaslib.app.core.nfc.TagReaderFactory
import com.itachi1706.cepaslib.app.core.serialize.CardKeysSerializer
import com.itachi1706.cepaslib.app.core.transit.TransitFactoryRegistry
import com.itachi1706.cepaslib.app.core.ui.FareBotCrossfadeTransition
import com.itachi1706.cepaslib.app.core.ui.FareBotScreen
import com.itachi1706.cepaslib.app.core.util.ExportHelper
import com.itachi1706.cepaslib.app.feature.home.CardStream
import com.itachi1706.cepaslib.app.feature.home.HomeScreen
import com.itachi1706.cepaslib.card.serialize.CardSerializer
import com.itachi1706.cepaslib.persist.CardKeysPersister
import com.itachi1706.cepaslib.persist.CardPersister
import com.jakewharton.rxrelay3.PublishRelay
import com.wealthfront.magellan.*
import com.wealthfront.magellan.navigation.NavigationListener
import com.wealthfront.magellan.navigation.Navigator
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    ScreenLifecycleListener,
    NavigationListener {

    @Inject
    internal lateinit var navigator: Navigator
    @Inject
    internal lateinit var nfcStream: NfcStream

    private val appBarLayout by bindView<AppBarLayout>(R.id.appBarLayout)
    private val toolbar by bindView<Toolbar>(R.id.toolbar)

    private val activityResultRelay = PublishRelay.create<ActivityResult>()
    private val handler = Handler(Looper.getMainLooper())
    private val menuItemClickRelay = PublishRelay.create<MenuItem>()
    private val permissionsResultRelay = PublishRelay.create<RequestPermissionsResult>()

    private val shortAnimationDuration: Long by lazy {
        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    }

    private val toolbarElevation: Float by lazy {
        resources.getDimensionPixelSize(R.dimen.toolbar_elevation).toFloat()
    }

    private var animToolbarBg: ObjectAnimator? = null

    val component: MainActivityComponent by lazy {
        DaggerMainActivity_MainActivityComponent.builder()
            .applicationComponent((application as FareBotApplication).component)
            .activity(this)
            .mainActivityModule(MainActivityModule())
            .activityOperations(
                ActivityOperations(
                    this,
                    activityResultRelay.hide(),
                    menuItemClickRelay.hide(),
                    permissionsResultRelay.hide()
                )
            )
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        component.inject(this)
        navigator.addLifecycleListener(this)
        nfcStream.onCreate(this, savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navigator.onCreate(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigator.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        navigator.onResume(this)
        nfcStream.onResume()
    }

    override fun onPause() {
        super.onPause()
        navigator.onPause(this)
        nfcStream.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.removeLifecycleListener(this)
        navigator.onDestroy(this)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!navigator.handleBack()) {
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        navigator.onCreateOptionsMenu(menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        navigator.onPrepareOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        menuItemClickRelay.accept(item)
        return true
    }

    override fun onNavigate(actionBarConfig: ActionBarConfig) {
        toolbar.visibility = if (actionBarConfig.visible()) View.VISIBLE else View.GONE
    }

    @SuppressLint("ResourceType") // Lint bug?
    override fun onShow(screen: Screen<*>) {
        val options = (screen as FareBotScreen<*, *>).getActionBarOptions()

        supportActionBar?.setDisplayHomeAsUpEnabled(!navigator.atRoot())

        if (CEPASLibBuilder.homeScreenWithBackButton && navigator.atRoot()) supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(
                true
            )
        }

        toolbar.setTitleTextColor(getColor(options.textColorRes, Color.BLACK))
        toolbar.title = screen.getTitle(this)
        toolbar.subtitle = null

        val newColor = getColor(options.backgroundColorRes, Color.TRANSPARENT)
        val curColor = (toolbar.background as? ColorDrawable)?.color ?: Color.TRANSPARENT

        val curColorForAnim = if (curColor == Color.TRANSPARENT) adjustAlpha(newColor) else curColor
        val newColorForAnim = if (newColor == Color.TRANSPARENT) adjustAlpha(curColor) else newColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animToolbarBg?.cancel()
            animToolbarBg =
                ObjectAnimator.ofArgb(toolbar, "backgroundColor", curColorForAnim, newColorForAnim)
                    .apply {
                        duration = shortAnimationDuration
                        start()
                    }
        }

        ViewCompat.setElevation(appBarLayout, if (options.shadow) toolbarElevation else 0f)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsResultRelay.accept(
            RequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        )
    }

    override fun onHide(screen: Screen<*>?) {
        // No-Op
    }

    @Module
    class MainActivityModule {

        @Provides
        @ActivityScope
        fun provideNfcTagStream(activity: MainActivity): NfcStream = NfcStream(activity)

        @Provides
        @ActivityScope
        fun provideCardStream(
            application: FareBotApplication,
            cardPersister: CardPersister,
            cardSerializer: CardSerializer,
            nfcStream: NfcStream,
            tagReaderFactory: TagReaderFactory
        ): CardStream {
            return CardStream(
                application,
                cardPersister,
                cardSerializer,
                nfcStream,
                tagReaderFactory
            )
        }

        @Provides
        @ActivityScope
        fun provideNavigator(activity: MainActivity): Navigator = Navigator.withRoot(HomeScreen())
            .transition(FareBotCrossfadeTransition(activity))
            .build()
    }

    @ActivityScope
    @Component(
        dependencies = [FareBotApplicationComponent::class],
        modules = [MainActivityModule::class]
    )
    interface MainActivityComponent {

        fun activityOperations(): ActivityOperations

        fun application(): FareBotApplication

        fun cardPersister(): CardPersister

        fun cardSerializer(): CardSerializer

        fun cardKeysPersister(): CardKeysPersister

        fun cardKeysSerializer(): CardKeysSerializer

        fun cardStream(): CardStream

        fun exportHelper(): ExportHelper

        fun nfcStream(): NfcStream

        fun sharedPreferences(): SharedPreferences

        fun tagReaderFactory(): TagReaderFactory

        fun transitFactoryRegistry(): TransitFactoryRegistry

        fun inject(mainActivity: MainActivity)

        @Component.Builder
        interface Builder {

            fun applicationComponent(applicationComponent: FareBotApplicationComponent): Builder

            fun mainActivityModule(mainActivityModule: MainActivityModule): Builder

            @BindsInstance
            fun activity(activity: MainActivity): Builder

            @BindsInstance
            fun activityOperations(activityOperations: ActivityOperations): Builder

            fun build(): MainActivityComponent
        }
    }
}
