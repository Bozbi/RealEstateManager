package com.sbizzera.real_estate_manager.ui.rem_activity.list_property_fragment

import androidx.lifecycle.MutableLiveData
import com.sbizzera.real_estate_manager.data.property.PointOfInterest

class FilterRepository private constructor() {
    companion object {
        val instance by lazy { FilterRepository() }
    }

    val filterLiveData = MutableLiveData(PropertyFilter())
}

data class PropertyFilter(
    val surfaceRange: IntRange? = null,
    val poiMap: Map<PointOfInterest, Boolean> = createPoiMap(),
    val availableSince: String? = null,
    val soldSince: String? = null,
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

