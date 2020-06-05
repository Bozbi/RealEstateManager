package com.sbizzera.real_estate_manager.utils

import com.sbizzera.real_estate_manager.data.property.Property
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDateTime
import java.util.*

val FAKE_PROPERTIES = listOf(
    Property(
        UUID.randomUUID().toString(),
        "Belle maison à BelAir",
        "Maison",
        350000,
        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
    ),
    Property(
        UUID.randomUUID().toString(),
        "Appartement avec cachet",
        "Appartement",
        250000,
        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
    ),
    Property(
        UUID.randomUUID().toString(),
        "Terrain à batir vue imprenable",
        "Terrain",
        100000,
        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
    ),
    Property(
        UUID.randomUUID().toString(),
        "Loft à rénover",
        "Appartement",
        450000,
        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
    ),
    Property(
        UUID.randomUUID().toString(),
        "Maison de ville à Fétilly",
        "Maison",
        550000,
        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
    ),
    Property(
        UUID.randomUUID().toString(),
        "Appartement à saisir urgemment",
        "Appartement",
        225000,
        DateTimeUtils.toSqlTimestamp(LocalDateTime.now()).toString()
    )
)