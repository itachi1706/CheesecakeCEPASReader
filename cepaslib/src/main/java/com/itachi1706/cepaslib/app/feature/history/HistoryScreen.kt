/*
 * HistoryScreen.kt
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

@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package com.itachi1706.cepaslib.app.feature.history

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.view.Menu
import android.widget.Toast
import com.itachi1706.cepaslib.R
import com.itachi1706.cepaslib.app.core.activity.ActivityOperations
import com.itachi1706.cepaslib.app.core.inject.ScreenScope
import com.itachi1706.cepaslib.app.core.kotlin.Optional
import com.itachi1706.cepaslib.app.core.kotlin.filterAndGetOptional
import com.itachi1706.cepaslib.app.core.transit.TransitFactoryRegistry
import com.itachi1706.cepaslib.app.core.ui.ActionBarOptions
import com.itachi1706.cepaslib.app.core.ui.FareBotScreen
import com.itachi1706.cepaslib.app.core.util.ActionBarOptionsDefaults
import com.itachi1706.cepaslib.app.core.util.ErrorUtils
import com.itachi1706.cepaslib.app.core.util.ExportHelper
import com.itachi1706.cepaslib.app.feature.card.CardScreen
import com.itachi1706.cepaslib.app.feature.main.MainActivity
import com.itachi1706.cepaslib.card.serialize.CardSerializer
import com.itachi1706.cepaslib.persist.CardPersister
import com.itachi1706.cepaslib.persist.db.model.SavedCard
import com.itachi1706.cepaslib.transit.TransitIdentity
import com.uber.autodispose.kotlin.autoDisposable
import dagger.Component
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import javax.inject.Inject

class HistoryScreen : FareBotScreen<HistoryScreen.HistoryComponent, HistoryScreenView>(), HistoryScreenView.Listener {

    companion object {
        private const val REQUEST_SELECT_FILE = 1
        private const val REQUEST_SELECT_EXPORT_FILE = 2
        private const val FILENAME = "cepasreader-export.json"
    }

    @Inject lateinit var activityOperations: ActivityOperations
    @Inject lateinit var cardPersister: CardPersister
    @Inject lateinit var cardSerializer: CardSerializer
    @Inject lateinit var exportHelper: ExportHelper
    @Inject lateinit var transitFactoryRegistry: TransitFactoryRegistry

    override fun getTitle(context: Context): String = context.getString(R.string.history)

    override fun getActionBarOptions(): ActionBarOptions = ActionBarOptionsDefaults.getActionBarOptionsDefault()

    override fun onCreateView(context: Context): HistoryScreenView =
            HistoryScreenView(context, activityOperations, this)

    override fun onUpdateMenu(menu: Menu) {
        activity.menuInflater.inflate(R.menu.screen_history, menu)
    }

    override fun onShow(context: Context) {
        super.onShow(context)

        activityOperations.menuItemClick
                .autoDisposable(this)
                .subscribe { menuItem ->
                    val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    when (menuItem.itemId) {
                        R.id.import_file -> {
                            val target: Intent
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) target = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            else {
                                // Allowed deprecated code as it is for pre KitKat devices support (API <=19)
                                @Suppress("DEPRECATION") val storageUri = Uri.fromFile(Environment.getExternalStorageDirectory())
                                target = Intent(Intent.ACTION_GET_CONTENT)
                                target.putExtra(Intent.EXTRA_STREAM, storageUri)
                            }
                            target.type = "*/*"
                            activity.startActivityForResult(Intent.createChooser(target, activity.getString(R.string.select_file)), REQUEST_SELECT_FILE)
                        }
                        R.id.import_clipboard -> {
                            val importClip = clipboardManager.primaryClip
                            if (importClip != null && importClip.itemCount > 0) {
                                val text = importClip.getItemAt(0).coerceToText(activity).toString()
                                Single.fromCallable { exportHelper.importCards(text) }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .autoDisposable(this)
                                        .subscribe { cards -> onCardsImported(cards) }
                            }
                        }
                        R.id.copy -> {
                            val exportClip = ClipData.newPlainText(null, exportHelper.exportCards(context))
                            clipboardManager.setPrimaryClip(exportClip)
                            Toast.makeText(activity, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
                        }
                        R.id.share -> {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, exportHelper.exportCards(context))
                            activity.startActivity(intent)
                        }
                        R.id.save -> {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                Toast.makeText(activity, R.string.export_fail_android_version_too_old, Toast.LENGTH_SHORT).show()
                            } else {
                                val storageIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "text/json"
                                    putExtra(Intent.EXTRA_TITLE, FILENAME)
                                }
                                activity.startActivityForResult(storageIntent, REQUEST_SELECT_EXPORT_FILE)
                            }
                        }
                    }
                }

        activityOperations.activityResult
                .autoDisposable(this)
                .subscribe { (requestCode, resultCode, data) ->
                    when (requestCode) {
                        REQUEST_SELECT_FILE -> {
                            if (resultCode == Activity.RESULT_OK) {
                                data?.data?.let {
                                    importFromFile(it)
                                }
                            }
                        }
                        REQUEST_SELECT_EXPORT_FILE -> {
                            if (resultCode == Activity.RESULT_OK) {
                                data?.data?.let {
                                    exportToFileWithSAF(it)
                                }
                            }
                        }
                    }
                }

        loadCards()

        view.observeItemClicks()
                .autoDisposable(this)
                .subscribe { viewModel -> navigator.goTo(CardScreen(viewModel.rawCard)) }
    }

    override fun onDeleteSelectedItems(items: List<HistoryViewModel>) {
        DeleteCard(this).execute(items)
    }

    class DeleteCard(context: HistoryScreen) : AsyncTask<List<HistoryViewModel>, Void, Void>() {

        private val activityRef: WeakReference<HistoryScreen> = WeakReference(context)

        override fun doInBackground(vararg items: List<HistoryViewModel>): Void? {
            val activity = activityRef.get() ?: return null
            for ((savedCard) in items[0]) {
                activity.cardPersister.deleteCard(savedCard)
            }
            activity.loadCards()
            return null
        }
    }

    override fun createComponent(parentComponent: MainActivity.MainActivityComponent): HistoryComponent =
            DaggerHistoryScreen_HistoryComponent.builder()
            .mainActivityComponent(parentComponent)
            .build()

    override fun inject(component: HistoryComponent) {
        component.inject(this)
    }

    private fun loadCards() {
        observeCards()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(this)
                .subscribe(
                        { viewModels -> view.setViewModels(viewModels) },
                        { e -> ErrorUtils.showErrorToast(activity, e) })
    }

    private fun observeCards(): Single<List<HistoryViewModel>> {
        return Single.create<List<SavedCard>> { e ->
            try {
                e.onSuccess(cardPersister.cards)
            } catch (error: Throwable) {
                e.onError(error)
            }
        }.map { savedCards ->
            savedCards.map { savedCard ->
                val rawCard = cardSerializer.deserialize(savedCard.data)
                var transitIdentity: TransitIdentity? = null
                var parseException: Exception? = null
                try {
                    transitIdentity = transitFactoryRegistry.parseTransitIdentity(rawCard.parse())
                } catch (ex: Exception) {
                    parseException = ex
                }
                HistoryViewModel(savedCard, rawCard, transitIdentity, parseException)
            }
        }
    }

    private fun onCardsImported(cardIds: List<Long>) {
        loadCards()

        val text = activity.resources.getQuantityString(R.plurals.cards_imported, cardIds.size, cardIds.size)
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()

        if (cardIds.size == 1) {
            Single.create<Optional<SavedCard>> { e -> e.onSuccess(Optional(cardPersister.getCard(cardIds[0]))) }
                    .filterAndGetOptional()
                    .map { savedCard -> cardSerializer.deserialize(savedCard.data) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .autoDisposable(this)
                    .subscribe { rawCard -> navigator.goTo(CardScreen(rawCard)) }
        }
    }

    private fun exportToFileWithSAF(uri: Uri) {
        Single.fromCallable {
            activity?.contentResolver?.openOutputStream(uri)
                    ?.bufferedWriter()
                    .use { it?.write(exportHelper.exportCards(activity?.applicationContext)) }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(this)
                .subscribe({
                    Toast.makeText(activity, activity.getString(R.string.saved_to_x, getFileName(uri)), Toast.LENGTH_SHORT)
                            .show()
                }, { ex -> ErrorUtils.showErrorAlert(activity, ex) })
    }

    private fun getFileName(uri: Uri): String {
        val cursor: Cursor? = activity.contentResolver.query(uri, null, null, null, null, null)
        var name = FILENAME
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }


    private fun importFromFile(uri: Uri) {
        Single.fromCallable {
            val json = activity?.contentResolver?.openInputStream(uri)
                    ?.bufferedReader()
                    .use { it?.readText() }
            if (json == null) {
                Toast.makeText(activity, activity.getString(R.string.import_fail), Toast.LENGTH_SHORT).show()
                exportHelper.importCards("")
            }
            else exportHelper.importCards(json)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(this)
                .subscribe { cards -> onCardsImported(cards) }
    }

    @ScreenScope
    @Component(dependencies = [MainActivity.MainActivityComponent::class])
    interface HistoryComponent {
        fun inject(historyScreen: HistoryScreen)
    }
}
