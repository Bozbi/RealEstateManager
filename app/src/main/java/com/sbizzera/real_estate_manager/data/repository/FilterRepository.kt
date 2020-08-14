package com.sbizzera.real_estate_manager.data.repository

import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.data.model.PointOfInterest

class FilterRepository private constructor() {
    companion object {
        val instance by lazy { FilterRepository() }
    }

    val filterLiveData = MutableLiveData(PropertyFilter())
}

data class PropertyFilter(
    val surfaceRange: IntRange? = null,
    val poiMap: Map<PointOfInterest, Boolean> = createPoiMap(),
    val createDateRange: LongRange? = null,
    val soldDateRange: LongRange? = null,
    val priceRange: IntRange? = null,
    val roomRange: IntRange? = null
)

private fun createPoiMap(): Map<PointOfInterest, Boolean> {
    val map = mutableMapOf<PointOfInterest, Boolean>()
    PointOfInterest.values().forEach {
        map[it] = false
    }
    return map
}

