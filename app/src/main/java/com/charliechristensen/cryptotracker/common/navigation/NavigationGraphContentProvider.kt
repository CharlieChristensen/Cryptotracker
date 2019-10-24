package com.charliechristensen.cryptotracker.common.navigation

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.annotation.NavigationRes
import com.charliechristensen.cryptotracker.MainApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
abstract class NavigationGraphContentProvider: ContentProvider() {

    @NavigationRes
    abstract fun getNavigationGraph(): Int

    override fun onCreate(): Boolean {
        (context as MainApplication).appComponent.navGraphHolder().addGraph(getNavigationGraph())
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String? = null

}
