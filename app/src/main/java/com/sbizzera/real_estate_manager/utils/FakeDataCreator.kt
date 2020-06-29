package com.sbizzera.real_estate_manager.utils

import com.sbizzera.real_estate_manager.data.photo.Photo
import com.sbizzera.real_estate_manager.data.property.Property
import com.sbizzera.real_estate_manager.ui.rem_activity.edit_property_fragment.EditPropertyUiModel
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDateTime
import java.util.*

val FAKE_UIPHOTO_LIST = mutableListOf(
    EditPropertyUiModel.PhotoUiModel("photoId0","Maison","https://images.unsplash.com/photo-1580587771525-78b9dba3b914?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1267&q=80"),
    EditPropertyUiModel.PhotoUiModel("photoID1","Cuisine","https://images.unsplash.com/photo-1556911220-bff31c812dba?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1435&q=80"),
    EditPropertyUiModel.PhotoUiModel("photoID2","Salle de bain","https://images.unsplash.com/photo-1521783593447-5702b9bfd267?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=821&q=80"),
    EditPropertyUiModel.PhotoUiModel("photoID3","LivingRoom","https://images.unsplash.com/photo-1554995207-c18c203602cb?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1350&q=80")
)

//val FAKE_PROPERTIES = listOf(
//    Property(
//        UUID.randomUUID().toString(),
//        "Belle maison à BelAir",
//        "Maison",
//        350000,
//        listOf(FAKE_PHOTO_LIST[0], FAKE_PHOTO_LIST[1]),
//        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
//    ),
//    Property(
//        UUID.randomUUID().toString(),
//        "Appartement avec cachet",
//        "Appartement",
//        250000,
//        listOf(FAKE_PHOTO_LIST[2], FAKE_PHOTO_LIST[3]),
//        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
//    ),
//    Property(
//        UUID.randomUUID().toString(),
//        "Terrain à batir vue imprenable",
//        "Terrain",
//        100000,
//        listOf(FAKE_PHOTO_LIST[2], FAKE_PHOTO_LIST[3]),
//        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
//    ),
//    Property(
//        UUID.randomUUID().toString(),
//        "Loft à rénover",
//        "Appartement",
//        450000,
//        listOf(FAKE_PHOTO_LIST[0], FAKE_PHOTO_LIST[1]),
//        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
//    ),
//    Property(
//        UUID.randomUUID().toString(),
//        "Maison de ville à Fétilly",
//        "Maison",
//        550000,
//        listOf(FAKE_PHOTO_LIST[0], FAKE_PHOTO_LIST[1]),
//        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
//    ),
//    Property(
//        UUID.randomUUID().toString(),
//        "Appartement à saisir urgemment",
//        "Appartement",
//        225000,
//        listOf(FAKE_PHOTO_LIST[2], FAKE_PHOTO_LIST[3]),
//        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
//    )
//)

