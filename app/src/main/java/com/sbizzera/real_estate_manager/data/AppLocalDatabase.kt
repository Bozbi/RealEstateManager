package com.sbizzera.real_estate_manager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyDao
import com.sbizzera.real_estate_manager.data.utils.Converters
import com.sbizzera.real_estate_manager.utils.DATABASE_NAME


@Database(entities = [Property::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppLocalDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao

    companion object {
        private var instance: AppLocalDatabase? = null

        fun getInstance(context: Context): AppLocalDatabase {
            return instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): AppLocalDatabase {
            return Room.databaseBuilder(
                context,
                AppLocalDatabase::class.java,
                DATABASE_NAME
            )
                .build()
        }
    }
}