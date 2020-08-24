package com.sbizzera.real_estate_manager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.sbizzera.real_estate_manager.data.api.AppLocalDatabase
import com.sbizzera.real_estate_manager.data.model.Property

class PropertyContentProvider : ContentProvider() {

    companion object {
        private val AUTHORITY = "com.sbizzera.real_estate_manager.provider"
        private val TABLE_NAME = "properties"
        val URI_PROPERTY = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }


    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        context?.let { context ->
            val propertyId = uri.lastPathSegment
            val cursor = AppLocalDatabase.getInstance(context).propertyDao().getPropertyWithCursor(propertyId!!)
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        }

        throw IllegalArgumentException("Failed to query properties $uri")
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        context?.let { context ->
            values?.let {contentValues->
                val insertResult =
                    AppLocalDatabase.getInstance(context).propertyDao().insertPropertyForTest(Property.fromContentValues(contentValues))
                if(insertResult != 0L){
                    return uri
                }
            }
        }
        throw IllegalArgumentException ("Failed to insert row into " + uri)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }
}
