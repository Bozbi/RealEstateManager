package com.sbizzera.real_estate_manager.provider

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import androidx.room.Room
import androidx.test.*
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sbizzera.real_estate_manager.data.api.AppLocalDatabase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("DEPRECATION")

@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTest {
    private lateinit var mContentResolver: ContentResolver
    private lateinit var db : AppLocalDatabase


    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppLocalDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mContentResolver = InstrumentationRegistry.getContext().contentResolver
    }

    @Test
    fun getPropertiesWhenNoItemInserted() {
        val cursor = mContentResolver.query(PropertyContentProvider.URI_PROPERTY, null, null, null, null)
        assertThat(cursor, notNullValue())
        assertThat(cursor?.count, `is`(0))
        cursor?.close()
    }

    @Test
    fun insertAndGetItem(){
        mContentResolver.insert(PropertyContentProvider.URI_PROPERTY, generateProperty())
        val cursor = mContentResolver.query(
            Uri.withAppendedPath(PropertyContentProvider.URI_PROPERTY, "testId"),
            null,
            null,
            null,
            null
        )
        assertThat(cursor, notNullValue())
        assertThat(cursor!!.count, `is`(1))
        assertThat(cursor.moveToFirst(), `is`(true))
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("propertyId")), `is`("testId"))
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("propertyTitle")), `is`("testTitle"))
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("propertyType")), `is`("House"))
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("price")), `is`(1000))
        db.propertyDao().deleteTables("testId")
    }

    private fun generateProperty(): ContentValues {
        return ContentValues().apply {
            put("propertyId", "testId")
            put("propertyTitle", "testTitle")
            put("propertyType", "House")
            put("price", 1000)
        }
    }
}