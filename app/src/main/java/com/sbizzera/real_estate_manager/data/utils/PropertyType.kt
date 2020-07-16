package com.sbizzera.real_estate_manager.data.utils

enum class PropertyType (val typeName : String){
    HOUSE("House"),
    FLAT("Flat"),
    FIELD("Field"),
    MANSION("Mansion")
}

fun getTypeNameList():List<String>{
    var listToReturn = mutableListOf<String>()
    PropertyType.values().forEach {
        listToReturn.add(it.typeName)
    }
    return listToReturn
}