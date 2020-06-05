package com.sbizzera.real_estate_manager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.photo.PhotoDao
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.data.property.PropertyDao
import com.sbizzera.real_estate_manager.data.property_register.PropertyRegister
import com.sbizzera.real_estate_manager.data.property_register.PropertyRegisterDao
import com.sbizzera.real_estate_manager.data.property_register.PropertyRegisterRow
import com.sbizzera.real_estate_manager.utils.DATABASE_NAME


@Database(entities = [Property::class,PropertyRegisterRow::class,Photo::class], version = 1, exportSchema = false)
abstract class AppLocalDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun propertyRegisterDao(): PropertyRegisterDao
    abstract fun photoDao():PhotoDao

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